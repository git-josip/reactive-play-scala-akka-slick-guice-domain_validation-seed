package com.josip.reactiveluxury.core.utils

import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.messages.{MessageKey, Messages}
import scala.util.matching.Regex
import Asserts._

object ValidateUtils
{
  val EMPTY_ERROR_KEY = ""
  private final val REGEX_MATCHING_VALID_MAIL = """^([a-z0-9_\.-]+)\+?(([a-z0-9_\.-]+))?@([\da-z\.-]+)\.([a-z\.]{2,6})$"""

  object ValidateMessages
  {
    val THIS_VALUE_IS_REQUIRED = "Value is required."

    val REGEX_MATCHING_VALID_MAIL_ERROR = "Invalid mail"

    val VALUE_MUST_BE_NULL                      = "Value must be null"
    val VALUE_MUST_NOT_BE_NULL                  = "Value must not be null"
    val VALUE_MUST_BE_TRUE                      = "Value must be true"
    val VALUE_MUST_BE_FALSE                     = "Value must be false"
    val VALUE_MUST_BE_EMPTY_STRING              = "Value must be empty string"
    val VALUE_MUST_NOT_BE_EMPTY_STRING          = "Value must not be empty string"
    val VALUE_MUST_NOT_BE_NULL_NOR_EMPTY_STRING = "Value must not be null nor empty string"
    val VALIDATION_FAILED                       = "Validation failed"

    val VALUES_MUST_BE_EQUAL_FORMATTER                = "Value %s must be equal to '%s'"
    val SEQ_SIZE_TO_SMALL_FORMATTER                   = "Size must be %s or bigger. Actual: %s"
    val VALUE_IS_NOT_IN_RANGE_INCLUSIVE_FORMATTER     = "Value '%s' is not in valid inclusive range (%s, %s)"
    val VALUE_IS_NOT_IN_RANGE_EXCLUSIVE_FORMATTER     = "Value '%s' is not in valid exclusive range (%s, %s)"
    val VALUE_MUST_BE_BIGGER_THAN_FORMATTER           = "Value '%s' must be bigger than '%s'"
    val VALUE_MUST_BE_BIGGER_THAN_OR_EQUAL_FORMATTER  = "Value '%s' must be bigger than or equal to '%s'"
    val VALUE_MUST_BE_LESS_THAN_FORMATTER             = "Value '%s' must be less than '%s'"
    val VALUE_MUST_BE_LESS_THAN_OR_EQUAL_FORMATTER    = "Value '%s' must be less than or equal to '%s'"

    val SCALE_IS_NOT_VALID_ERROR_MESSAGE_FORMATTER    = "Maximum allowed number of decimal places is: '%s', Currently: '%s' "

    val VALUE_LENGTH_MUST_BE_LESS_THAN_FORMATTER            = "Length must be less than '%s'. Actual '%s'"
    val VALUE_LENGTH_MUST_BE_LESS_THAN_OR_EQUAL_FORMATTER   = "Length must be less than or equal to '%s'. Actual '%s'"
    val VALUE_LENGTH_MUST_BE_BIGGER_THAN_FORMATTER          = "Length must be bigger than '%s'. Actual '%s'"
    val VALUE_LENGTH_MUST_BE_BIGGER_THAN_OR_EQUAL_FORMATTER = "Length must be bigger than or equal to '%s'. Actual '%s'"
  }

  private object ValidationHelpers
  {
    def isNull[T](value: T) =
    {
      value == null
    }

    def isNotNull[T](value: T) =
    {
      !ValidationHelpers.isNull(value)
    }

    def isTrue(value: Boolean) =
    {
      argumentIsNotNull(value)

      value.equals(true)
    }

    def isFalse(value: Boolean) =
    {
      argumentIsNotNull(value)

      !ValidationHelpers.isTrue(value)
    }

    def isValid(value: Boolean) =
    {
      argumentIsNotNull(value)

      !ValidationHelpers.isFalse(value)
    }

    def isEmpty(value: String) =
    {
      argumentIsNotNull(value)

      StringUtils.isEmpty(value)
    }

    def isNotEmpty(value: String) =
    {
      argumentIsNotNull(value)

      !ValidationHelpers.isEmpty(value)
    }

