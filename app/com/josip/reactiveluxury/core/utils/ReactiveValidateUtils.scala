package com.josip.reactiveluxury.core.utils

import com.josip.reactiveluxury.core.Asserts._
import com.josip.reactiveluxury.core.messages.{MessageKey, Messages}

import scala.concurrent.Future

object ReactiveValidateUtils {
  def continueIfMessagesHaveErrors(messages: Messages)(action: Future[Messages]): Future[Messages] = {
    argumentIsNotNull(messages)

    if(!messages.hasErrors()) {
      action
    } else {
      Future.successful(messages)
    }
  }


  def validateLengthIsLessThanOrEqual(value: String, minLength: Int, messages: Messages, errorKey:String, errorMessage: String): Future[Messages] = {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateLengthIsLessThanOrEqual(value, minLength, messages, errorKey, errorMessage)

    Future.successful(messages)
  }

  def isFalse(value: Boolean, messages: Messages, errorKey: String, errorMessage: String): Future[Messages] = {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorKey)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.isFalse(value, messages, errorKey, errorMessage)

    Future.successful(messages)
  }

  def isTrue(value: Boolean, messages: Messages, errorKey: String, errorMessage: String): Future[Messages] = {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorKey)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.isTrue(value, messages, errorKey, errorMessage)

    Future.successful(messages)
  }

  def isNotNull[T](value: T, messages: Messages, errorKey: String, errorMessage: String): Future[Messages] = {
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.isNotNull(value, messages, errorKey, errorMessage)

    Future.successful(messages)
  }

  def isNotNullNorEmpty(value: String, messages: Messages, errorKey: String, errorMessage: String): Future[Messages] = {
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.isNotNullNorEmpty(value, messages, errorKey, errorMessage)

    Future.successful(messages)
  }

  def validateEmail(email: String, errorKey: MessageKey, messages: Messages): Future[Messages] = {
    argumentIsNotNull(email)
    argumentIsNotNull(errorKey)
    argumentIsNotNull(messages)

    ValidateUtils.validateEmail(
      email,
      errorKey,
      messages
    )

    Future.successful(messages)
  }
}
