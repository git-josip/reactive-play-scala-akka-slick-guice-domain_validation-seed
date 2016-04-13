package controllers.core

import com.josip.reactiveluxury.core.jwt.{JwtUtil, TokenPayload, JwtSecret}
import com.josip.reactiveluxury.core.utils.StringUtils
import com.josip.reactiveluxury.core.{ValidationResult, Validator, Asserts}
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.module.domain.user.User
import com.josip.reactiveluxury.module.service.domain.authentication.AuthenticationService
import play.api.Logger
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{Format, JsValue}
import play.api.mvc._
import scala.concurrent.Future
import com.josip.reactiveluxury.configuration.CustomExecutionContext._

abstract class SecuredController(
                                  implicit private val authenticationService : AuthenticationService
                                ) extends Controller {
  Asserts.argumentIsNotNull(authenticationService)

  implicit private final val SECRET = JwtSecret(this.authenticationService.authenticationConfiguration().secret)

  private final val INVALID_TOKEN_ERROR = "Invalid authentication token"
  private final val MISSING_TOKEN_ERROR = "Missing authentication token"
  private final val AUTH_TOKEN_NOT_FOUND_ERROR = "Authorization token not found in secured endpoint"
  private final val AUTHORIZATION_TOKEN_VALID_START = "Bearer "

  def AuthenticatedActionWithPayload(block: (Request[AnyContent], TokenPayload) => Future[Result]): Action[AnyContent] = {
    AuthenticatedActionWithPayload(parse.anyContent)(block)
  }

  def AuthenticatedActionWithPayload[A](bodyParser: BodyParser[A])(block: (Request[A], TokenPayload) => Future[Result]): Action[A] = {
    Action.async(bodyParser) {
      request =>
        request.headers.get(AUTHORIZATION).map(token => {
          authenticationService.validateToken(token).flatMap(validationResult =>
            if (!validationResult)
              Future.successful(
                Unauthorized(ResponseTools.errorToRestResponse(INVALID_TOKEN_ERROR).json)
              )
            else for {
              tokenPayload <- JwtUtil.getPayloadIfValidToken[TokenPayload](token.replaceFirst(AUTHORIZATION_TOKEN_VALID_START, StringUtils.EMPTY_STRING))
              response: Result <- block(request, tokenPayload.get) handleError()
            } yield response
          )
        }).getOrElse(
          Future.successful(
            Unauthorized(ResponseTools.errorToRestResponse(MISSING_TOKEN_ERROR).json)
          ))
    }
  }

  def FileUploadAuthenticatedAction(block: Request[MultipartFormData[TemporaryFile]] => Future[Result]): Action[MultipartFormData[TemporaryFile]] = {
    Action.async(parse.multipartFormData) {
      request =>
        request.headers.get(AUTHORIZATION).map(token => {
          authenticationService.validateToken(token).flatMap(validationResult =>
            if (!validationResult)
              Future.successful(
                Unauthorized(ResponseTools.errorToRestResponse(INVALID_TOKEN_ERROR).json)
              )
            else block(request) handleError()
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
        Logger.logger.info(s"MutateJsonAction called with body: '${request.body}'")
        request.body.validate[T].map {
          case item if item.getClass == manifest.runtimeClass =>
            Logger.logger.info(s"MutateJsonAction called with model: '${item.toString}'")
            validator.validate(item, None).flatMap(
              validationResult =>
                validationResult.isValid.flatMap(valid => {
                  if(valid) {
                    mutateBlock(request, validationResult) handleError()
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

  def MutateJsonAuthenticatedActionWithUser[T: Format: Manifest](validator: Validator[T])(mutateBlock: (Request[JsValue], ValidationResult[T], User) => Future[Result]): Action[JsValue] = {
    AuthenticatedActionWithPayload(parse.json) {
      (request, tokenPayload) =>
        Logger.logger.info(s"MutateJsonAuthenticatedActionWithUser called with body: '${request.body}'")
        request.body.validate[T].map {
          case item if item.getClass == manifest.runtimeClass =>
            Logger.logger.info(s"MutateJsonAuthenticatedActionWithUser called with model: '${item.toString}'")
            for {
              requestUser <- userFromSecuredRequest(request)
              validationResult <- validator.validate(item, requestUser.id)
              response: Result <- {
                validationResult.isValid.flatMap(valid => {
                  if(valid) {
                    mutateBlock(request, validationResult, requestUser) handleError()
                  } else {
                    validationResult.errorsRestResponse.map(error => BadRequest(error.json))
                  }
                })
              }
            }  yield response
        }.recoverTotal {
          error =>
            Future.successful(BadRequest(ResponseTools.jsErrorToRestResponse[T](error).json))
        }
    }
  }

  def MutateJsonAuthenticatedActionWithPayload[T: Format: Manifest](validator: Validator[T])(mutateBlock: (Request[JsValue], ValidationResult[T], TokenPayload) => Future[Result]): Action[JsValue] = {
    AuthenticatedActionWithPayload(parse.json) {
      (request, tokenPayload)  =>
        Logger.logger.info(s"MutateJsonAuthenticatedActionWithPayload called with body: '${request.body}'")
        request.body.validate[T].map {
          case item if item.getClass == manifest.runtimeClass =>
            Logger.logger.info(s"MutateJsonAuthenticatedActionWithPayload called with model: '${item.toString}'")
            for {
              validationResult <- validator.validate(item, Some(tokenPayload.userId))
              response: Result <- {
                validationResult.isValid.flatMap(valid => {
                  if(valid) {
                    mutateBlock(request, validationResult, tokenPayload) handleError()
                  } else {
                    validationResult.errorsRestResponse.map(error => BadRequest(error.json))
                  }
                })
              }
            }  yield response
        }.recoverTotal {
          error =>
            Future.successful(BadRequest(ResponseTools.jsErrorToRestResponse[T](error).json))
        }
    }
  }

  implicit def userFromSecuredRequest(implicit request : Request[_]) : Future[User] = {
    Asserts.argumentIsNotNull(request)

    val token = request.headers.get(AUTHORIZATION)
      .getOrElse(throw new IllegalStateException(AUTH_TOKEN_NOT_FOUND_ERROR))

    this.authenticationService.getUserFromToken(token)
  }
}