    def isMatchingRegex(value: String, regexPattern: Regex) =
    {
      argumentIsNotNull(value)
      argumentIsNotNull(regexPattern)

      value.matches(regexPattern.toString)
    }

    def isNotNullNorEmpty(value: String) =
    {
      !ValidationHelpers.isNull(value) && !ValidationHelpers.isEmpty(value)
    }

    def areEqual[T](value: T, otherValue: T) =
    {
      (value == null && otherValue == null) || (value != null && value.equals(otherValue))
    }

    def isInRangeInclusive[T](value: T, minValue: T, maxValue: T) =
    {
      value match {
        case checkingValue: Int         => checkingValue >= minValue.asInstanceOf[Int]         && checkingValue <= maxValue.asInstanceOf[Int]
        case checkingValue: Long        => checkingValue >= minValue.asInstanceOf[Long]        && checkingValue <= maxValue.asInstanceOf[Long]
        case checkingValue: BigDecimal  => checkingValue >= minValue.asInstanceOf[BigDecimal]  && checkingValue <= maxValue.asInstanceOf[BigDecimal]
        case checkingValue: BigInt      => checkingValue >= minValue.asInstanceOf[BigInt]      && checkingValue <= maxValue.asInstanceOf[BigInt]
        case checkingValue: Float       => checkingValue >= minValue.asInstanceOf[Float]       && checkingValue <= maxValue.asInstanceOf[Float]
        case checkingValue: Double      => checkingValue >= minValue.asInstanceOf[Double]      && checkingValue <= maxValue.asInstanceOf[Double]
        case _ => throw new IllegalStateException("Is not valid numeric type")
      }
    }

    def isInRangeExclusive[T](value: T, minValue: T, maxValue: T) =
    {
      value match {
        case checkingValue: Int         => checkingValue > minValue.asInstanceOf[Int]         && checkingValue < maxValue.asInstanceOf[Int]
        case checkingValue: Long        => checkingValue > minValue.asInstanceOf[Long]        && checkingValue < maxValue.asInstanceOf[Long]
        case checkingValue: BigDecimal  => checkingValue > minValue.asInstanceOf[BigDecimal]  && checkingValue < maxValue.asInstanceOf[BigDecimal]
        case checkingValue: BigInt      => checkingValue > minValue.asInstanceOf[BigInt]      && checkingValue < maxValue.asInstanceOf[BigInt]
        case checkingValue: Float       => checkingValue > minValue.asInstanceOf[Float]       && checkingValue < maxValue.asInstanceOf[Float]
        case checkingValue: Double      => checkingValue > minValue.asInstanceOf[Double]      && checkingValue < maxValue.asInstanceOf[Double]
        case _ => throw new IllegalStateException("Is not valid numeric type")
      }
    }

    def isBiggerThan[T](value: T, minValue: T) =
    {
      value match {
        case checkingValue: Int         => checkingValue > minValue.asInstanceOf[Int]
        case checkingValue: Long        => checkingValue > minValue.asInstanceOf[Long]
        case checkingValue: BigDecimal  => checkingValue > minValue.asInstanceOf[BigDecimal]
        case checkingValue: BigInt      => checkingValue > minValue.asInstanceOf[BigInt]
        case checkingValue: Float       => checkingValue > minValue.asInstanceOf[Float]
        case checkingValue: Double      => checkingValue > minValue.asInstanceOf[Double]
        case _ => throw new IllegalStateException("Is not valid numeric type")
      }
    }

    def isBiggerThanOrEqual[T](value: T, minValue: T) =
    {
      value match {
        case checkingValue: Int         => checkingValue >= minValue.asInstanceOf[Int]
        case checkingValue: Long        => checkingValue >= minValue.asInstanceOf[Long]
        case checkingValue: BigDecimal  => checkingValue >= minValue.asInstanceOf[BigDecimal]
        case checkingValue: BigInt      => checkingValue >= minValue.asInstanceOf[BigInt]
        case checkingValue: Float       => checkingValue >= minValue.asInstanceOf[Float]
        case checkingValue: Double      => checkingValue >= minValue.asInstanceOf[Double]
        case _ => throw new IllegalStateException("Is not valid numeric type")
      }
    }

