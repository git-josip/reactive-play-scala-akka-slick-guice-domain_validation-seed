package com.josip.reactiveluxury.module.domain.user

import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.messages.MessageKey
import org.joda.time.LocalDate
import play.api.libs.json.Json

case class UserCreateModel(
  firstName     : String,
  lastName      : String,
  email         : String,
  password      : String,
  dateOfBirth   : LocalDate,
  gender        : Gender,
  contact       : Contact,
  height        : Option[Int],
  weight        : Option[Int]
) {
  Asserts.argumentIsNotNull(firstName)
  Asserts.argumentIsNotNull(lastName)
  Asserts.argumentIsNotNull(email)
  Asserts.argumentIsNotNull(password)
  Asserts.argumentIsNotNull(dateOfBirth)
  Asserts.argumentIsNotNull(gender)
  Asserts.argumentIsNotNull(contact)
  Asserts.argumentIsNotNull(height)
  Asserts.argumentIsNotNull(weight)

  val role = UserRole.COMPETITOR
}

object UserCreateModel {

  import com.josip.reactiveluxury.core.json.customfomatters.CustomFormatter.Joda._
  import com.josip.reactiveluxury.core.json.customfomatters.CustomFormatter.Enum._

  implicit val jsonFormat = Json.format[UserCreateModel]

  val FIRST_NAME_FORM_ID      = MessageKey("firstName")
  val LAST_NAME_FORM_ID       = MessageKey("lastName")
  val EMAIL_FORM_ID           = MessageKey("email")
  val PASSWORD_FORM_ID        = MessageKey("password")
  val DATE_OF_BIRTH_FORM_ID   = MessageKey("dateOfBirth")
  val GENDER_FORM_ID          = MessageKey("gender")
  val CONTACT_FORM_ID         = MessageKey("contact")
  val HEIGHT_FORM_ID          = MessageKey("height")
  val WEIGHT_FORM_ID          = MessageKey("weight")
}
