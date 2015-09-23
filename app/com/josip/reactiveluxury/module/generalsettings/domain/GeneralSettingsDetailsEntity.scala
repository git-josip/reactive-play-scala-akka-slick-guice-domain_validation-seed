package com.josip.reactiveluxury.module.generalsettings.domain

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json

case class GeneralSettingsDetailsEntity(
  id          : Long,
  companyData : CompanyData,
  businessData: BusinessData
)
{
  Asserts.argumentIsNotNull(companyData)
  Asserts.argumentIsNotNull(businessData)
}

object GeneralSettingsDetailsEntity {
  final val SINGLETON_ID = 0

  implicit val jsonFormat = Json.format[GeneralSettingsDetailsEntity]
}