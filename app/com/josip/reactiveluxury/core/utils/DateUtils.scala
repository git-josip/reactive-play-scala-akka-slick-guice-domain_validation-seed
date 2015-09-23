package com.josip.reactiveluxury.core.utils

import com.josip.reactiveluxury.core.Asserts

import scala.language.implicitConversions
import org.joda.time._
import java.util.Date
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import Asserts._
import java.sql.{Date => SqlDate}

object DateUtils
{
  final val dateTimeFormatter             : DateTimeFormatter = DateTimeFormat.forPattern("dd MMMM YYYY");
  final val dateTimeFormatterWithMinutes  : DateTimeFormatter = DateTimeFormat.forPattern("dd MMMM YYYY, HH:mm");
  final val dateTimeFormatterWithSeconds  : DateTimeFormatter = DateTimeFormat.forPattern("dd MMMM YYYY HH:mm:ss");
  final val DD_MMM_YYYY__dateFormatter    : DateTimeFormatter = DateTimeFormat.forPattern("dd MMM YYYY");
  final val POSTGRES_DATE_FORMAT          : DateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")
  final val HH_mm_DATE_FORMATTER          : DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
  final val YYYY_MM_dd__dateFormatter     : DateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd");

  def nowDateTimeUTC: DateTime =
  {
    DateTime.now(DateTimeZone.UTC)
  }

  def nowLocalDateUTC: LocalDate =
  {
    LocalDate.now(DateTimeZone.UTC)
  }

  def jodaDateTimeToJavaDate(value: Option[DateTime]) : Option[Date] =
  {
    argumentIsNotNull(value)

    value.map(v=>DateUtils.jodaDateTimeToJavaDate(v))
  }
  def jodaDateTimeToJavaDate(value: DateTime): Date =
  {
    argumentIsNotNull(value)

    value.toDate
  }

  def jodaDateTimeToSqlDate(value: Option[DateTime]) : Option[SqlDate] =
  {
    argumentIsNotNull(value)

    value.map(v=>DateUtils.jodaDateTimeToSqlDate(v))
  }
  def jodaDateTimeToSqlDate(value: DateTime): SqlDate =
  {
    argumentIsNotNull(value)

    new SqlDate(value.getMillis)
  }

  def javaDateToJodaDateTime(value: Date): DateTime =
  {
    argumentIsNotNull(value)

    new DateTime(value).withZone(DateTimeZone.UTC)
  }
  def javaDateToJodaDateTime(value: Option[Date]): Option[DateTime] =
  {
    argumentIsNotNull(value)

    value.map(v=>DateUtils.javaDateToJodaDateTime(v))
  }

  def jodaLocalDateToJavaDate(value: Option[LocalDate]) : Option[Date] =
  {
    argumentIsNotNull(value)

    value.map(v=>DateUtils.jodaLocalDateToJavaDate(v))
  }
  def jodaLocalDateToJavaDate(value: LocalDate): Date =
  {
    argumentIsNotNull(value)

    value.toDate
  }
  def javaDateToJodaLocalDate(value: Date): LocalDate =
  {
    argumentIsNotNull(value)

    new LocalDate(value, DateTimeZone.UTC)
  }
  def javaDateToJodaLocalDate(value: Option[Date]): Option[LocalDate] =
  {
    argumentIsNotNull(value)

    value.map(v=>DateUtils.javaDateToJodaLocalDate(v))
  }

  def jodaLocalTimeToJavaDate(value: Option[LocalTime]) : Option[Date] =
  {
    argumentIsNotNull(value)

    value.map(DateUtils.jodaLocalTimeToJavaDate(_))
  }
  def jodaLocalTimeToJavaDate(value: LocalTime): Date =
  {
    argumentIsNotNull(value)

    new Date(value.toDateTimeToday.getMillisOfDay)
  }
  def javaDateToJodaLocalTime(value: Date): LocalTime =
  {
    argumentIsNotNull(value)

    new LocalTime(value)
  }
  def javaDateToJodaLocalTime(value: Option[Date]): Option[LocalTime] =
  {
    argumentIsNotNull(value)

    value.map(DateUtils.javaDateToJodaLocalTime(_))
  }

  def calculateLocalTimeRange(from :LocalTime, to :LocalTime, step :Period) :Seq[LocalTime] =
  {
    Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to)).toList
  }
}
