package com.josip.reactiveluxury.module.user.domain

import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json

case class UserDetailsEntity
(
  id        : Long,
  firstName : String,
  lastName  : String,
  username  : String,
  email     : String,
  password  : String
)
{
  selfReference =>
  Asserts.argumentIsNotNull(firstName)
  Asserts.argumentIsNotNull(lastName)
  Asserts.argumentIsNotNull(email)
  Asserts.argumentIsNotNull(username)
  Asserts.argumentIsNotNull(password)

  lazy val withoutPassword = selfReference.copy(password = "n/a")
}

object UserDetailsEntity
{
  implicit val jsonFormat = Json.format[UserDetailsEntity]

  def of(item: UserCreateEntity) = {
    Asserts.argumentIsNotNull(item)

    UserDetailsEntity(
      id = 0,
      firstName = item.firstName,
      lastName = item.lastName,
      username = item.username,
      email = item.email,
      password = item.password
    )
  }
}