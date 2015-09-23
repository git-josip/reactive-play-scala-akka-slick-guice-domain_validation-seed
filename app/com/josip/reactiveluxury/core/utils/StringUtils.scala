package com.josip.reactiveluxury.core.utils

import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.messages.{MessageKey, Messages}
import Asserts._
import org.joda.time.format.DateTimeFormatter
import org.joda.time.{LocalDate, DateTime}
import com.josip.reactiveluxury.core.messages.MessageKey

object StringUtils
{
  final val WHITESPACE              = " "
  final val EMPTY_STRING            = ""

  private final val ERROR_DEFAULT_KEY = MessageKey("ErrorKey")

  private final val SINGLE_QUOTE_FORMATTER  = "'%s'"

  private object ErrorMessages
  {
    val VALUE_CANT_BE_PARSED_DEFAULT_ERROR_FORMATTER = "Text '%s' cannot be parsed to class '%s'."

    def createDefaultParsingError[TItem](valueAsString: String)(implicit fieldManifest: Manifest[TItem]): String =
    {
      val valueClass = fieldManifest.runtimeClass
      String.format(VALUE_CANT_BE_PARSED_DEFAULT_ERROR_FORMATTER, valueAsString, valueClass.getSimpleName)
    }
  }

  final val COMMA_SEPARATOR = ","

  def trimExtraQuotes(value: String): String =
  {
    if(value.startsWith("\"") && value.endsWith("\"")) {
      value.substring(1, value.length - 1)
    } else {
      value
    }
  }

  def singleQuote(value: String): String =
  {
    argumentIsNotNull(value)

    String.format(SINGLE_QUOTE_FORMATTER, value)
  }

  def removeWhiteSpaces(value: String) =
  {
    argumentIsNotNull(value)

    value.replace(WHITESPACE, EMPTY_STRING)
  }

  def trim(value: String) =
  {
    argumentIsNotNull(value)

    value.trim
  }

  def isEmpty(valueAsString: String) =
  {
    argumentIsNotNull(valueAsString)

    valueAsString.isEmpty
  }

  def isNotEmpty(valueAsString: String) =
  {
    argumentIsNotNull(valueAsString)

    !StringUtils.isEmpty(valueAsString)
  }

  def split(valueAsString: String, separator: String): List[String] =
  {
    argumentIsNotNull(valueAsString)
    argumentIsNotNullNorEmpty(separator)

    val localMessages = Messages.of
    this.evaluateSplit(valueAsString, separator, localMessages)
    argumentIsTrue(!localMessages.hasErrors)

    valueAsString.split(separator).toList
  }
  def evaluateSplit(valueAsString: String, separator: String, messages: Messages)
  {
    argumentIsNotNull(valueAsString)
    argumentIsNotNullNorEmpty(separator)
    argumentIsNotNull(messages)

    try   { valueAsString.split(separator)  }
    catch { case _: Throwable => messages.putError("Value can be split by this separator.")}
  }
  def canSplit(valueAsString: String, separator: String) =
  {
    argumentIsNotNullNorEmpty(separator)

    val messages = Messages.of
    this.evaluateSplit(valueAsString, separator, messages)

    !messages.hasErrors
  }

