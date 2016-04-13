package controllers

import javax.inject.Inject
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.module.service.domain.authentication.AuthenticationService
import com.josip.reactiveluxury.module.service.domain.user.UserDomainService
import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action}
import com.josip.reactiveluxury.core.authentication.Credentials
import com.josip.reactiveluxury.core.jwt.ResponseToken
import scala.concurrent.Future
import com.josip.reactiveluxury.configuration.CustomExecutionContext._

class AuthenticationController @Inject()
(
  private val userDomainService     : UserDomainService,
  private val authenticationService : AuthenticationService
) extends Controller {
  Asserts.argumentIsNotNull(userDomainService)
  Asserts.argumentIsNotNull(authenticationService)

  private final val BAD_EMAIL_OR_PASSWORD_ERROR = "Bad email or password"

  def authenticate = Action.async(parse.json) {
    implicit request =>
      request.body.validate[Credentials].map {
        case credentials: Credentials =>
          authenticationService.authenticate(credentials.email.toLowerCase, credentials.password).map {
            case (Some(token), messages) => Ok(Json.toJson(ResponseTools.of[ResponseToken](token, Some(messages) ).json))
            case (None, messages) =>   Unauthorized(ResponseTools.noData(messages).json)
          }  handleError()
      }.recoverTotal {
        error =>
          Future.successful(BadRequest(ResponseTools.errorToRestResponse(error.errors.flatMap(_._2).map(_.message).head).json))
      }
  }

  def refreshToken = Action.async(parse.json) {
    implicit request =>
      request.body.validate[ResponseToken].map {
        case authenticationToken: ResponseToken =>
          authenticationService.refreshToken(authenticationToken.token).map{
            case Some(token) => Ok(Json.toJson(ResponseTools.data(token)))
            case None => Unauthorized(ResponseTools.errorToRestResponse(BAD_EMAIL_OR_PASSWORD_ERROR).json)
          }   handleError()
      }.recoverTotal {
        error => Future.successful(BadRequest(ResponseTools.errorToRestResponse(error.errors.flatMap(_._2).map(_.message).head).json))
      }
  }
}
