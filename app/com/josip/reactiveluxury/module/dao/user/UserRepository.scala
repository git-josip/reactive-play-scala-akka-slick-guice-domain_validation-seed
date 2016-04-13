package com.josip.reactiveluxury.module.dao.user

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.json.customfomatters.CustomFormatter.Enum
import com.josip.reactiveluxury.core.slick.generic.CrudRepository
import com.josip.reactiveluxury.module.dao.user.sql.UserRepositoryImpl
import com.josip.reactiveluxury.module.domain.user.{UserStatus, UserRole, User}
import play.api.libs.json.Json
import slick.jdbc.GetResult

import scala.concurrent.Future

@ImplementedBy(classOf[UserRepositoryImpl])
trait UserRepository extends CrudRepository[User] {
  def findByEmail(email: String): Future[Option[User]]
  def verify(userId: Long)

  def findByIdMinimalDetails(id: Long): Future[Option[UserRepository.UserMinimalDetails]]
  def findByEmailMinimalDetails(email: String): Future[Option[UserRepository.UserMinimalDetails]]
  def findByExternalIdMinimalDetails(externalId: String): Future[Option[UserRepository.UserMinimalDetails]]
}

object UserRepository {
  case class UserMinimalDetails
  (
    id          : Long,
    externalId  : String,
    email       : String,
    role        : UserRole,
    password    : String,
    status      : UserStatus
  ) {
    Asserts.argumentIsNotNull(externalId)
    Asserts.argumentIsNotNull(email)
  }

  object UserMinimalDetails {
    import Enum.enumFormatByName

    implicit val jsonFormat = Json.format[UserMinimalDetails]

    implicit val getResult = GetResult(
      r => UserMinimalDetails(
        id          = r.nextLong(),
        externalId  = r.nextString(),
        email       = r.nextString(),
        role        = UserRole.valueOf(r.nextString()),
        password    = r.nextString(),
        status      = UserStatus.valueOf(r.nextString())
      )
    )
  }
}