    def isLessThan[T](value: T, maxValue: T) =
    {
      value match {
        case checkingValue: Int         => checkingValue < maxValue.asInstanceOf[Int]
        case checkingValue: Long        => checkingValue < maxValue.asInstanceOf[Long]
        case checkingValue: BigDecimal  => checkingValue < maxValue.asInstanceOf[BigDecimal]
        case checkingValue: BigInt      => checkingValue < maxValue.asInstanceOf[BigInt]
        case checkingValue: Float       => checkingValue < maxValue.asInstanceOf[Float]
        case checkingValue: Double      => checkingValue < maxValue.asInstanceOf[Double]
        case _ => throw new IllegalStateException("Is not valid numeric type")
      }
    }
    def isLessThanOrEqual[T](value: T, maxValue: T) =
    {
      value match {
        case checkingValue: Int         => checkingValue <= maxValue.asInstanceOf[Int]
        case checkingValue: Long        => checkingValue <= maxValue.asInstanceOf[Long]
        case checkingValue: BigDecimal  => checkingValue <= maxValue.asInstanceOf[BigDecimal]
        case checkingValue: BigInt      => checkingValue <= maxValue.asInstanceOf[BigInt]
        case checkingValue: Float       => checkingValue <= maxValue.asInstanceOf[Float]
        case checkingValue: Double      => checkingValue <= maxValue.asInstanceOf[Double]
        case _ => throw new IllegalStateException("Is not valid numeric type")
      }
    }
  }

  def validateEmail(email: String, errorKey: MessageKey, messages: Messages)
  {
    argumentIsNotNull(email)
    argumentIsNotNull(errorKey)
    argumentIsNotNull(messages)

    this.isMatchingRegex(
      email,
      REGEX_MATCHING_VALID_MAIL.r,
      messages,
      errorKey.value,
      ValidateMessages.REGEX_MATCHING_VALID_MAIL_ERROR
    )
  }

