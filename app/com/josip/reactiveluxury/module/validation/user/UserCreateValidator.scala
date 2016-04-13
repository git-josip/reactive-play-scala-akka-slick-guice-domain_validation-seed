package com.josip.reactiveluxury.module.validation.user

import javax.inject.{Inject, Singleton}
import com.josip.reactiveluxury.configuration.CustomExecutionContext._
import com.josip.reactiveluxury.core.messages.Messages
import com.josip.reactiveluxury.core.utils.{ReactiveValidateUtils, ValidateUtils}
import com.josip.reactiveluxury.core.{Asserts, ValidationResult, Validator}
import com.josip.reactiveluxury.module.domain.user.UserCreateModel
import com.josip.reactiveluxury.module.service.domain.user.UserDomainService
import scala.concurrent.Future

@Singleton()
class UserCreateValidator @Inject()
(
  private val entityDomainService : UserDomainService
) extends Validator[UserCreateModel] {
  Asserts.argumentIsNotNull(entityDomainService)

  override def validate(item: UserCreateModel, userId: Option[Long]): Future[ValidationResult[UserCreateModel]] = {
    Asserts.argumentIsNotNull(item)

    val validationMessages = Messages.of
    for {
      _ <- validateFirstName (item, validationMessages) handleError()
      _ <- validateLastName  (item, validationMessages) handleError()
      _ <- validatePassword  (item, validationMessages) handleError()
      _ <- validateEmail(item, validationMessages) handleError()
      _ <- validateContact(item, validationMessages) handleError()
    } yield ValidationResult(
        validatedItem = item,
        messages      = Future.successful(validationMessages)
      )
  }

  private def validateFirstName(item: UserCreateModel, validationMessages: Messages): Future[Unit] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.firstName

    ValidateUtils.isNotNullNorEmpty(
      fieldValue,
      localMessages,
      UserCreateModel.FIRST_NAME_FORM_ID.value,
      "must be defined"
    )

    if(!localMessages.hasErrors()) {
      ValidateUtils.validateLengthIsLessThanOrEqual(
        fieldValue,
        255,
        localMessages,
        UserCreateModel.FIRST_NAME_FORM_ID.value,
        "must be less than or equal to 255 character"
      )
    }

    Future.successful({})
  }

  private def validateLastName(item: UserCreateModel, validationMessages: Messages): Future[Unit] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.lastName

    ValidateUtils.isNotNullNorEmpty(
      fieldValue,
      localMessages,
      UserCreateModel.LAST_NAME_FORM_ID.value,
      "must be defined"
    )

    if(!localMessages.hasErrors()) {
      ValidateUtils.validateLengthIsLessThanOrEqual(
        fieldValue,
        255,
        localMessages,
        UserCreateModel.LAST_NAME_FORM_ID.value,
        "must be less than or equal to 255 character"
      )
    }

    Future.successful({})
  }

  private def validateEmail(item: UserCreateModel, validationMessages: Messages): Future[Unit] = {
    def validateEmailUniqueness(fV: String, messages: Messages): Future[Unit] = {
      if (!messages.hasErrors()) {
        for {
          doesExistWithEmail <- this.entityDomainService.doesExistByByEmail(fV) handleError()
          _ <- ReactiveValidateUtils.isFalse(
            doesExistWithEmail,
            messages,
            UserCreateModel.EMAIL_FORM_ID.value,
            "User already exists with this email"
          )

          _ <- ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
            fV,
            80,
            messages,
            UserCreateModel.EMAIL_FORM_ID.value,
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
        UserCreateModel.EMAIL_FORM_ID.value,
        "Email must be defined"
      )
      _ <- {
        if(!localMessages.hasErrors()) {
          ValidateUtils.validateEmail(
            fieldValue.toLowerCase,
            UserCreateModel.EMAIL_FORM_ID,
            localMessages
          )
        }
        Future.successful({})
      }
      m <- validateEmailUniqueness(fieldValue.toLowerCase, localMessages)
    } yield Future.successful({})
  }

  private def validatePassword(item: UserCreateModel, validationMessages: Messages): Future[Unit] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.password

    ValidateUtils.isNotNullNorEmpty(
      fieldValue,
      localMessages,
      UserCreateModel.PASSWORD_FORM_ID.value,
      "must be defined"
    )

    if(!localMessages.hasErrors()) {
      ValidateUtils.isTrue(
        fieldValue.length >= 6,
        localMessages,
        UserCreateModel.PASSWORD_FORM_ID.value,
        "password length must not be less than 6"
      )

      ValidateUtils.isTrue(
        fieldValue.length <=255,
        localMessages,
        UserCreateModel.PASSWORD_FORM_ID.value,
        "password length must not be bigger than 255"
      )
    }

    Future.successful({})
  }

  private def validateContact(item: UserCreateModel, validationMessages: Messages): Future[Unit] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.contact

    for {
      _ <- ReactiveValidateUtils.isNotNull(
        fieldValue,
        localMessages,
        UserCreateModel.CONTACT_FORM_ID.value,
        "must be defined"
      )
    } yield Future.successful({})
  }
}