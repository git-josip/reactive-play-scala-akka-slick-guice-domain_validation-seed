package com.josip.reactiveluxury.core.slick.custommapper

import com.josip.reactiveluxury.core.utils.StringUtils
import play.api.libs.json.{Json, JsValue}

import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._

object CustomSlickMapper
{
  object Postgres
  {
    def enumColumnType[T<: Enum[T]](implicit classManifest: Manifest[T]): ColumnType[T] =
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

    implicit val jsValueColumnType = PostgresDriver.MappedColumnType.base[JsValue, String](
      { jsv =>  jsv.toString()  },
      { sv => Json.parse(sv)    }
    )
  }
}
