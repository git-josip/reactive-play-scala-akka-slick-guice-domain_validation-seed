package com.josip.reactiveluxury.module.authentication.service

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.core.jwt.ResponseToken
import com.josip.reactiveluxury.module.user.domain.UserDetailsEntity
import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService
{
  def authenticate(username: String, password: String): Future[Option[ResponseToken]]
  def refreshToken(token : String) : Future[Option[ResponseToken]]
  def validateToken(token: String): Future[Boolean]
  def getUserFromToken(token : String) : Future[UserDetailsEntity]
}
