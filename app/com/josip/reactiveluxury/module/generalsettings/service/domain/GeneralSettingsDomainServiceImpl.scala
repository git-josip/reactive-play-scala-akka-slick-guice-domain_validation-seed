package com.josip.reactiveluxury.module.generalsettings.service.domain

import javax.inject.{Singleton, Inject}
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.module.generalsettings.dao.GeneralSettingsRepository
import com.josip.reactiveluxury.module.generalsettings.domain.GeneralSettingsDetailsEntity
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class GeneralSettingsDomainServiceImpl @Inject()(
  private final val entityRepository: GeneralSettingsRepository
) extends GeneralSettingsDomainService {
  Asserts.argumentIsNotNull(entityRepository)

  def getGeneralSettings: Future[GeneralSettingsDetailsEntity] = {
    this.entityRepository.findById(GeneralSettingsDetailsEntity.SINGLETON_ID).map(
      _.getOrElse(throw new RuntimeException(s"GeneralSettings with this id: ${GeneralSettingsDetailsEntity.SINGLETON_ID} does not exist"))
    )
  }
}
