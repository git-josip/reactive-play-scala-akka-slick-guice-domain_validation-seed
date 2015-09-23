package com.josip.reactiveluxury.module.user.service.domain

import javax.inject.{Inject, Singleton}
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.module.user.dao.UserRepository
import com.josip.reactiveluxury.core.{GeneratedId, Asserts}
import com.josip.reactiveluxury.module.user.domain.{UserDetailsEntity, UserCreateEntity}

import scala.concurrent.Future

@Singleton()
class UserDomainServiceImpl @Inject() (
  private val entityRepository: UserRepository
) extends UserDomainService
{
  Asserts.argumentIsNotNull(entityRepository)

  override def create(item: UserCreateEntity): Future[GeneratedId] =
  {
    Asserts.argumentIsNotNull(item)

    this.entityRepository.insert(UserDetailsEntity.of(item))
  }

  override def tryGetById(id: Long): Future[Option[UserDetailsEntity]] =
  {
    Asserts.argumentIsTrue(id > 0)

    this.entityRepository.findById(id)
  }

  override def tryGetByUsername(username: String): Future[Option[UserDetailsEntity]] =
  {
    Asserts.argumentIsNotNull(username)

    this.entityRepository.findByUsername(username)
  }

  override def tryGetByEmail(email: String): Future[Option[UserDetailsEntity]] =
  {
    Asserts.argumentIsNotNull(email)

    this.entityRepository.findByEmail(email)
  }

  override def getAll: Future[List[UserDetailsEntity]] =
  {
    this.entityRepository.findAll
  }
}
