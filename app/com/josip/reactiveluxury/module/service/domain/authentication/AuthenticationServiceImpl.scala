package com.josip.reactiveluxury.module.service.domain.authentication

import javax.inject.{Inject, Singleton}

import com.josip.reactiveluxury.configuration.AuthenticationConfigurationSetup
import com.josip.reactiveluxury.configuration.CustomExecutionContext._
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.jwt._
import com.josip.reactiveluxury.core.messages.Messages
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.core.utils.{DateUtils, HashUtils, StringUtils}
import com.josip.reactiveluxury.module.dao.user.UserRepository
import com.josip.reactiveluxury.module.domain.user.User
import com.josip.reactiveluxury.module.service.domain.user.UserDomainService
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.Future

@Singleton()
class AuthenticationServiceImpl @Inject()
(
  override val authenticationConfiguration  : AuthenticationConfigurationSetup,
  val userDomainService                     : UserDomainService
) extends AuthenticationService {
  Asserts.argumentIsNotNull(authenticationConfiguration)
  Asserts.argumentIsNotNull(userDomainService)

  implicit private final val SECRET = JwtSecret(this.authenticationConfiguration.secret)

  private final val AUTHORIZATION_TOKEN_VALID_START = "Bearer "

  override def authenticate(email: String, password: String): Future[(Option[ResponseToken], Messages)] = {
    Asserts.argumentIsNotNull(email)
    Asserts.argumentIsNotNull(password)

    for {
      user <- this.userDomainService.tryGetByEmailMinimalDetails(email) handleError()
      result <- {
        isUserValidForAuthentication(user, email, password) match {
          case true =>
            Future.successful(Some(createAuthenticationToken(user.get)), Messages.of)
          case false =>
            val messages = Messages.of
            if(user.isDefined) {
              if(!user.get.status.isVerified) {
                messages.putWarning(ResponseTools.GLOBAL_MESSAGE_KEY, "User is not verified.")
              } else {
                messages.putError(ResponseTools.GLOBAL_MESSAGE_KEY, "Bad email or password")
              }
            } else {
              messages.putWarning(ResponseTools.GLOBAL_MESSAGE_KEY, "Bad email or password")
            }

            Future.successful((None, messages))
        }
      }
    } yield result
  }

  override def validateToken(token: String): Future[Boolean] = {
    Asserts.argumentIsNotNull(token)

    if(token.startsWith(AUTHORIZATION_TOKEN_VALID_START)) {
      JwtUtil.getPayloadIfValidToken[TokenPayload](token.replaceFirst(AUTHORIZATION_TOKEN_VALID_START, StringUtils.EMPTY_STRING)).map {
        case Some(tp) => tp.expiration.isAfterNow
        case None     => false
      }
    } else {
      Future.successful(false)
    }
  }

  override def refreshToken(token : String) : Future[Option[ResponseToken]] = {
    Asserts.argumentIsNotNullNorEmpty(token)

    JwtUtil.getPayloadIfValidToken[TokenPayload](token).flatMap{
      case Some(tp) =>
        for {
          user <- this.userDomainService.tryGetByIdMinimalDetails(tp.userId) handleError()
        } yield Some(createAuthenticationToken(user.get))
      case None =>
        Future.successful(None)
    }
  }

  def getUserFromToken(token : String) : Future[User] = {
    Asserts.argumentIsNotNullNorEmpty(token)

    for {
      payloadCandidate <- JwtUtil.getPayloadIfValidToken[TokenPayload](token.replaceFirst(AUTHORIZATION_TOKEN_VALID_START, StringUtils.EMPTY_STRING)) handleError()
      user <- {
        payloadCandidate match {
          case Some(payload) => this.userDomainService.getById(payload.userId)
          case None => throw new IllegalStateException(s"TokenPayload must exist at this point. Token: '$token'")
        }
      }
    } yield user
  }

  private def createAuthenticationToken(user : UserRepository.UserMinimalDetails) : ResponseToken = {
    Asserts.argumentIsNotNull(user)

    val tokenPayload = TokenPayload(
      userId      = user.id,
      email    = s"${user.email}",
      expiration  = DateUtils.nowDateTimeUTC.plusHours(this.authenticationConfiguration.tokenHoursToLive)
    )

    ResponseToken(JwtUtil.signJwtPayload(tokenPayload))
  }

  private def isUserValidForAuthentication(user: Option[UserRepository.UserMinimalDetails], email: String, password: String): Boolean = {
    val messages = Messages.of

    if(user.isEmpty) {
      messages.putError(s"No existing user with provided credentials. email: '$email'")
    }

    if(!messages.hasErrors()) {
      if(!user.get.password.equals(HashUtils.sha1(password))) {
        messages.putError(s"Password do not match. email: '$email'")
      }

      if(!user.get.status.isVerified) {
        messages.putError(s"User is not verified. user: '${Json.toJson(user.get)}'")
      }

      if(user.get.role.isSystemUser) {
        messages.putError(s"User is system user. user: '${Json.toJson(user.get)}'")
      }
    }

    if(messages.hasErrors()) {
      Logger.logger.info(s"Authentication failed. Reason: '${messages.errors().map(_.text).mkString(StringUtils.COMMA_SEPARATOR)}'")
    }

    !messages.hasErrors()
  }
}
