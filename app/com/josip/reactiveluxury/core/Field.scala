package com.josip.reactiveluxury.core

import com.josip.reactiveluxury.core.messages.{Messages, MessageKey}
import com.josip.reactiveluxury.core.utils.{StringUtils, ValidateUtils}
import Asserts._
import com.josip.reactiveluxury.core.messages.MessageKey
import com.josip.reactiveluxury.core.utils.ValidateUtils
import StringUtils._
import org.joda.time.format.DateTimeFormatter
import org.joda.time.{DateTime, DateTimeZone, LocalDate}
import play.api.libs.json.JsValue

import scala.language.existentials

sealed abstract class Field[TField](key: String, val evaluateBindHelper: (MessageKey, String, Messages) => Unit)
{
  argumentIsNotNullNorEmpty(key)
  argumentIsNotNull(evaluateBindHelper)

  final val messageKey = MessageKey(key)

  def bind(valueAsString: Option[String]) : TField
  def bindJsValue(valueAsJsValue: Option[JsValue]): TField

  def evaluateBind(valueAsString: Option[String], messages: Messages)
  def evaluateBindJsValue(valueAsJsValue: Option[JsValue], messages: Messages)
}

object Field
{
  private[this] final val DEFAULT_TO_STRING_METHOD = (v: Any) => { v.toString }

  def jsValueToString(jsValue: Option[JsValue]): Option[String] =
  {
    jsValue.map(jv => trimExtraQuotes(jv.toString()))
  }

  sealed case class MandatoryField[TField]
  (
    key                       : String,
    override val evaluateBindHelper : (MessageKey, String, Messages)  => Unit,
    bindFromString            : (String          )  => TField,
    toStringMethod            : (TField          )  => String         = DEFAULT_TO_STRING_METHOD
  ) extends Field[TField](key, evaluateBindHelper)
  {
    argumentIsNotNullNorEmpty(key)
    argumentIsNotNull(evaluateBindHelper)
    argumentIsNotNull(bindFromString)
    argumentIsNotNull(toStringMethod)

    override def bind(valueAsString: Option[String]): TField =
    {
      argumentIsNotNull(valueAsString)
      argumentIsTrue(valueAsString.isDefined)

      val localMessages = Messages.of
      this.evaluateBind(valueAsString, localMessages)
      argumentIsTrue(!localMessages.hasErrors)

      bindFromString(valueAsString.get)
    }

    override def bindJsValue(valueAsJsValue: Option[JsValue]): TField =
    {
      argumentIsNotNull(valueAsJsValue)
      argumentIsTrue(valueAsJsValue.isDefined)

      this.bind(Field.jsValueToString(valueAsJsValue))
    }

    override def evaluateBind(valueAsString: Option[String], messages: Messages)
    {
      argumentIsNotNull(valueAsString)
      argumentIsNotNull(messages)

      val localMessages = Messages.of(messages)

      ValidateUtils.validateIsOptionDefined(messageKey, valueAsString, localMessages)
      if(!localMessages.hasErrors)
      {
        this.evaluateBindHelper(messageKey, valueAsString.get, messages)
      }
    }

    override def evaluateBindJsValue(valueAsJsValue: Option[JsValue], messages: Messages)
    {
      argumentIsNotNull(valueAsJsValue)
      argumentIsNotNull(messages)

      this.evaluateBind(
        valueAsString = Field.jsValueToString(valueAsJsValue),
        messages = messages
      )
    }
  }
  object MandatoryField
  {
    def of[TField]
    (
      key                     : String,
      evaluateBindFromString  : (MessageKey, String, Messages)  => Unit,
      bindFromString          : (String) => TField,
      toStringMethod          : TField => String
    ): MandatoryField[TField] =
    {
      argumentIsNotNullNorEmpty(key)
      argumentIsNotNull(evaluateBindFromString)
      argumentIsNotNull(bindFromString)
      argumentIsNotNull(toStringMethod)

      MandatoryField(
        key                 = key,
        evaluateBindHelper  = evaluateBindFromString,
        bindFromString      = bindFromString,
        toStringMethod      = toStringMethod
      )
    }

    def of[TField]
    (
      key                     : String,
      evaluateBindFromString  : (MessageKey, String, Messages)  => Unit,
      bindFromString          : (String) => TField
    ): MandatoryField[TField] =
    {
      argumentIsNotNullNorEmpty(key)
      argumentIsNotNull(evaluateBindFromString)
      argumentIsNotNull(bindFromString)

      MandatoryField(
        key                 = key,
        evaluateBindHelper  = evaluateBindFromString,
        bindFromString      = bindFromString
      )
    }
  }

