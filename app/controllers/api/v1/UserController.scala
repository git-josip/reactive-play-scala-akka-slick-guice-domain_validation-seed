package controllers.api.v1

import javax.inject.Inject
import akka.actor.ActorRef
import com.josip.reactiveluxury.configuration.actor.ActorFactory
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.response.ResponseTools
import com.josip.reactiveluxury.core.utils.HashUtils
import com.josip.reactiveluxury.module.authentication.service.AuthenticationService
import com.josip.reactiveluxury.module.log.action.actor.ActionLogCreateMsg
import com.josip.reactiveluxury.module.log.action.domain.{ActionDomainType, ActionLogEntity, ActionType}
import com.josip.reactiveluxury.module.user.domain.{UserCreateEntity, UserDetailsEntity}
import com.josip.reactiveluxury.module.user.service.domain.UserDomainService
import com.josip.reactiveluxury.module.user.validation.UserCreateValidator
import controllers.core.SecuredController
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserController @Inject()(
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

  def read(id: Long) = AuthenticatedAction {
    request =>
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

  def create = MutateJsonAction[UserCreateEntity](userCreateValidator) {
    (request, validationResult) =>
      val userCreateEntity = validationResult.validatedItem
      for {
        generatedId <- this.userDomainService.create(userCreateEntity.copy(password = HashUtils.sha1(userCreateEntity.password)))
        createdItem <- this.userDomainService.getById(generatedId.id)
        result <- {
          val userCreatedAction = ActionLogEntity.of[UserDetailsEntity, UserDetailsEntity](
            userId      = createdItem.id,
            domainType  = ActionDomainType.USER,
            domainId    = createdItem.id,
            actionType  = ActionType.CREATED,
            before      = None,
            after       = Some(createdItem)
          )
          ActorFactory.actionLogActorRouter.tell(ActionLogCreateMsg(userCreatedAction), ActorRef.noSender)
          for {
            messages <- validationResult.messages
            result <- Future.successful(Ok(ResponseTools.of(createdItem.withoutPassword, Some(messages)).json))
          } yield result
        }
      } yield result
  }
}
