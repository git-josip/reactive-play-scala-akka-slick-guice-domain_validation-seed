package com.josip.reactiveluxury.core

import com.josip.reactiveluxury.core.messages.Messages
import com.josip.reactiveluxury.core.response.{ResponseTools, RestResponse}
import com.josip.reactiveluxury.core.response.ResponseTools
import play.api.libs.json.Writes
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Validator[T] {
  def validate(item:T, userId: Option[Long]) : Future[ValidationResult[T]]
}

case class ValidationResult[T: Writes](
  validatedItem : T,
  messages      : Future[Messages]
) {
  Asserts.argumentIsNotNull(validatedItem)
  Asserts.argumentIsNotNull(messages)

  def isValid: Future[Boolean] = {
    for {
      m <- this.messages
      result <- Future.successful(!m.hasErrors)
    } yield result
  }

  def errorsRestResponse: Future[RestResponse[T]] = {
    for {
      m <- this.messages
      result <- Future.successful(
        ResponseTools.of(
          data = this.validatedItem,
          messages = Some(m)
        )
      )
    } yield result
  }
}
