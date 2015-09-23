package com.josip.reactiveluxury.module.user.validation

import javax.inject.{Inject, Singleton}
import com.josip.reactiveluxury.core.{ValidationResult, Validator, Asserts}
import com.josip.reactiveluxury.core.messages.Messages
import com.josip.reactiveluxury.module.user.domain.UserCreateEntity
import com.josip.reactiveluxury.module.user.service.domain.UserDomainService
import com.josip.reactiveluxury.core.utils.ReactiveValidateUtils
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class UserCreateValidator @Inject() (
  private val entityDomainService: UserDomainService
) extends Validator[UserCreateEntity] {
  Asserts.argumentIsNotNull(entityDomainService)

  override def validate(item: UserCreateEntity): Future[ValidationResult[UserCreateEntity]] = {
    Asserts.argumentIsNotNull(item)

    val validationMessages = Messages.of
    for {
      _ <- validateFirstName (item, validationMessages)
      _ <- validateLastName  (item, validationMessages)
      _ <- validatePassword  (item, validationMessages)
      _ <- validateFirstName (item, validationMessages)
      _ <- validateUserName(item, validationMessages)
      _ <- validateEmail(item, validationMessages)
    } yield ValidationResult(
        validatedItem = item,
        messages      = Future.successful(validationMessages)
      )
  }

  private def validateFirstName(item: UserCreateEntity, validationMessages: Messages): Future[Unit] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.firstName

    for {
      _ <- ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
      fieldValue,
      80,
      localMessages,
      UserCreateEntity.FIRST_NAME_FORM_ID.value,
      "must be less than or equal to 80 character"
      )
    } yield Future.successful({})
  }

  private def validateLastName(item: UserCreateEntity, validationMessages: Messages): Future[Unit] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.lastName

    for {
      _ <- ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
      fieldValue,
      80,
      localMessages,
      UserCreateEntity.LAST_NAME_FORM_ID.value,
      "must be less than or equal to 80 character"
      )
    } yield Future.successful({})
  }

  private def validateEmail(item: UserCreateEntity, validationMessages: Messages): Future[Unit] = {
    def validateEmailUniqueness(fV: String, messages: Messages): Future[Unit] = {
      if (!messages.hasErrors()) {
        for {
          doesExistWithEmail <- this.entityDomainService.doesExistByByEmail(fV)
          _ <- ReactiveValidateUtils.isFalse(
            doesExistWithEmail,
            messages,
            UserCreateEntity.EMAIL_FORM_ID.value,
            "User already exists with this email"
          )

          _ <- ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
            fV,
            80,
            messages,
            UserCreateEntity.EMAIL_FORM_ID.value,
            "must be less than or equal to 80 character"
          )
        } yield Future.successful({})
      } else {
        Future.successful({})
      }
    }

    val fieldValue = item.email
    val localMessages = Messages.of(validationMessages)

    for {
      _ <- ReactiveValidateUtils.isNotNullNorEmpty(
        fieldValue,
        localMessages,
        UserCreateEntity.EMAIL_FORM_ID.value,
        "Email must be defined"
      )

      m <- validateEmailUniqueness(fieldValue, localMessages)
    } yield Future.successful({})
  }

  private def validateUserName(item: UserCreateEntity, validationMessages: Messages): Future[Unit] = {
    def validateUserNameUniqueness(fV: String, messages: Messages): Future[Unit] = {
      if (!messages.hasErrors()) {
        for {
          doesExistWithUsername <- this.entityDomainService.doesExistByUsername(fV)
          _ <- ReactiveValidateUtils.isFalse(
            doesExistWithUsername,
            messages,
            UserCreateEntity.USERNAME_FORM_ID.value,
            "User already exists with this username"
          )

          _ <- ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
            fV,
            80,
            messages,
            UserCreateEntity.USERNAME_FORM_ID.value,
            "must be less than or equal to 80 character"
          )
        } yield Future.successful({})
      } else {
        Future.successful({})
      }
    }

    val fieldValue = item.username
    val localMessages = Messages.of(validationMessages)

    for {
      _ <- ReactiveValidateUtils.isNotNullNorEmpty(
        fieldValue,
        localMessages,
        UserCreateEntity.USERNAME_FORM_ID.value,
        "Username must be defined"
      )

      _ <- validateUserNameUniqueness(fieldValue, localMessages)
    } yield Future.successful({})
  }

  private def validatePassword(item: UserCreateEntity, validationMessages: Messages): Future[Unit] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.password

    for {
      _ <- ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
      fieldValue,
      80,
      localMessages,
      UserCreateEntity.PASSWORD_FORM_ID.value,
      "must be less than or equal to 80 character"
      )
    } yield Future.successful({})
  }
}