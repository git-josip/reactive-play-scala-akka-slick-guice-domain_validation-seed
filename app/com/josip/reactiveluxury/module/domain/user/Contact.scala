package com.josip.reactiveluxury.module.domain.user

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json

case class Contact (
  address: Option[String],
  city: Option[String],
  zip: Option[String],
  phone: Option[String]
) {
  Asserts.argumentIsNotNull(address)
  Asserts.argumentIsNotNull(city)
  Asserts.argumentIsNotNull(zip)
  Asserts.argumentIsNotNull(phone)
}

object Contact {
  implicit val jsonFormat = Json.format[Contact]
}

