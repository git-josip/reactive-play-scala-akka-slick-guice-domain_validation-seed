package com.josip.reactiveluxury.core.messages

import com.google.common.base.Objects
import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.{JsValue, Json, Writes}
import Asserts._
import scala.collection.mutable.{ListBuffer => MutebleList}

case class Messages private
(
  private val parentMessages: Option[Messages]
)
{
  argumentIsNotNull(parentMessages)

  private val _messages : MutebleList[Message] = MutebleList.empty[Message]

  def putMessage(message: Message) {
    argumentIsNotNull(message)

    _messages.synchronized {
      _messages += message

      if(parentMessages.isDefined) {
        parentMessages.get.putMessage(message)
      }
    }
  }

  def putMessages(messages: Messages) {
    argumentIsNotNull(messages)

    _messages.synchronized {
      _messages ++= messages.messages

      if(parentMessages.isDefined) {
        parentMessages.get.putMessages(messages)
      }
    }
  }

  def putInformation(text: String) = {
    argumentIsNotNullNorEmpty(text)

    this.putMessage(Message.information(text))
  }
  def putInformation(key: MessageKey, text: String) = {
    argumentIsNotNull(key)
    argumentIsNotNullNorEmpty(text)

    this.putMessage(Message.information(key, text))
  }
  def putInformation(text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    this.putMessage(Message.information(text, childMessages))
  }

  def putInformation(key: MessageKey, text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(key)
    argumentIsNotNull(childMessages)

    this.putMessage(Message.information(key, text, childMessages))
  }

  def putWarning(text: String) = {
    argumentIsNotNullNorEmpty(text)

    this.putMessage(Message.warning(text))
  }
  def putWarning(key: MessageKey, text: String) = {
    argumentIsNotNull(key)
    argumentIsNotNullNorEmpty(text)

    this.putMessage(Message.warning(key, text))
  }
  def putWarning(text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    this.putMessage(Message.warning(text, childMessages))
  }

  def putWarning(key: MessageKey, text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(key)
    argumentIsNotNull(childMessages)

    this.putMessage(Message.warning(key, text, childMessages))
  }

  def putError(text: String) = {
    argumentIsNotNullNorEmpty(text)

    this.putMessage(Message.error(text))
  }
  def putError(key: MessageKey, text: String) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(key)

    this.putMessage(Message.error(key, text))
  }
  def putError(text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(childMessages)

    this.putMessage(Message.error(text, childMessages))
  }

  def putError(key: MessageKey, text: String, childMessages: Messages) = {
    argumentIsNotNullNorEmpty(text)
    argumentIsNotNull(key)
    argumentIsNotNull(childMessages)

    this.putMessage(Message.error(key, text, childMessages))
  }

  def hasAnyMessageWith(messageType: MessageType): Boolean = {
    argumentIsNotNull(messageType)

    messages.foreach(message => {
      if( isMessageWantedType(message, messageType)){ return true }
    })

    false
  }

  def hasInformation() = {
    this.hasAnyMessageWith(MessageType.INFORMATION)
  }

  def hasWarnings() = {
    this.hasAnyMessageWith(MessageType.WARNING)
  }

  def hasErrors() = {
    this.hasAnyMessageWith(MessageType.ERROR)
  }

  def hasAnyErrorsWithMessageKey(messageKey: MessageKey): Boolean = {
    val errorsWithGivenKey = this.errors().filter(_.key.isDefined).filter(_.key.get.equals(messageKey))
    errorsWithGivenKey.nonEmpty
  }

  def messages = {
    _messages.toSeq
  }

  def messages(messageType: MessageType) :Seq[Message] = {
    argumentIsNotNull(messageType)

    filterMessages(this.messages, Seq(messageType))
  }

  def information() = {
    this.messages(MessageType.INFORMATION)
  }

  def warnings() = {
    this.messages(MessageType.WARNING)
  }

  def errors() = {
    this.messages(MessageType.ERROR)
  }

  def bindMessages = {
    this.messages.filter(_.key.isDefined)
  }

  def bindInformation() = {
    filterMessages(this.bindMessages, Seq(MessageType.INFORMATION))
  }

  def bindWarnings() = {
    filterMessages(this.bindMessages, Seq(MessageType.WARNING))
  }

  def bindErrors() = {
    filterMessages(this.bindMessages, Seq(MessageType.ERROR))
  }

  private def filterMessages(messages: Seq[Message], messageTypes: Seq[MessageType]) = {
    messages.filter(message => {
      isMessagesInWantedTypes(message, messageTypes)
    })
  }

  private def isMessagesInWantedTypes(message: Message, messageTypes: Seq[MessageType]) = {
    val filteredTypes = messageTypes.filter(messageType => isMessageWantedType(message, messageType))
    filteredTypes.size match {
      case 0 => false
      case 1 => true
      case _ => throw new RuntimeException
    }
  }
  private def isMessageWantedType(message: Message, messageType: MessageType) = {
    messageType match {
      case MessageType.INFORMATION  => message.messageType.isInformation
      case MessageType.WARNING      => message.messageType.isWarning
      case MessageType.ERROR        => message.messageType.isError
      case _                        => throw new RuntimeException("Invalid type " + messageType.name)

    }
  }

  def responseErrors: JsValue = {
    val errorMessages = this.errors()
    val errorMessagesWithKey = errorMessages.filter(_.key.isDefined)
    val messagesGroupedByKey = errorMessagesWithKey.groupBy(_.key)
    val errorTextByKey = messagesGroupedByKey.map(record => (record._1.get.value, record._2.map(_.text)))
    Json.toJson(errorTextByKey)
  }

  def toJson: String = {
    Json.prettyPrint(Json.toJson(this)(Messages.jsonWrites))
  }

  override def equals(otherAsAny: Any): Boolean = {
    if(otherAsAny == null)                  return false
    if(!otherAsAny.isInstanceOf[Messages])  return false

    val other = otherAsAny.asInstanceOf[Messages]

    return  this.messages.equals(other.messages) &&
            this.parentMessages.equals(other.parentMessages)
  }

  override def hashCode: Int = {
    return Objects.hashCode(
      this.parentMessages,
      this.messages
    )
  }
}

object Messages
{
  def of = {
    Messages(None);
  }

  def of(parentMessages: Messages) = {
    argumentIsNotNull(parentMessages)

    Messages(Some(parentMessages))
  }

  implicit val jsonWrites = new Writes[Messages] {
    def writes(messages: Messages): JsValue = {
      Json.obj(
        "Items" -> messages.messages
      )
    }
  }
}
