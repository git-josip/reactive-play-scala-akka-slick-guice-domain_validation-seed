package com.josip.reactiveluxury.module.service.domain.user

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.service.EntityServiceImpl
import com.josip.reactiveluxury.module.dao.user.UserRepository
import com.josip.reactiveluxury.module.domain.user.User

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class UserDomainServiceImpl @Inject() (
  override val entityRepository: UserRepository,
  actorSystem: ActorSystem,
  implicit override val ec : ExecutionContext
) extends EntityServiceImpl[User, UserRepository](entityRepository, actorSystem) with UserDomainService {
  Asserts.argumentIsNotNull(entityRepository)

  override def tryGetByEmail(email: String): Future[Option[User]] = {
    Asserts.argumentIsNotNull(email)

    this.entityRepository.findByEmail(email)
  }

  override def verify(userId: Long) {
    this.entityRepository.verify(userId)
  }

  override  def tryGetByIdMinimalDetails(id: Long): Future[Option[UserRepository.UserMinimalDetails]] = {
    this.entityRepository.findByIdMinimalDetails(id)
  }

  override  def tryGetByEmailMinimalDetails(email: String): Future[Option[UserRepository.UserMinimalDetails]] = {
    Asserts.argumentIsNotNull(email)

    this.entityRepository.findByEmailMinimalDetails(email)
  }

  override  def tryGetByExternalIdMinimalDetails(externalId: String): Future[Option[UserRepository.UserMinimalDetails]] = {
    Asserts.argumentIsNotNull(externalId)

    this.entityRepository.findByExternalIdMinimalDetails(externalId)
  }
}
