package com.josip.reactiveluxury.module.service.domain.authentication

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.configuration.AuthenticationConfigurationSetup
import com.josip.reactiveluxury.core.jwt.ResponseToken
import com.josip.reactiveluxury.core.messages.Messages
import com.josip.reactiveluxury.module.domain.user.User

import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {
  def authenticate(email: String, password: String): Future[(Option[ResponseToken], Messages)]
  def refreshToken(token : String) : Future[Option[ResponseToken]]
  def validateToken(token: String): Future[Boolean]
  def getUserFromToken(token : String) : Future[User]

  def authenticationConfiguration(): AuthenticationConfigurationSetup
}
