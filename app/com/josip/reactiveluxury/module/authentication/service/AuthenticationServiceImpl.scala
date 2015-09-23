package com.josip.reactiveluxury.module.authentication.service

import javax.inject.{Inject, Singleton}
import com.josip.reactiveluxury.configuration.AuthenticationConfigurationSetup
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.jwt.{JwtSecret, ResponseToken, JwtUtil, TokenPayload}
import com.josip.reactiveluxury.core.utils.{HashUtils, DateUtils}
import com.josip.reactiveluxury.module.user.domain.UserDetailsEntity
import com.josip.reactiveluxury.module.user.service.domain.UserDomainService
import com.josip.reactiveluxury.core.utils.HashUtils
import com.josip.reactiveluxury.core.jwt.TokenPayload
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class AuthenticationServiceImpl @Inject()(
  authenticationConfiguration : AuthenticationConfigurationSetup,
  userDomainService           : UserDomainService
) extends AuthenticationService {
  Asserts.argumentIsNotNull(authenticationConfiguration)
  Asserts.argumentIsNotNull(userDomainService)

  implicit private final val SECRET = JwtSecret(this.authenticationConfiguration.secret)

  override def authenticate(username: String, password: String): Future[Option[ResponseToken]] = {
    Asserts.argumentIsNotNull(username)
    Asserts.argumentIsNotNull(password)

    this.userDomainService.tryGetByUsername(username).filter {
      case Some(u) => u.password == HashUtils.sha1(password)
      case None     => false
    }.map {
      case Some(u) => Some(createAuthenticationToken(u))
      case None => None
    }
  }

  override def validateToken(token: String): Future[Boolean] = {
    Asserts.argumentIsNotNull(token)

    JwtUtil.getPayloadIfValidToken[TokenPayload](token).map {
      case Some(tp) => tp.expiration.isAfterNow
      case None     => false
    }
  }

  override def refreshToken(token : String) : Future[Option[ResponseToken]] = {
    Asserts.argumentIsNotNullNorEmpty(token)

    JwtUtil.getPayloadIfValidToken[TokenPayload](token).flatMap{
      case Some(tp) => this.userDomainService.getById(tp.userId).map(u => Some(createAuthenticationToken(u)))
      case None => Future.successful(None)
    }
  }

  private def createAuthenticationToken(user : UserDetailsEntity) : ResponseToken = {
    Asserts.argumentIsNotNull(user)

    val tokenPayload = TokenPayload(
      userId      = user.id,
      username    = user.username,
      expiration  = DateUtils.nowDateTimeUTC.plusHours(this.authenticationConfiguration.tokenHoursToLive))

    ResponseToken(JwtUtil.signJwtPayload(tokenPayload))
  }

  def getUserFromToken(token : String) : Future[UserDetailsEntity] = {
    Asserts.argumentIsNotNullNorEmpty(token)

    JwtUtil.getPayloadIfValidToken[TokenPayload](token).flatMap{
      case Some(tp) => this.userDomainService.getById(tp.userId)
      case None => throw new IllegalStateException("TokenPayload must exist at this point.")
    }
  }
}