  def tryParseBigDecimal(key: MessageKey, valueAsString: String, messages: Messages): Option[BigDecimal] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingBigDecimal(key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseBigDecimalWithErrorKey(key, valueAsString)) }
  }
  def parseBigDecimalWithErrorKey(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(StringUtils.canParseBigDecimal(key, valueAsString))

    BigDecimal(valueAsString)
  }
  def parseBigDecimal(valueAsString: String): BigDecimal =
  {
    this.parseBigDecimalWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingBigDecimal(key: MessageKey, valueAsString: String, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    try   { BigDecimal(valueAsString); }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[BigDecimal](valueAsString))}
  }
  def canParseBigDecimal(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)

    val messages = Messages.of
    this.evaluateParsingBigDecimal(key, valueAsString, messages)

    !messages.hasErrors
  }

  def tryParseLong(key: MessageKey, valueAsString: String, messages: Messages): Option[Long] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingLong(key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseLongWithErrorKey(key, valueAsString)) }
  }
  def parseLongWithErrorKey(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(StringUtils.canParseLong(key, valueAsString))

    valueAsString.toLong
  }
  def parseLong(valueAsString: String): Long =
  {
    this.parseLongWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingLong(key: MessageKey, valueAsString: String, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    try   { valueAsString.toLong }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[Long](valueAsString))}
  }
  def canParseLong(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)

    val messages = Messages.of
    this.evaluateParsingLong(key, valueAsString, messages)

    !messages.hasErrors
  }

  def tryParseInt(key: MessageKey, valueAsString: String, messages: Messages): Option[Int] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingInt(key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseIntWithErrorKey(key, valueAsString)) }
  }
  def parseIntWithErrorKey(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(StringUtils.canParseInt(key, valueAsString))

    valueAsString.toInt
  }
  def parseInt(valueAsString: String): Int =
  {
    this.parseIntWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingInt(key: MessageKey, valueAsString: String, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    try   { valueAsString.toInt }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[Int](valueAsString))}
  }
  def canParseInt(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)

    val messages = Messages.of
    this.evaluateParsingInt(key, valueAsString, messages)

    !messages.hasErrors
  }

  def tryParseBoolean(key: MessageKey, valueAsString: String, messages: Messages): Option[Boolean] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingBoolean(key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseBooleanWithErrorKey(key, valueAsString)) }
  }
  def parseBooleanWithErrorKey(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(StringUtils.canParseBoolean(key, valueAsString))

    valueAsString.toBoolean
  }
  def parseBoolean(valueAsString: String): Boolean =
  {
    this.parseBooleanWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingBoolean(key: MessageKey, valueAsString: String, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    try   { valueAsString.toBoolean }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[Boolean](valueAsString))}
  }
  def canParseBoolean(key: MessageKey, valueAsString: String) =
  {
    argumentIsNotNull(key)

    val messages = Messages.of
    this.evaluateParsingBoolean(key, valueAsString, messages)

    !messages.hasErrors
  }

  def tryParseDateTime(key: MessageKey, valueAsString: String, messages: Messages): Option[DateTime] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingDateTime(key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseDateTimeWithErrorKey(key, valueAsString)) }
  }
  def parseDateTimeWithErrorKey(key: MessageKey, valueAsString: String): DateTime =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(StringUtils.canParseDateTime(key, valueAsString))

    StringUtils.parseDateTimeWithErrorKey(key, valueAsString, DateUtils.dateTimeFormatterWithSeconds)
  }
  def parseDateTime(valueAsString: String): DateTime =
  {
    this.parseDateTimeWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingDateTime(key: MessageKey, valueAsString: String, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    try   { StringUtils.parseDateTimeWithErrorKey(key, valueAsString, DateUtils.dateTimeFormatterWithSeconds) }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[DateTime](valueAsString))}
  }
  def canParseDateTime(key: MessageKey, valueAsString: String): Boolean =
  {
    argumentIsNotNull(key)

    val messages = Messages.of
    this.evaluateParsingDateTime(key, valueAsString, messages)

    !messages.hasErrors
  }

  def tryParseDateTime(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter, messages: Messages): Option[DateTime] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(formatter)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingDateTime(key, valueAsString, formatter, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseDateTimeWithErrorKey(key, valueAsString, formatter)) }
  }
  def parseDateTimeWithErrorKey(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter): DateTime =
  {
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(formatter)
    argumentIsTrue(StringUtils.canParseDateTime(key, valueAsString, formatter))

    formatter.parseDateTime(valueAsString)
  }
  def parseDateTime(valueAsString: String, formatter: DateTimeFormatter): DateTime =
  {
    this.parseDateTimeWithErrorKey(ERROR_DEFAULT_KEY, valueAsString, formatter)
  }
  def evaluateParsingDateTime(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(formatter)
    argumentIsNotNull(messages)

    try   { formatter.parseDateTime(valueAsString) }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[DateTime](valueAsString))}
  }
  def canParseDateTime(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter): Boolean =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(formatter)

    val messages = Messages.of
    this.evaluateParsingDateTime(key, valueAsString, formatter, messages)

    !messages.hasErrors
  }

  def tryParseLocalDate(key: MessageKey, valueAsString: String, messages: Messages): Option[LocalDate] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingLocalDate(key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseLocalDateWithErrorKey(key, valueAsString)) }
  }
  def parseLocalDateWithErrorKey(key: MessageKey, valueAsString: String): LocalDate =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(StringUtils.canParseLocalDate(key, valueAsString))

    StringUtils.parseLocalDateWithErrorKey(key, valueAsString, DateUtils.dateTimeFormatter)
  }
  def parseLocalDate(valueAsString: String): LocalDate =
  {
    this.parseLocalDateWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingLocalDate(key: MessageKey, valueAsString: String, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    try   { StringUtils.parseLocalDateWithErrorKey(key, valueAsString, DateUtils.dateTimeFormatter) }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[LocalDate](valueAsString))}
  }
  def canParseLocalDate(key: MessageKey, valueAsString: String): Boolean =
  {
    argumentIsNotNull(key)

    val messages = Messages.of
    this.evaluateParsingLocalDate(key, valueAsString, messages)

    !messages.hasErrors
  }

  def tryParseLocalDate(key: MessageKey, valueAsString: Option[String], messages: Messages): Option[LocalDate] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(valueAsString.isDefined)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingLocalDate(key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseLocalDateWithErrorKey(key, valueAsString)) }
  }
  def parseLocalDateWithErrorKey(key: MessageKey, valueAsString: Option[String]): LocalDate =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(valueAsString.isDefined)
    argumentIsTrue(StringUtils.canParseLocalDate(key, valueAsString))

    StringUtils.parseLocalDateWithErrorKey(key, valueAsString.get, DateUtils.dateTimeFormatter)
  }
  def parseLocalDate(valueAsString: Option[String]): LocalDate =
  {
    this.parseLocalDateWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingLocalDate(key: MessageKey, valueAsString: Option[String], messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    try   { StringUtils.parseLocalDateWithErrorKey(key, valueAsString.get, DateUtils.dateTimeFormatter) }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[LocalDate](valueAsString.get))}
  }
  def canParseLocalDate(key: MessageKey, valueAsString: Option[String]): Boolean =
  {
    argumentIsNotNull(key)

    val messages = Messages.of
    this.evaluateParsingLocalDate(key, valueAsString, messages)

    !messages.hasErrors
  }

  def tryParseLocalDate(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter, messages: Messages): Option[LocalDate] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(formatter)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingLocalDate(key, valueAsString, formatter, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseLocalDateWithErrorKey(key, valueAsString, formatter)) }
  }
  def parseLocalDateWithErrorKey(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter): LocalDate =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(formatter)
    argumentIsTrue(StringUtils.canParseLocalDate(key, valueAsString, formatter))

    formatter.parseLocalDate(valueAsString)
  }
  def parseLocalDate(valueAsString: String, formatter: DateTimeFormatter): LocalDate =
  {
    this.parseLocalDateWithErrorKey(ERROR_DEFAULT_KEY, valueAsString, formatter)
  }
  def evaluateParsingLocalDate(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter, messages: Messages)
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(formatter)
    argumentIsNotNull(messages)

    try   { formatter.parseLocalDate(valueAsString) }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[LocalDate](valueAsString))}
  }
  def canParseLocalDate(key: MessageKey, valueAsString: String, formatter: DateTimeFormatter) =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(formatter)

    val messages = Messages.of
    this.evaluateParsingLocalDate(key, valueAsString, formatter, messages)

    !messages.hasErrors
  }

  def tryParseEnum[TEnum <: Enum[TEnum]](key: MessageKey, valueAsString: String, messages: Messages)(implicit classManifest: Manifest[TEnum]): Option[TEnum] =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)

    val localMessages = Messages.of(messages)
    this.evaluateParsingEnum[TEnum](key, valueAsString, localMessages)

    if(localMessages.hasErrors) { None } else { Some(this.parseEnumWithErrorKey[TEnum](key, valueAsString)) }
  }
  def parseEnumWithErrorKey[TEnum <: Enum[TEnum]](key: MessageKey, valueAsString: String)(implicit classManifest: Manifest[TEnum]): TEnum =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsTrue(StringUtils.canParseEnum(key, valueAsString))
    argumentIsNotNull(classManifest)

    val enumClass = classManifest.runtimeClass.asInstanceOf[Class[TEnum]]
    Enum.valueOf[TEnum](enumClass, valueAsString)
  }
  def parseEnum[TEnum <: Enum[TEnum]](valueAsString: String)(implicit classManifest: Manifest[TEnum]): TEnum =
  {
    this.parseEnumWithErrorKey(ERROR_DEFAULT_KEY, valueAsString)
  }
  def evaluateParsingEnum[TEnum <: Enum[TEnum]](key: MessageKey, valueAsString: String, messages: Messages)(implicit classManifest: Manifest[TEnum])
  {
    argumentIsNotNull(key)
    argumentIsNotNull(valueAsString)
    argumentIsNotNull(messages)
    argumentIsNotNull(classManifest)

    val enumClass = classManifest.runtimeClass.asInstanceOf[Class[TEnum]]

    try   { Enum.valueOf[TEnum](enumClass, valueAsString) }
    catch { case _: Throwable => messages.putError(key, ErrorMessages.createDefaultParsingError[TEnum](valueAsString))}
  }
  def canParseEnum[TEnum <: Enum[TEnum]](key: MessageKey, valueAsString: String)(implicit classManifest: Manifest[TEnum]) =
  {
    argumentIsNotNull(key)
    argumentIsNotNull(classManifest)

    val messages = Messages.of
    this.evaluateParsingEnum[TEnum](key, valueAsString, messages)

    !messages.hasErrors
  }

  def canParseEnum[TEnum <: Enum[TEnum]](valueAsString: String)(implicit classManifest: Manifest[TEnum]) =
  {
    argumentIsNotNull(classManifest)

    val messages = Messages.of
    this.evaluateParsingEnum[TEnum](ERROR_DEFAULT_KEY, valueAsString, messages)

    !messages.hasErrors
  }
}
