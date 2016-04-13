package com.josip.reactiveluxury.module.service.domain.user

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.configuration.CustomExecutionContext.customForkJoinPoolContext
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.service.EntityService
import com.josip.reactiveluxury.module.dao.user.UserRepository
import com.josip.reactiveluxury.module.domain.user.User

import scala.concurrent.Future

@ImplementedBy(classOf[UserDomainServiceImpl])
trait UserDomainService extends EntityService[User] {
  def tryGetByEmail(email: String): Future[Option[User]]

  def doesExistByByEmail(email: String): Future[Boolean] = {
    Asserts.argumentIsNotNull(email)

    this.tryGetByEmail(email).map(_.isDefined)
  }

  def verify(userId: Long)

  def tryGetByIdMinimalDetails(id: Long): Future[Option[UserRepository.UserMinimalDetails]]
  def tryGetByEmailMinimalDetails(email: String): Future[Option[UserRepository.UserMinimalDetails]]
  def tryGetByExternalIdMinimalDetails(externalId: String): Future[Option[UserRepository.UserMinimalDetails]]
}
