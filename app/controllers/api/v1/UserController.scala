package controllers.api.v1

import javax.inject.Inject
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.core.utils.HashUtils
import com.josip.reactiveluxury.module.domain.user.{User, UserCreateModel}
import com.josip.reactiveluxury.module.service.domain.authentication.AuthenticationService
import com.josip.reactiveluxury.module.service.domain.user.UserDomainService
import com.josip.reactiveluxury.module.validation.user.UserCreateValidator
import controllers.core.SecuredController
import scala.concurrent.Future
import com.josip.reactiveluxury.configuration.CustomExecutionContext._

class UserController @Inject()
(
  private val userDomainService     : UserDomainService,
  private val userCreateValidator   : UserCreateValidator
)
(
  implicit private val authenticationService: AuthenticationService
) extends SecuredController
{
  Asserts.argumentIsNotNull(userDomainService)
  Asserts.argumentIsNotNull(userCreateValidator)
  Asserts.argumentIsNotNull(authenticationService)

  def read(id: Long) = AuthenticatedActionWithPayload {
    (request, tokenPayload)  =>
      for {
        itemCandidate <- this.userDomainService.tryGetById(id)
        result <- {
          if(itemCandidate.isEmpty) {
            Future.successful(NotFound(ResponseTools.errorToRestResponse("User with this id does not exist.").json))
          } else {
            Future.successful(Ok(ResponseTools.data(itemCandidate.get.withoutPassword).json))
          }
        }
      } yield result
  }

  def create = MutateJsonAction[UserCreateModel](userCreateValidator) {
    (request, validationResult) =>
      val validatedItem = validationResult.validatedItem
      val userCreateEntityAfterModification = validatedItem.copy(
        password = HashUtils.sha1(validatedItem.password),
        email = validatedItem.email.toLowerCase
      )
      for {
        createdItem <- this.userDomainService.create(User.of(userCreateEntityAfterModification))(None) handleError()
        messages <- validationResult.messages
        result <- Future.successful(Ok(ResponseTools.of(createdItem.withoutPassword, Some(messages)).json))
      } yield result
  }
}
