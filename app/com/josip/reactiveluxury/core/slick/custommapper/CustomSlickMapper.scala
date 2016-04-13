package com.josip.reactiveluxury.core.slick.custommapper

import com.github.tminglei.slickpg.Interval
import com.josip.reactiveluxury.core.utils.StringUtils
import org.joda.time.{LocalDate, Period, DurationFieldType}
import play.api.libs.json.{Json, JsValue}
import slick.driver.PostgresDriver
import com.josip.reactiveluxury.core.slick.postgres.MyPostgresDriver.api._

object CustomSlickMapper
{
  object Postgres
  {
    implicit def enumColumnType[T<: Enum[T]](implicit classManifest: Manifest[T]): ColumnType[T] =
    {
      PostgresDriver.MappedColumnType.base[T, String](
        { adt => adt.name                                    },
        { dv  => StringUtils.parseEnum[T](dv)(classManifest) }
      )
    }

    implicit val jodaDateTimeColumnType = PostgresDriver.MappedColumnType.base[org.joda.time.DateTime, java.sql.Timestamp](
      { dt => new java.sql.Timestamp(dt.getMillis) },
      { ts => new org.joda.time.DateTime(ts)       }
    )

    implicit val jodaLocalDateColumnType = PostgresDriver.MappedColumnType.base[org.joda.time.LocalDate, java.sql.Date](
      { dt => new java.sql.Date(dt.toDateTimeAtStartOfDay.getMillis) },
      { d => new LocalDate(d.getTime)       }
    )

    implicit val jsValueColumnType = PostgresDriver.MappedColumnType.base[JsValue, String](
      { jsv =>  jsv.toString()  },
      { sv => Json.parse(sv)    }
    )

    implicit val jodaPeriodTimeColumnType = PostgresDriver.MappedColumnType.base[org.joda.time.Period, Interval](
      { period =>
        val years = period.get(DurationFieldType.years())
        val months = period.get(DurationFieldType.months())
        val days = period.get(DurationFieldType.days())
        val hours = period.get(DurationFieldType.hours())
        val mins = period.get(DurationFieldType.minutes())
        val secs = period.get(DurationFieldType.seconds())

        Interval(
          years = years,
          months = months,
          days = days,
          hours = hours,
          minutes = mins,
          seconds = secs
        )
      },
      { interval =>
        val years = interval.years
        val months = interval.months
        val days = interval.days
        val hours = interval.hours
        val mins = interval.minutes
        val secs = interval.seconds.toInt
        val millis = interval.milliseconds

        new Period(years, months, 0, days, hours, mins, secs, millis);
      }
    )
  }
}