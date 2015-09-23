package com.josip.reactiveluxury.core.utils

import com.josip.reactiveluxury.core.Asserts
import Asserts._
import java.math.{BigDecimal => JavaBigDecimal}

object BigDecimalUtils
{
  def optionScalaToOptionJava(value: Option[BigDecimal]): Option[JavaBigDecimal] =
  {
    argumentIsNotNull(value)

   if(value.isDefined) Option(BigDecimalUtils.scalaToJava(value.get)) else Option.empty
  }

  def scalaToJava(value: BigDecimal) =
  {
    argumentIsNotNull(value)

    new JavaBigDecimal(value.toString)
  }

  def optionJavaToOptionScala(value: Option[JavaBigDecimal]): Option[BigDecimal] =
  {
    argumentIsNotNull(value)

    if(value.isDefined) Option(BigDecimalUtils.javaToScala(value.get)) else Option.empty
  }

  def javaToScala(value: JavaBigDecimal) =
  {
    argumentIsNotNull(value)

    BigDecimal(value)
  }
}
