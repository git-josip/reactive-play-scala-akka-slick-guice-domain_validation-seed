package controllers

import javax.inject.Inject
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.module.user.service.domain.UserDomainService
import com.josip.reactiveluxury.module.authentication.service.AuthenticationService
import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action}
import com.josip.reactiveluxury.core.authentication.Credentials
import com.josip.reactiveluxury.core.jwt.ResponseToken
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationController @Inject() (
  private val userDomainService     : UserDomainService,
  private val authenticationService : AuthenticationService
) extends Controller {
  Asserts.argumentIsNotNull(userDomainService)
  Asserts.argumentIsNotNull(authenticationService)

  private final val BAD_USERNAME_OR_PASSWORD_ERROR = "Bad username or password"

  def authenticate = Action.async(parse.json) {
    implicit request =>
      request.body.validate[Credentials].map {
        case credentials: Credentials =>
          authenticationService.authenticate(credentials.username, credentials.password).map {
            case Some(token) => Ok(Json.toJson(ResponseTools.data(token)))
            case None =>   Unauthorized(ResponseTools.errorToRestResponse(BAD_USERNAME_OR_PASSWORD_ERROR).json)
          }
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
            case None => Unauthorized(ResponseTools.errorToRestResponse(BAD_USERNAME_OR_PASSWORD_ERROR).json)
          }
      }.recoverTotal {
        error => Future.successful(BadRequest(ResponseTools.errorToRestResponse(error.errors.flatMap(_._2).map(_.message).head).json))
      }
  }
}