  sealed case class OptionalField[TField]
  (
    field: MandatoryField[TField]
  ) extends Field[Option[TField]](field.key, field.evaluateBindHelper)
  {
    argumentIsNotNull(field)

    override def bind(valueAsString: Option[String]): Option[TField] =
    {
      argumentIsNotNull(valueAsString)

      if (!valueAsString.isDefined) return None

      val localMessages = Messages.of
      this.field.evaluateBind(valueAsString, localMessages)
      argumentIsTrue(!localMessages.hasErrors)

      Some(field.bind(valueAsString))
    }

    override def bindJsValue(valueAsJsValue: Option[JsValue]): Option[TField] =
    {
      argumentIsNotNull(valueAsJsValue)
      argumentIsTrue(valueAsJsValue.isDefined)

      this.bind(Field.jsValueToString(valueAsJsValue))
    }

    override def evaluateBind(valueAsString: Option[String], messages: Messages)
    {
      argumentIsNotNull(valueAsString)
      argumentIsNotNull(messages)

      if(valueAsString.isDefined)
      {
        this.field.evaluateBind(valueAsString, messages)
      }
    }

    override def evaluateBindJsValue(valueAsJsValue: Option[JsValue], messages: Messages)
    {
      argumentIsNotNull(valueAsJsValue)
      argumentIsNotNull(messages)

      this.evaluateBind(
        valueAsString = Field.jsValueToString(valueAsJsValue),
        messages      = messages
      )
    }
  }

  def longNumber(fieldKey: String): MandatoryField[Long] =
  {
    argumentIsNotNullNorEmpty(fieldKey)

    MandatoryField.of[Long](fieldKey, evaluateParsingLong _, parseLong _)
  }

  def number(fieldKey: String): MandatoryField[Int] =
  {
    argumentIsNotNullNorEmpty(fieldKey)

    MandatoryField.of[Int](fieldKey, evaluateParsingInt _, parseInt _)
  }

  def boolean(fieldKey: String): MandatoryField[Boolean] =
  {
    argumentIsNotNullNorEmpty(fieldKey)

    MandatoryField.of[scala.Boolean](fieldKey, evaluateParsingBoolean _, parseBoolean _)
  }

  def bigDecimal(fieldKey: String): MandatoryField[BigDecimal] =
  {
    argumentIsNotNullNorEmpty(fieldKey)

    MandatoryField.of[BigDecimal] (fieldKey, evaluateParsingBigDecimal _, parseBigDecimal _)
  }

  def text(key: String): MandatoryField[String] =
  {
    argumentIsNotNullNorEmpty(key)

    val evaluateParsingString = (k: MessageKey, v: String, m: Messages) => {
      argumentIsNotNull(k)
      argumentIsNotNull(v)
      argumentIsNotNull(m)
    }
    val parseString           = (v: String) => {
      argumentIsNotNull(v)
      v
    }
    MandatoryField.of[String](key, evaluateParsingString, parseString)
  }

  def nonEmptyText(key: String): MandatoryField[String] =
  {
    argumentIsNotNullNorEmpty(key)

    val evaluateParsingString = (k: MessageKey, v: String, m: Messages) => {
      argumentIsNotNull(k)
      argumentIsNotNull(v)
      argumentIsNotNull(m)

      ValidateUtils.isNotEmpty(v, m, k.value, "Value must not be empty")
    }
    val parseString           = (v: String) => {
      argumentIsNotNull(v)
      v
    }
    MandatoryField.of[String](key, evaluateParsingString, parseString)
  }

  def dateTime(key: String)(dateFormatter: DateTimeFormatter): MandatoryField[DateTime] =
  {
    argumentIsNotNullNorEmpty(key)
    argumentIsNotNull(dateFormatter)

    val evaluateParsingMethod = (k: MessageKey, v: String, m: Messages)  => { evaluateParsingDateTime(k, v, dateFormatter, m) }
    val parseDateTimeMethod   = (v: String)               => { parseDateTime(v, dateFormatter)              }
    val toStringMethod        = (v: DateTime)             => { dateFormatter.print(v)                       }
    MandatoryField.of[DateTime](key, evaluateParsingMethod, parseDateTimeMethod, toStringMethod)
  }

  def dateTimeFromMillis(key: String): MandatoryField[DateTime] =
  {
    argumentIsNotNullNorEmpty(key)

    val evaluateParsingMethod = (k: MessageKey, v: String, m: Messages)  => {
      val localMessages = Messages.of
      StringUtils.evaluateParsingLong(k, v, localMessages)
      if (localMessages.hasErrors)
        m.putError(k, "invalid time in milliseconds")
    }
    val parseDateTimeMethod   = (v: String)               => { new DateTime(StringUtils.parseLong(v), DateTimeZone.UTC) }
    val toStringMethod        = (v: DateTime)             => { v.getMillis.toString                                     }
    MandatoryField.of[DateTime](key, evaluateParsingMethod, parseDateTimeMethod, toStringMethod)
  }

  def localDate(key: String)(dateFormatter: DateTimeFormatter): MandatoryField[LocalDate] =
  {
    argumentIsNotNullNorEmpty(key)
    argumentIsNotNull(dateFormatter)

    val evaluateParsingMethod = (k: MessageKey, v: String, m: Messages)  => { evaluateParsingLocalDate(k, v, dateFormatter, m)  }
    val parseMethod           = (v: String)               => { parseLocalDate(v, dateFormatter)               }
    val toStringMethod        = (v: LocalDate)            => { dateFormatter.print(v)                         }

    MandatoryField.of[LocalDate](key, evaluateParsingMethod, parseMethod, toStringMethod)
  }

  def optional[TField](field: MandatoryField[TField]): OptionalField[TField] =
  {
    argumentIsNotNull(field)

    OptionalField(field)
  }
}



