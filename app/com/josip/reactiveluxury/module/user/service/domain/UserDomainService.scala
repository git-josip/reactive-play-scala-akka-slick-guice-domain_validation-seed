package com.josip.reactiveluxury.module.user.service.domain

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.module.user.domain.{UserDetailsEntity, UserCreateEntity}
import com.josip.reactiveluxury.core.{Asserts, GeneratedId}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[UserDomainServiceImpl])
trait UserDomainService
{
  def create(item: UserCreateEntity): Future[GeneratedId]

  def tryGetById(id: Long): Future[Option[UserDetailsEntity]]
  def tryGetByUsername(username: String): Future[Option[UserDetailsEntity]]
  def tryGetByEmail(email: String): Future[Option[UserDetailsEntity]]
  def getAll: Future[List[UserDetailsEntity]]

  def getById(id: Long): Future[UserDetailsEntity] = {
    Asserts.argumentIsNotNull(id)

    this.tryGetById(id)
      .map(_.getOrElse(throw new RuntimeException("user with this id does not exist"))
    )
  }
  def getByUsername(username: String): Future[UserDetailsEntity] = {
    Asserts.argumentIsNotNull(username)

    this.tryGetByUsername(username)
      .map(_.getOrElse(throw new RuntimeException("user with this username does not exist")))
  }

  def doesExistByUsername(userName: String): Future[Boolean] = {
    Asserts.argumentIsNotNull(userName)

    this.tryGetByUsername(userName).map(_.isDefined)
  }

  def doesExistByByEmail(email: String): Future[Boolean] = {
    Asserts.argumentIsNotNull(email)

    this.tryGetByEmail(email).map(_.isDefined)
  }
}
