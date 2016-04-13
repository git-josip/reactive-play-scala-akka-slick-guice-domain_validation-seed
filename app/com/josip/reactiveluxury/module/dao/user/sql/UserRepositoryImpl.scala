package com.josip.reactiveluxury.module.dao.user.sql

import javax.inject.{Inject, Singleton}

import com.josip.reactiveluxury.configuration.DatabaseProvider
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.slick.generic.CrudRepositoryImpl
import com.josip.reactiveluxury.module.dao.user.UserRepository
import com.josip.reactiveluxury.module.dao.user.sql.UserEntityMapper._
import com.josip.reactiveluxury.module.domain.user.{UserStatus, User}
import com.josip.reactiveluxury.core.slick.postgres.MyPostgresDriver.api._
import com.josip.reactiveluxury.configuration.CustomExecutionContext._
import scala.concurrent.Future

@Singleton()
class UserRepositoryImpl @Inject() (databaseProvider: DatabaseProvider) extends CrudRepositoryImpl[UserTableDescriptor, User](databaseProvider, UserTableDescriptor.user) with UserRepository {
  Asserts.argumentIsNotNull(databaseProvider)

  override def findByEmail(email: String): Future[Option[User]] = {
    Asserts.argumentIsNotNullNorEmpty(email)

    val action = UserTableDescriptor.user.filter(_.email === email).result.headOption
    databaseProvider.db.run(action)
  }

  override def verify(userId: Long) {
    val action = this.query.filter(_.id === userId)
      .map(_.status)
      .update(UserStatus.VERIFIED)
      .transactionally

    this.databaseProvider.db.run(action)
  }

  override def findByIdMinimalDetails(id: Long): Future[Option[UserRepository.UserMinimalDetails]] = {
    val selectQuery =
      sql"""
           SELECT id, external_id, email, role, password, status
           FROM public.user
           WHERE id = $id
        """.as[UserRepository.UserMinimalDetails]

    for {
      item <- databaseProvider.db.run(selectQuery.headOption) handleError()
    } yield item
  }

  override def findByEmailMinimalDetails(email: String): Future[Option[UserRepository.UserMinimalDetails]] = {
    Asserts.argumentIsNotNull(email)

    val selectQuery =
      sql"""
           SELECT id, external_id, email, role, password, status
           FROM public.user
           WHERE email = $email
        """.as[UserRepository.UserMinimalDetails]

    for {
      item <- databaseProvider.db.run(selectQuery.headOption) handleError()
    } yield item
  }

  override def findByExternalIdMinimalDetails(externalId: String): Future[Option[UserRepository.UserMinimalDetails]] = {
    Asserts.argumentIsNotNull(externalId)

    val selectQuery =
      sql"""
           SELECT id, external_id, email, role, password, status
           FROM public.user
           WHERE external_id = $externalId
        """.as[UserRepository.UserMinimalDetails]

    for {
      item <- databaseProvider.db.run(selectQuery.headOption) handleError()
    } yield item
  }
}
