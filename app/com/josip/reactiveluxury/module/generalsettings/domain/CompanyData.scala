package com.josip.reactiveluxury.module.generalsettings.domain

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json

case class CompanyData
(
  country : String,
  street  : String,
  city    : String,
  zip     : String,
  phone   : String,
  mobile  : String,
  oib     : String
)
{
  Asserts.argumentIsNotNull(country)
  Asserts.argumentIsNotNull(street)
  Asserts.argumentIsNotNull(city)
  Asserts.argumentIsNotNull(zip)
  Asserts.argumentIsNotNull(phone)
  Asserts.argumentIsNotNull(mobile)
  Asserts.argumentIsNotNull(oib)
}

object CompanyData
{
  implicit val jsonFormat = Json.format[CompanyData]
}
