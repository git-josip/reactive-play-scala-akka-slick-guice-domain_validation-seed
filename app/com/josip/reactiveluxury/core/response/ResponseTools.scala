package com.josip.reactiveluxury.core.response

import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.messages.{MessageKey, Messages}
import com.josip.reactiveluxury.core.messages.MessageKey
import play.api.libs.json.{Json, JsError, JsValue, Writes}

object ResponseTools
{
  val GLOBAL_MESSAGE_KEY = MessageKey("GLOBAL_MESSAGE")

  def restResponseOf[T: Writes](data: T, messages: Messages): RestResponse[T] =
  {
    val messagesRestResponse = ResponseTools.messagesToMessagesRestResponse(messages)

    RestResponse[T](
      data      = Some(data),
      messages  = Some(messagesRestResponse)
    )
  }

  def messagesToMessagesRestResponse(messages: Messages): MessagesRestResponse =
  {
    val global = Helper.messagesToGlobalMessagesRestResponse(messages)
    val local = Helper.messagesToLocalMessagesRestResponse(messages)

    MessagesRestResponse(
      global  = Some(global),
      local   = local
    )
  }

  def data[T: Writes](data: T) =
  {
    Asserts.argumentIsNotNull(data)

    RestResponse[T](data = Some(data))
  }

  def noData(messages: Messages) = {
    Asserts.argumentIsNotNull(messages)

    ResponseTools.restResponseOf(Json.toJson("{}"), messages)
  }

  def messages(messagesRestResponse: MessagesRestResponse) =
  {
    Asserts.argumentIsNotNull(messagesRestResponse)

    RestResponse(
      data      = Option.empty[JsValue],
      messages  = Some(messagesRestResponse)
    )
  }

  def of[T: Writes](data: T, messages: Option[Messages]) = {
    Asserts.argumentIsNotNull(data)
    Asserts.argumentIsNotNull(messages)

    RestResponse[T](
      data      = Some(data),
      messages  = messages.map(ResponseTools.messagesToMessagesRestResponse)
    )
  }

  def jsErrorToRestResponse[T: Writes](error: JsError): RestResponse[T] =
  {
    Asserts.argumentIsNotNull(error)

    val errorsGroupByFormId = error.errors.groupBy(_._1.toJsonString).toList

    val restLocalErrors = errorsGroupByFormId.map(v =>
      LocalMessagesRestResponse(
        formIdentifier = v._1.replaceFirst("obj.", ""),
        errors = v._2.map(_._2).flatten.map(_.message).toList
      )
    )

    val messagesRestResponse = MessagesRestResponse(local = restLocalErrors)

    RestResponse[T](data = None, messages = Some(messagesRestResponse))
  }

  def errorsToRestResponse(errors: List[String]) =
  {
    Asserts.argumentIsNotNull(errors)

    val messagesResponse = MessagesRestResponse(
      global = Some(GlobalMessagesRestResponse(errors = errors))
    )
    ResponseTools.messages(messagesResponse)
  }

  def errorToRestResponse(error: String) =
  {
    Asserts.argumentIsNotNull(error)

    ResponseTools.errorsToRestResponse(List(error))
  }

  private object Helper
  {
    def messagesToGlobalMessagesRestResponse(messages: Messages): GlobalMessagesRestResponse =
    {
      val allBindMessages = messages.bindMessages
      val allGlobalBindMessage = allBindMessages.filter(_.key == Some(GLOBAL_MESSAGE_KEY)).toList

      val globalInfo = allGlobalBindMessage.filter(_.messageType.isInformation).map(_.text)
      val globalWarnings = allGlobalBindMessage.filter(_.messageType.isWarning).map(_.text)
      val globalErrors = allGlobalBindMessage.filter(_.messageType.isError).map(_.text)

      GlobalMessagesRestResponse(
        info      = globalInfo,
        warnings  = globalWarnings,
        errors    = globalErrors
      )
    }

    def messagesToLocalMessagesRestResponse(messages: Messages): List[LocalMessagesRestResponse] =
    {
      val allBindMessages = messages.bindMessages
      val allNonGlobalMessages = allBindMessages.filter(_.key.get != GLOBAL_MESSAGE_KEY).toList
      val messagesGroupedByKey = allNonGlobalMessages.groupBy(_.key.get)

      messagesGroupedByKey.map(record =>
        LocalMessagesRestResponse(
          formIdentifier  = record._1.value,
          info            = record._2.filter(_.messageType.isInformation).map(_.text),
          warnings        = record._2.filter(_.messageType.isWarning).map(_.text),
          errors          = record._2.filter(_.messageType.isError).map(_.text)
        )
      ).toList
    }
  }
}