  def validate[T](value: T, validateFunction: (T => Boolean), messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(validateFunction)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, validateFunction(value), messages, errorMessage)
  }
  def validate[T](value: T, validateFunction: (T => Boolean), messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(validateFunction)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, validateFunction(value), messages, errorKey, errorMessage)
  }
  def validate[T](value: T, validationResult: Boolean, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(validationResult)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    validate(value, validationResult, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validate[T](value: T, validationResult: Boolean, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(validationResult)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    validationResult match {
      case false  => if(errorKey.isEmpty) messages.putError(errorMessage) else messages.putError(MessageKey(errorKey), errorMessage)
      case true   =>
    }
  }
  def validate[T](errorKey: MessageKey, value: T, validationResult: Boolean, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(errorKey)
    argumentIsNotNull(validationResult)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    if(!validationResult) { messages.putError(errorKey, errorMessage) }
  }

  def validateIsOptionDefined(errorKey: MessageKey, value: Option[_], messages: Messages)
  {
    argumentIsNotNull(errorKey)
    argumentIsNotNull(value)
    argumentIsNotNull(messages)

    val validationResult = value.isDefined
    ValidateUtils.validate(errorKey, value, validationResult, messages, ValidateMessages.THIS_VALUE_IS_REQUIRED)
  }
  def validateIsOptionDefined(value: Option[_], messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsOptionDefined(value, messages, ValidateMessages.THIS_VALUE_IS_REQUIRED)
  }
  def validateIsOptionDefined(value: Option[_], messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsOptionDefined(value, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateIsOptionDefined(value: Option[_], messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNull(errorKey)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = value.isDefined
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def isNull[T](value: T, messages: Messages)
  {
    argumentIsNotNull(messages)

    ValidateUtils.isNull(value, messages, ValidateMessages.VALUE_MUST_BE_NULL)
  }
  def isNull[T](value: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNullNorEmpty(errorMessage)
    argumentIsNotNull(messages)

    ValidateUtils.validate(value, ValidationHelpers.isNull _, messages, errorMessage)
  }
  def isNull[T](value: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorKey)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isNull _, messages,  errorMessage)
  }

  def isNotNull[T](value: T, messages: Messages)
  {
    argumentIsNotNull(messages)

    ValidateUtils.isNotNull(value, messages, ValidateMessages.VALUE_MUST_NOT_BE_NULL)
  }
  def isNotNull[T](value: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNullNorEmpty(errorMessage)
    argumentIsNotNull(messages)

    ValidateUtils.validate(value, ValidationHelpers.isNotNull _, messages, errorMessage)
  }
  def isNotNull[T](value: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNullNorEmpty(errorMessage)
    argumentIsNotNull(messages)

    ValidateUtils.validate(value, ValidationHelpers.isNotNull _, messages, errorKey, errorMessage)
  }

  def isTrue(value: Boolean, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)

    ValidateUtils.isTrue(value, messages, ValidateMessages.VALUE_MUST_BE_TRUE)
  }
  def isTrue(value: Boolean, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate[Boolean](value, ValidationHelpers.isTrue _, messages, errorMessage)
  }
  def isTrue(value: Boolean, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate[Boolean](value, ValidationHelpers.isTrue _, messages, errorKey, errorMessage)
  }

  def isFalse(value: Boolean, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)

    ValidateUtils.isFalse(value, messages, ValidateMessages.VALUE_MUST_BE_FALSE)
  }
  def isFalse(value: Boolean, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isFalse _, messages, errorMessage)
  }
  def isFalse(value: Boolean, messages: Messages, errorKey: String, errorMessage: String) {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorKey)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isFalse _, messages, errorKey, errorMessage)
  }

  def isValid(value: Boolean, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)

    ValidateUtils.isValid(value, messages, ValidateMessages.VALIDATION_FAILED)
  }
  def isValid(value: Boolean, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isValid _, messages, errorMessage)
  }
  def isValid(value: Boolean, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isValid _, messages, errorKey, errorMessage)
  }

  def isEmpty(value: String, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)

    ValidateUtils.isEmpty(value, messages, ValidateMessages.VALUE_MUST_BE_EMPTY_STRING)
  }
  def isEmpty(value: String, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isEmpty _, messages, errorMessage)
  }
  def isEmpty(value: String, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isEmpty _, messages, errorMessage)
  }

  def isNotEmpty(value: String, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)

    ValidateUtils.isNotEmpty(value, messages, ValidateMessages.VALUE_MUST_NOT_BE_EMPTY_STRING)
  }
  def isNotEmpty(value: String, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isNotEmpty _, messages, errorMessage)
  }
  def isNotEmpty(value: String, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isNotEmpty _, messages, errorKey, errorMessage)
  }

  def isMatchingRegex(value: String, regexPattern: Regex, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNull(regexPattern)

    ValidateUtils.isMatchingRegex(value, regexPattern, messages, ValidateMessages.VALUE_MUST_NOT_BE_EMPTY_STRING)
  }
  def isMatchingRegex(value: String, regexPattern: Regex, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)
    argumentIsNotNull(regexPattern)

    val validateMethod = (v: String) => { ValidationHelpers.isMatchingRegex(v, regexPattern) }
    ValidateUtils.validate(value, validateMethod, messages, errorMessage)
  }
  def isMatchingRegex(value: String, regexPattern: Regex, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)
    argumentIsNotNull(regexPattern)

    val validateMethod = (v: String) => { ValidationHelpers.isMatchingRegex(v, regexPattern) }
    ValidateUtils.validate(value, validateMethod, messages, errorKey, errorMessage)
  }

  def isNotNullNorEmpty(value: String, messages: Messages)
  {
    argumentIsNotNull(messages)

    ValidateUtils.isNotNullNorEmpty(value, messages, ValidateMessages.VALUE_MUST_NOT_BE_NULL_NOR_EMPTY_STRING)
  }
  def isNotNullNorEmpty(value: String, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isNotNullNorEmpty _, messages, errorMessage)
  }
  def isNotNullNorEmpty(value: String, messages: Messages, errorKey: String, errorMessage: String) {
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validate(value, ValidationHelpers.isNotNullNorEmpty _, messages, errorKey, errorMessage)
  }

  def validateAreEqual[T](value: T, otherValue: T, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(otherValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateAreEqual(value, otherValue, messages, String.format(ValidateMessages.VALUES_MUST_BE_EQUAL_FORMATTER, value.toString, otherValue.toString))
  }
  def validateAreEqual[T](value: T, otherValue: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(otherValue)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.areEqual(value, otherValue)
    ValidateUtils.validate(value, validationResult, messages, errorMessage)
  }
  def validateAreEqual[T](value: T, otherValue: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(otherValue)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.areEqual(value, otherValue)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }


  def validateIsInRangeInclusive[T](value: T, minValue: T, maxValue: T, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsInRangeInclusive(value, minValue, maxValue, messages, EMPTY_ERROR_KEY, String.format(ValidateMessages.VALUE_IS_NOT_IN_RANGE_INCLUSIVE_FORMATTER, value.toString, minValue.toString, maxValue.toString))
  }
  def validateIsInRangeInclusive[T](value: T, minValue: T, maxValue: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsInRangeInclusive(value, minValue, maxValue, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateIsInRangeInclusive[T](value: T, minValue: T, maxValue: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(errorMessage)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.isInRangeInclusive(value, minValue, maxValue)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def validateIsInRangeExclusive[T](value: T, minValue: T, maxValue: T, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsInRangeExclusive(value, minValue, maxValue, messages, EMPTY_ERROR_KEY, String.format(ValidateMessages.VALUE_IS_NOT_IN_RANGE_EXCLUSIVE_FORMATTER, value.toString, minValue.toString, maxValue.toString))
  }
  def validateIsInRangeExclusive[T](value: T, minValue: T, maxValue: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsInRangeExclusive(value, minValue, maxValue, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateIsInRangeExclusive[T](value: T, minValue: T, maxValue: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(errorMessage)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.isInRangeExclusive(value, minValue, maxValue)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def validateScale(value: BigDecimal, maximumScale: Int, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maximumScale)
    argumentIsNotNull(messages)

    ValidateUtils.validateScale(value, maximumScale, messages, String.format(ValidateMessages.SCALE_IS_NOT_VALID_ERROR_MESSAGE_FORMATTER, maximumScale.toString, value.scale.toString))
  }
  def validateScale(value: BigDecimal, maximumScale: Int, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maximumScale)
    argumentIsNotNull(messages)

    ValidateUtils.validateScale(value, maximumScale, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateScale(value: BigDecimal, maximumScale: Int, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maximumScale)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.isLessThanOrEqual(value.scale, maximumScale)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def validateIsBiggerThan[T](value: T, minValue: T, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsBiggerThan(value, minValue, messages, String.format(ValidateMessages.VALUE_MUST_BE_BIGGER_THAN_FORMATTER, value.toString, minValue.toString))
  }
  def validateIsBiggerThan[T](value: T, minValue: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsBiggerThan(value, minValue, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateIsBiggerThan[T](value: T, minValue: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.isBiggerThan(value, minValue)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def validateIsBiggerThanOrEqual[T](value: T, minValue: T, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsBiggerThanOrEqual(value, minValue, messages, String.format(ValidateMessages.VALUE_MUST_BE_BIGGER_THAN_OR_EQUAL_FORMATTER, value.toString, minValue.toString))
  }
  def validateIsBiggerThanOrEqual[T](value: T, minValue: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsBiggerThanOrEqual(value, minValue, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateIsBiggerThanOrEqual[T](value: T, minValue: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minValue)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.isBiggerThanOrEqual(value, minValue)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def validateSizeIsLessThan[T](value: Seq[T], minSize: Int, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minSize)
    argumentIsTrue(minSize >= 0)
    argumentIsNotNull(messages)

    ValidateUtils.validateSizeIsLessThan(value, minSize, messages, String.format(ValidateMessages.SEQ_SIZE_TO_SMALL_FORMATTER, minSize.toString, value.size.toString))
  }
  def validateSizeIsLessThan[T](value: Seq[T], minSize: Int, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minSize)
    argumentIsTrue(minSize >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsLessThan(value.size, minSize, messages, errorMessage)
  }
  def validateSizeIsLessThan[T](value: Seq[T], minSize: Int, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minSize)
    argumentIsTrue(minSize >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsLessThan(value.size, minSize, messages, errorKey, errorMessage)
  }

  def validateIsLessThan[T](value: T, maxValue: T, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsLessThan(value, maxValue, messages, String.format(ValidateMessages.VALUE_MUST_BE_LESS_THAN_FORMATTER, value.toString, maxValue.toString))
  }
  def validateIsLessThan[T](value: T, maxValue: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsLessThan(value, maxValue, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateIsLessThan[T](value: T, maxValue: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.isLessThan(value, maxValue)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def validateIsLessThanOrEqual[T](value: T, maxValue: T, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsLessThanOrEqual(value, maxValue, messages, String.format(ValidateMessages.VALUE_MUST_BE_LESS_THAN_OR_EQUAL_FORMATTER, value.toString, maxValue.toString))
  }
  def validateIsLessThanOrEqual[T](value: T, maxValue: T, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)

    ValidateUtils.validateIsLessThanOrEqual(value, maxValue, messages, EMPTY_ERROR_KEY, errorMessage)
  }
  def validateIsLessThanOrEqual[T](value: T, maxValue: T, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(maxValue)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    val validationResult = ValidationHelpers.isLessThanOrEqual(value, maxValue)
    ValidateUtils.validate(value, validationResult, messages, errorKey, errorMessage)
  }

  def validateLengthIsLessThan(value: String, minLength: Int, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)

    ValidateUtils.validateLengthIsLessThan(value, minLength, messages, String.format(ValidateMessages.VALUE_LENGTH_MUST_BE_LESS_THAN_FORMATTER, minLength.toString, value.length.toString))
  }
  def validateLengthIsLessThan(value: String, minLength: Int, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)

    ValidateUtils.validateLengthIsLessThan(value, minLength, messages, EMPTY_ERROR_KEY, String.format(ValidateMessages.VALUE_LENGTH_MUST_BE_LESS_THAN_FORMATTER, minLength.toString, value.length.toString))
  }
  def validateLengthIsLessThan(value: String, minLength: Int, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsLessThan(value.length, minLength, messages, errorKey, errorMessage)
  }

  def validateLengthIsLessThanOrEqual(value: String, minLength: Int, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)

    ValidateUtils.validateLengthIsLessThanOrEqual(value, minLength, messages, String.format(ValidateMessages.VALUE_LENGTH_MUST_BE_LESS_THAN_OR_EQUAL_FORMATTER, minLength.toString, value.length.toString))
  }
  def validateLengthIsLessThanOrEqual(value: String, minLength: Int, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsLessThanOrEqual(value.length, minLength, messages, errorMessage)
  }
  def validateLengthIsLessThanOrEqual(value: String, minLength: Int, messages: Messages, errorKey:String, errorMessage: String) {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsLessThanOrEqual(value.length, minLength, messages, errorKey, errorMessage)
  }

  def validateLengthIsBiggerThan(value: String, minLength: Int, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)

    ValidateUtils.validateLengthIsBiggerThan(value, minLength, messages, String.format(ValidateMessages.VALUE_LENGTH_MUST_BE_BIGGER_THAN_FORMATTER, minLength.toString, value.length.toString))
  }
  def validateLengthIsBiggerThan(value: String, minLength: Int, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsBiggerThan(value.length, minLength, messages, errorMessage)
  }
  def validateLengthIsBiggerThan(value: String, minLength: Int, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsBiggerThan(value.length, minLength, messages, errorKey, errorMessage)
  }

  def validateLengthIsBiggerThanOrEqual(value: String, minLength: Int, messages: Messages)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)

    ValidateUtils.validateLengthIsBiggerThanOrEqual(value, minLength, messages, String.format(ValidateMessages.VALUE_LENGTH_MUST_BE_BIGGER_THAN_OR_EQUAL_FORMATTER, minLength.toString, value.length.toString))
  }
  def validateLengthIsBiggerThanOrEqual(value: String, minLength: Int, messages: Messages, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsBiggerThanOrEqual(value.length, minLength, messages, errorMessage)
  }
  def validateLengthIsBiggerThanOrEqual(value: String, minLength: Int, messages: Messages, errorKey: String, errorMessage: String)
  {
    argumentIsNotNull(value)
    argumentIsNotNull(minLength)
    argumentIsTrue(minLength >= 0)
    argumentIsNotNull(messages)
    argumentIsNotNullNorEmpty(errorMessage)

    ValidateUtils.validateIsBiggerThanOrEqual(value.length, minLength, messages, errorKey, errorMessage)
  }
}
