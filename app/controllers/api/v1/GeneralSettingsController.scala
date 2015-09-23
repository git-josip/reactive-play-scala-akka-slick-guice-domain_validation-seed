package controllers.api.v1

import javax.inject.Inject
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.module.authentication.service.AuthenticationService
import com.josip.reactiveluxury.module.generalsettings.service.domain.GeneralSettingsDomainService
import controllers.core.SecuredController
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GeneralSettingsController @Inject()(
  private val generalSettingsDomainService: GeneralSettingsDomainService
)
(
  implicit private val authenticationService: AuthenticationService
) extends SecuredController {
  Asserts.argumentIsNotNull(generalSettingsDomainService)
  Asserts.argumentIsNotNull(authenticationService)

  def read = AuthenticatedAction {
    request =>
      for {
        item <- this.generalSettingsDomainService.getGeneralSettings
        result <- Future.successful(Ok(ResponseTools.data(item).json))
      } yield result
  }
}
