package com.josip.reactiveluxury.module.user.dao

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.module.user.dao.sql.UserRepositoryImpl
import com.josip.reactiveluxury.core.GeneratedId
import com.josip.reactiveluxury.module.user.dao.sql.UserRepositoryImpl
import com.josip.reactiveluxury.module.user.domain.UserDetailsEntity
import scala.concurrent.Future

@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository
{
  def insert(item: UserDetailsEntity): Future[GeneratedId]

  def findById(id: Long): Future[Option[UserDetailsEntity]]
  def findByUsername(username: String): Future[Option[UserDetailsEntity]]
  def findByEmail(email: String): Future[Option[UserDetailsEntity]]
  def findAll: Future[List[UserDetailsEntity]]
}
