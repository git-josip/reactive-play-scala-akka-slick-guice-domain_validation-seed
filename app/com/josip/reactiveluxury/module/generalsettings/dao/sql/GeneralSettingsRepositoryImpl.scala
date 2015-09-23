package com.josip.reactiveluxury.module.generalsettings.dao.sql

import javax.inject.{Inject, Singleton}
import com.josip.reactiveluxury.configuration.DatabaseProvider
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.module.generalsettings.dao.GeneralSettingsRepository
import com.josip.reactiveluxury.module.generalsettings.dao.sql.mapper.GeneralSettingsMapper
import com.josip.reactiveluxury.module.generalsettings.domain.GeneralSettingsDetailsEntity
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future

@Singleton()
class GeneralSettingsRepositoryImpl@Inject() (databaseProvider: DatabaseProvider) extends GeneralSettingsRepository {
  Asserts.argumentIsNotNull(databaseProvider)

  override def findById(id: Long): Future[Option[GeneralSettingsDetailsEntity]] = {
    Asserts.argumentIsTrue(id >= 0)

    val action = GeneralSettingsMapper.GeneralSettingsDetailsEntityTableDescriptor.query
      .filter(_.id === id).result.headOption

    databaseProvider.db.run(action)
  }
}
