package com.josip.reactiveluxury.module.validation.user

import javax.inject.{Inject, Singleton}

import com.josip.reactiveluxury.configuration.CustomExecutionContext._
import com.josip.reactiveluxury.core.messages.Messages
import com.josip.reactiveluxury.core.utils.ReactiveValidateUtils
import com.josip.reactiveluxury.core.utils.ReactiveValidateUtils.continueIfMessagesHaveErrors
import com.josip.reactiveluxury.core.{Asserts, ValidationResult, Validator}
import com.josip.reactiveluxury.module.domain.user.UserCreateModel
import com.josip.reactiveluxury.module.service.domain.user.UserDomainService

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class UserCreateValidator @Inject()
(
  private val entityDomainService : UserDomainService,
  implicit val ec : ExecutionContext
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
        messages      = validationMessages
      )
  }

  private def validateFirstName(item: UserCreateModel, validationMessages: Messages): Future[Messages] = {
    val localMessages = Messages.of(validationMessages)
    val fieldValue = item.firstName

    for {
      _ <- ReactiveValidateUtils.isNotNullNorEmpty(
        fieldValue,
        localMessages,
        UserCreateModel.FIRST_NAME_FORM_ID.value,
        "must be defined"
      )
      _ <- continueIfMessagesHaveErrors(localMessages) (
        ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
          fieldValue,
          255,
          localMessages,
          UserCreateModel.FIRST_NAME_FORM_ID.value,
          "must be less than or equal to 255 character"
        )
      )

    } yield localMessages
  }

  private def validateLastName(item: UserCreateModel, validationMessages: Messages): Future[Messages] = {
    val localMessages = Messages.of(validationMessages)
    val fieldValue = item.lastName

    for {
      _ <- ReactiveValidateUtils.isNotNullNorEmpty(
        fieldValue,
        localMessages,
        UserCreateModel.LAST_NAME_FORM_ID.value,
        "must be defined"
      )
      _ <- continueIfMessagesHaveErrors(localMessages) (
        ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
          fieldValue,
          255,
          localMessages,
          UserCreateModel.LAST_NAME_FORM_ID.value,
          "must be less than or equal to 255 character"
        )
      )
    } yield localMessages
  }

  private def validateEmail(item: UserCreateModel, validationMessages: Messages): Future[Messages] = {
    val fieldValue = item.email
    val localMessages = Messages.of(validationMessages)

    for {
      _ <- ReactiveValidateUtils.isNotNullNorEmpty(
        fieldValue,
        localMessages,
        UserCreateModel.EMAIL_FORM_ID.value,
        "Email must be defined"
      )
      _ <- continueIfMessagesHaveErrors(localMessages) (
        ReactiveValidateUtils.validateEmail(
          fieldValue.toLowerCase,
          UserCreateModel.EMAIL_FORM_ID,
          localMessages
        )
      )
      doesExistWithEmail <- this.entityDomainService.doesExistByByEmail(fieldValue) handleError()
      _ <- ReactiveValidateUtils.isFalse(
        doesExistWithEmail,
        localMessages,
        UserCreateModel.EMAIL_FORM_ID.value,
        "User already exists with this email"
      )
      _ <- ReactiveValidateUtils.validateLengthIsLessThanOrEqual(
        fieldValue,
        80,
        localMessages,
        UserCreateModel.EMAIL_FORM_ID.value,
        "must be less than or equal to 80 character"
      )
    } yield localMessages
  }

  private def validatePassword(item: UserCreateModel, validationMessages: Messages): Future[Messages] = {
    val localMessages = Messages.of(validationMessages)
    val fieldValue = item.password

    for {
      _ <- ReactiveValidateUtils.isNotNullNorEmpty(
        fieldValue,
        localMessages,
        UserCreateModel.PASSWORD_FORM_ID.value,
        "must be defined"
      )
      _ <- continueIfMessagesHaveErrors(localMessages) (
        ReactiveValidateUtils.isTrue(
          fieldValue.length >= 6,
          localMessages,
          UserCreateModel.PASSWORD_FORM_ID.value,
          "password length must not be less than 6"
        )
      )

      _ <- continueIfMessagesHaveErrors(localMessages) (
        ReactiveValidateUtils.isTrue(
          fieldValue.length <=255,
          localMessages,
          UserCreateModel.PASSWORD_FORM_ID.value,
          "password length must not be bigger than 255"
        )
      )
    } yield localMessages
  }

  private def validateContact(item: UserCreateModel, validationMessages: Messages): Future[Messages] = {
    val localMessages = Messages.of(validationMessages)

    val fieldValue = item.contact

    for {
      _ <- ReactiveValidateUtils.isNotNull(
        fieldValue,
        localMessages,
        UserCreateModel.CONTACT_FORM_ID.value,
        "must be defined"
      )
    } yield localMessages
  }
}