package controllers.core

import com.josip.reactiveluxury.core.{ValidationResult, Validator, Asserts}
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.module.authentication.service.AuthenticationService
import com.josip.reactiveluxury.module.user.domain.UserDetailsEntity
import play.api.libs.json.{Format, JsValue}
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class SecuredController(
  implicit private val authenticationService : AuthenticationService
) extends Controller {
  Asserts.argumentIsNotNull(authenticationService)

  private final val INVALID_TOKEN_ERROR = "Invalid authentication token"
  private final val MISSING_TOKEN_ERROR = "Missing authentication token"
  private final val AUTH_TOKEN_NOT_FOUND_ERROR = "Authorization token not found in secured endpoint"

  def AuthenticatedAction(block: Request[AnyContent] => Future[Result]): Action[AnyContent] = {
    AuthenticatedAction(parse.anyContent)(block)
  }

  def AuthenticatedAction[A](bodyParser: BodyParser[A])(block: Request[A] => Future[Result]): Action[A] = {
    Action.async(bodyParser) {
      request =>
        request.headers.get(AUTHORIZATION).map(token => {
          authenticationService.validateToken(token).flatMap(validationResult =>
            if (!validationResult)
              Future.successful(
                Unauthorized(ResponseTools.errorToRestResponse(INVALID_TOKEN_ERROR).json)
              )
            else block(request)
          )
        }).getOrElse(
            Future.successful(
              Unauthorized(ResponseTools.errorToRestResponse(MISSING_TOKEN_ERROR).json)
          ))
    }
  }

  def MutateJsonAction[T: Format: Manifest](validator: Validator[T])(mutateBlock: (Request[JsValue], ValidationResult[T]) => Future[Result]): Action[JsValue] = {
    Action.async(parse.json) {
      request =>
        request.body.validate[T].map {
          case item if item.getClass == manifest.runtimeClass =>
            validator.validate(item).flatMap(
              validationResult =>
                validationResult.isValid.flatMap(valid => {
                  if(valid) {
                    mutateBlock(request, validationResult)
                  } else {
                    validationResult.errorsRestResponse.map(error => BadRequest(error.json))
                  }
                })
            )}.recoverTotal {
          error =>
            Future.successful(
              BadRequest(ResponseTools.jsErrorToRestResponse[T](error).json)
            )
        }
    }
  }

  def MutateJsonAuthenticatedAction[T: Format: Manifest](validator: Validator[T])(mutateBlock: (Request[JsValue], ValidationResult[T], UserDetailsEntity) => Future[Result]): Action[JsValue] = {
    AuthenticatedAction(parse.json) {
      request =>
        request.body.validate[T].map {
          case item if item.getClass == manifest.runtimeClass =>
            validator.validate(item).flatMap(validationResult => {
              for {
                requestUser <- userFromSecuredRequest(request)
                response: Result <- {
                  validationResult.isValid.flatMap(valid => {
                    if(valid) {
                      mutateBlock(request, validationResult, requestUser)
                    } else {
                      validationResult.errorsRestResponse.map(error => BadRequest(error.json))
                    }
                  })
                }
              }  yield response
            })
        }.recoverTotal {
          error =>
            Future.successful(BadRequest(ResponseTools.jsErrorToRestResponse[T](error).json))
        }
    }
  }

  implicit def userFromSecuredRequest(implicit request : Request[_]) : Future[UserDetailsEntity] = {
    Asserts.argumentIsNotNull(request)

    val token = request.headers.get(AUTHORIZATION)
      .getOrElse(throw new IllegalStateException(AUTH_TOKEN_NOT_FOUND_ERROR))

    this.authenticationService.getUserFromToken(token)
  }
}
