package com.josip.reactiveluxury.module.generalsettings.dao

import com.google.inject.ImplementedBy
import com.josip.reactiveluxury.module.generalsettings.dao.sql.GeneralSettingsRepositoryImpl
import com.josip.reactiveluxury.module.generalsettings.domain.GeneralSettingsDetailsEntity

import scala.concurrent.Future

@ImplementedBy(classOf[GeneralSettingsRepositoryImpl])
trait GeneralSettingsRepository
{
  def findById(id: Long): Future[Option[GeneralSettingsDetailsEntity]]
}
