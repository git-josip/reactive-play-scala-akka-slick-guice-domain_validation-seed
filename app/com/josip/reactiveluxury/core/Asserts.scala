package com.josip.reactiveluxury.core

object Asserts
{
  def argumentIsNotNull[T](arg: T)
  {
    if (arg == null)
      throw new IllegalArgumentException(ExceptionMessages.NULL_ARGUMENT_PASSED)
  }

  def argumentIsNotNull[T](arg: T, argName: String)
  {
    if(argName == null){
      throw new IllegalArgumentException(ExceptionMessages.ARGUMENT_NAME_MUST_NOT_BE_NULL)
    }
    if(argName.isEmpty){
      throw new IllegalArgumentException(ExceptionMessages.ARGUMENT_NAME_MUST_NOT_BE_EMPTY)
    }

    if (arg == null)
      throw new IllegalArgumentException(ExceptionMessages.NULL_ARGUMENT_PASSED_ARGUMENT_NAME_IS + argName)
  }

  def argumentIsTrue(arg: Boolean)
  {
    if (!arg)
      throw new IllegalArgumentException(ExceptionMessages.ARGUMENT_MUST_BE_TRUE)
  }

  def argumentIsTrue(arg: Boolean, message: String)
  {
    argumentIsNotNullNorEmpty(message)

    if (!arg)
      throw new IllegalArgumentException(message)
  }

  def argumentIsNotNullNorEmpty(arg: String)
  {
    if (arg == null )
      throw new IllegalArgumentException(ExceptionMessages.NULL_ARGUMENT_PASSED)
    if (arg.isEmpty)
      throw new IllegalArgumentException(ExceptionMessages.EMPTY_ARGUMENT_PASSED)
  }

  def argumentIsNotNullNorEmpty(arg: String, description: => String)
  {
    if(description == null){
      throw new IllegalArgumentException(ExceptionMessages.DESCRIPTION_MUST_NOT_BE_NULL)
    }
    if(description.isEmpty){
      throw new IllegalArgumentException(ExceptionMessages.DESCRIPTION_MUST_NOT_BE_EMPTY)
    }

    if (arg == null )
      throw new IllegalArgumentException(ExceptionMessages.NULL_ARGUMENT_PASSED + addDescription(description))
    if (arg.isEmpty)
      throw new IllegalArgumentException(ExceptionMessages.EMPTY_ARGUMENT_PASSED + addDescription(description))
  }

  private object ExceptionMessages
  {
    val NULL_ARGUMENT_PASSED                  = "Null argument passed!"
    val NULL_ARGUMENT_PASSED_ARGUMENT_NAME_IS = "Null argument passed! Argument name:"
    val EMPTY_ARGUMENT_PASSED                 = "Empty argument passed!"
    val ARGUMENT_NAME_MUST_NOT_BE_NULL        = "Argument name must not be null"
    val ARGUMENT_NAME_MUST_NOT_BE_EMPTY       = "Argument name must not be empty"
    val ARGUMENT_MUST_BE_TRUE                 = "Argument must be true!"
    val DESCRIPTION_MUST_NOT_BE_NULL          = "Description must not be null"
    val DESCRIPTION_MUST_NOT_BE_EMPTY         = "Description must not be empty"
  }

  private def addDescription(description: => String): String =
  {
    String.format(" (%s)", description)
  }
}
