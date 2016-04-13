package com.josip.reactiveluxury.core.utils

import com.josip.reactiveluxury.core.Asserts._
import com.josip.reactiveluxury.core.messages.Messages
import scala.concurrent.Future

object ReactiveValidateUtils {

  def validateLengthIsLessThanOrEqual(value: String, minLength: Int, messages: Messages, errorKey:String, errorMessage: String) = {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateLengthIsLessThanOrEqual(value, minLength, messages, errorKey, errorMessage)

    Future.successful({})
  }

  def isFalse(value: Boolean, messages: Messages, errorKey: String, errorMessage: String) = {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorKey)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.isFalse(value, messages, errorKey, errorMessage)

    Future.successful({})
  }

  def isNotNull[T](value: T, messages: Messages, errorKey: String, errorMessage: String) = {
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.isNotNull(value, messages, errorKey, errorMessage)

    Future.successful({})
  }

  def isNotNullNorEmpty(value: String, messages: Messages, errorKey: String, errorMessage: String) = {
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.isNotNullNorEmpty(value, messages, errorKey, errorMessage)

    Future.successful({})
  }
}
