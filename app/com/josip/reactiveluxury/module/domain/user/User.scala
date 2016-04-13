package com.josip.reactiveluxury.module.domain.user

import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.slick.generic.Entity
import com.josip.reactiveluxury.core.utils.DateUtils
import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json.Json

case class User
(
  override val id         : Option[Long] = Option.empty,
  override val externalId : Option[String] = Option.empty,
  override val createdOn  : Option[DateTime] = Some(DateUtils.nowDateTimeUTC),
  override val modifiedOn : Option[DateTime] = Some(DateUtils.nowDateTimeUTC),
  status          : UserStatus,
  role            : UserRole,
  firstName       : Option[String],
  lastName        : Option[String],
  email           : String,
  password        : String,
  dateOfBirth     : LocalDate,
  gender          : Gender,
  contact         : Contact,
  height          : Option[Int],
  weight          : Option[Int]
) extends Entity[User] {
  selfReference =>

  Asserts.argumentIsNotNull(id)
  Asserts.argumentIsNotNull(externalId)
  Asserts.argumentIsNotNull(createdOn)
  Asserts.argumentIsNotNull(modifiedOn)
  Asserts.argumentIsNotNull(status)
  Asserts.argumentIsNotNull(role)
  Asserts.argumentIsNotNull(firstName)
  Asserts.argumentIsNotNull(lastName)
  Asserts.argumentIsNotNull(email)
  Asserts.argumentIsNotNull(password)
  Asserts.argumentIsNotNull(dateOfBirth)
  Asserts.argumentIsNotNull(gender)
  Asserts.argumentIsNotNull(contact)
  Asserts.argumentIsNotNull(height)
  Asserts.argumentIsNotNull(weight)

  lazy val withoutPassword = selfReference.copy(password = "n/a")

  lazy val displayName = if(firstName.isDefined & lastName.isDefined) {
    s"${firstName.get} ${lastName.get}"
  } else {
    email
  }
}

object User {

  import com.josip.reactiveluxury.core.json.customfomatters.CustomFormatter.Joda._
  import com.josip.reactiveluxury.core.json.customfomatters.CustomFormatter.Enum._

  implicit val jsonFormat = Json.format[User]

  def of(item: UserCreateModel) = {
    Asserts.argumentIsNotNull(item)

    val now = DateUtils.nowDateTimeUTC

    User(
      createdOn = Some(now),
      modifiedOn=  Some(now),
      status = UserStatus.NOT_VERIFIED,
      role = item.role,
      firstName = Some(item.firstName),
      lastName = Some(item.lastName),
      email = item.email,
      password = item.password,
      dateOfBirth = item.dateOfBirth,
      gender = item.gender,
      contact = item.contact,
      height = item.height,
      weight = item.weight
    )
  }
}