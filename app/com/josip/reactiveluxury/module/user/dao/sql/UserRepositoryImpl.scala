package com.josip.reactiveluxury.module.user.dao.sql

import javax.inject.{Inject, Singleton}
import com.josip.reactiveluxury.configuration.DatabaseProvider
import com.josip.reactiveluxury.module.user.dao.UserRepository
import com.josip.reactiveluxury.module.user.dao.sql.mapper.UserEntityMapper._
import com.josip.reactiveluxury.module.user.domain.{UserDetailsEntity, UserCreateEntity}
import com.josip.reactiveluxury.core.{Asserts, GeneratedId}
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton()
class UserRepositoryImpl @Inject() (databaseProvider: DatabaseProvider) extends UserRepository {
  Asserts.argumentIsNotNull(databaseProvider)

  override def insert(item: UserDetailsEntity): Future[GeneratedId] = {
    Asserts.argumentIsNotNull(item)

    val action = (UserDetailsTableDescriptor.queryWithId += item).transactionally
    databaseProvider.db.run(action).map(id => GeneratedId(id))
  }

  override def findById(id: Long): Future[Option[UserDetailsEntity]] = {
    Asserts.argumentIsTrue(id > 0)

    val action = UserDetailsTableDescriptor.query.filter(_.id === id).result.headOption
    databaseProvider.db.run(action)
  }

  override def findByUsername(username: String): Future[Option[UserDetailsEntity]] = {
    Asserts.argumentIsNotNull(username)

    val action = UserDetailsTableDescriptor.query.filter(_.username === username).result.headOption
    databaseProvider.db.run(action)
  }

  override def findByEmail(email: String): Future[Option[UserDetailsEntity]] = {
    Asserts.argumentIsNotNullNorEmpty(email)

    val action = UserDetailsTableDescriptor.query.filter(_.email === email).result.headOption
    databaseProvider.db.run(action)
  }

  override def findAll: Future[List[UserDetailsEntity]] = {
    val action = UserDetailsTableDescriptor.query.result
    databaseProvider.db.run(action).map(_.toList)
  }
}
