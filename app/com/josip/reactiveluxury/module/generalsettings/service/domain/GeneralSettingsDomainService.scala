package com.josip.reactiveluxury.module.generalsettings.service.domain

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.module.generalsettings.domain.GeneralSettingsDetailsEntity

import scala.concurrent.Future

@ImplementedBy(classOf[GeneralSettingsDomainServiceImpl])
trait GeneralSettingsDomainService {
  def getGeneralSettings: Future[GeneralSettingsDetailsEntity]
}
