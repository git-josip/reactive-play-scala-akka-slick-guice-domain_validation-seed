package com.josip.reactiveluxury.core.messages

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.functional.syntax._
import play.api.libs.json._
import Asserts._

case class Message private
(
  messageType     : MessageType,
  key             : Option[MessageKey],
  text            : String,
  childMessages   : Messages
)
{
  argumentIsNotNull(messageType)
  argumentIsNotNull(key)
  argumentIsNotNullNorEmpty(text)
  argumentIsNotNull(childMessages)
}

object Message {
  def information(text: String) = {
    argumentIsNotNullNorEmpty(text)

    Message.of(MessageType.INFORMATION, text)
  }

  def information(text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    Message.of(childMessages, MessageType.INFORMATION, text)
  }

  def information(key: MessageKey, text: String) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(key)

    Message.of(MessageType.INFORMATION, key, text)
  }

  def information(key: MessageKey, text: String, childMessages: Messages) = {
    argumentIsNotNull(key)
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    Message.of(MessageType.INFORMATION, key, text, childMessages)
  }

  def warning(text: String) = {
    argumentIsNotNullNorEmpty(text)

    Message.of(MessageType.WARNING, text)
  }

  def warning(text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    Message.of(childMessages, MessageType.WARNING, text)
  }

  def warning(key: MessageKey, text: String) = {
    argumentIsNotNull(key)
    argumentIsNotNullNorEmpty(text)

    Message.of(MessageType.WARNING, key, text)
  }

  def warning(key: MessageKey, text: String, childMessages: Messages) = {
    argumentIsNotNull(key)
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    Message.of(MessageType.WARNING, key, text, childMessages)
  }

  def error(text: String) = {
    argumentIsNotNullNorEmpty(text)

    Message.of(MessageType.ERROR, text)
  }

  def error(text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    Message.of(childMessages, MessageType.ERROR, text)
  }

  def error(key  : MessageKey, text: String) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(key)

    Message.of(MessageType.ERROR, key, text)
  }

  def error(key: MessageKey, text: String, childMessages: Messages) = {
    argumentIsNotNull(key)
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    Message.of(MessageType.ERROR, key, text, childMessages)
  }

  private def of(messageType: MessageType, text: String) = {
    Message(messageType, None, text, Messages.of)
  }
  private def of(childMessages: Messages, messageType: MessageType, text: String) = {
    val childMessagesCopied = Messages.of
    childMessagesCopied.putMessages(childMessages)

    Message(messageType, None, text, childMessagesCopied)
  }
  private def of(messageType: MessageType, key: MessageKey, text: String) = {
    Message(messageType, Some(key), text, Messages.of)
  }
  private def of(messageType: MessageType, key: MessageKey, text: String, childMessages: Messages) = {
    Message(messageType, Some(key), text, childMessages)
  }

  implicit val MESSAGE_TYPE: Writes[MessageType] = new Writes[MessageType] {
    def writes(o: MessageType): JsValue = {
      Json.obj(
        "Type" -> o.name
      )
    }
  }
  implicit val jsonWrites: Writes[Message] = (
    (__ \ "type")       .write[MessageType]         and
    (__ \ "messageKey") .write[Option[MessageKey]]  and
    (__ \ "text")       .write[String]              and
    (__ \ "children")   .lazyWrite(Messages.jsonWrites)
  )(unlift(Message.unapply))
}
