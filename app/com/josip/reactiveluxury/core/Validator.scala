package com.josip.reactiveluxury.core

import com.josip.reactiveluxury.core.messages.Messages
import com.josip.reactiveluxury.core.response.RestResponse
import com.josip.reactiveluxury.core.response.ResponseTools
import play.api.libs.json.Writes
import scala.concurrent.Future

trait Validator[T] {
  def validate(item:T, userId: Option[Long]) : Future[ValidationResult[T]]
}

case class ValidationResult[T: Writes](
  validatedItem : T,
  messages      : Messages
) {
  Asserts.argumentIsNotNull(validatedItem)
  Asserts.argumentIsNotNull(messages)

  def isValid: Boolean = {
    !this.messages.hasErrors();
  }

  def errorsRestResponse: RestResponse[T] = {
    ResponseTools.of(
      data = this.validatedItem,
      messages = Some(this.messages)
    )
  }
}
