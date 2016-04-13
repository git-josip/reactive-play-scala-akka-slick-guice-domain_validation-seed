package com.josip.reactiveluxury.core.json.customfomatters

import com.josip.reactiveluxury.core.messages.MessageKey
import com.josip.reactiveluxury.core.utils.{DateUtils, StringUtils}
import org.joda.time.{DateTime, LocalDate, Period}
import play.api.data.validation.ValidationError
import play.api.libs.json._

object CustomFormatter
{
  object Enum {
    def enumWritesByName[TEnum <: Enum[TEnum]]= new Writes[TEnum] {
      def writes(o: TEnum): JsValue = JsString(o.name)
    }

    def enumReadsByName[TEnum <: Enum[TEnum]](implicit classManifest: Manifest[TEnum]) = new Reads[TEnum] {
      def reads(json: JsValue) = json match {
        case JsString(valueCandidate) =>
          if (StringUtils.canParseEnum[TEnum](MessageKey("enum"), valueCandidate)) {
            JsSuccess(StringUtils.parseEnum(valueCandidate))
          } else {
            JsError(Seq(JsPath() -> Seq(ValidationError(s"Not valid '${classManifest.runtimeClass.getSimpleName}' value"))))
          }
        case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
      }
    }

    implicit def enumFormatByName[TEnum <: Enum[TEnum]](implicit classManifest: Manifest[TEnum])= new Format[TEnum] {
      override def writes(o: TEnum): JsValue = JsString(o.name)

      override def reads(json: JsValue) = json match {
        case JsString(valueCandidate) =>
          if (StringUtils.canParseEnum[TEnum](MessageKey("enum"), valueCandidate)) {
            JsSuccess(StringUtils.parseEnum(valueCandidate))
          } else {
            JsError(Seq(JsPath() -> Seq(ValidationError(s"Not valid '${classManifest.runtimeClass.getSimpleName}' value"))))
          }
        case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
      }
    }
  }

  object Joda {
    implicit def period = new Format[Period] {
      override def writes(p: Period): JsValue = JsString(DateUtils.PERIOD_FORMATTER.print(p))

      override def reads(json: JsValue) = json match {
        case JsString(valueCandidate) =>
          if (DateUtils.canParsePeriod(valueCandidate)) {
            JsSuccess(DateUtils.PERIOD_FORMATTER.parsePeriod(valueCandidate))
          } else {
            JsError(Seq(JsPath() -> Seq(ValidationError(s"Not valid joda period value"))))
          }
        case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
      }
    }

    implicit def localDate = new Format[LocalDate] {
      override def writes(ld: LocalDate): JsValue = JsNumber(ld.toDateTimeAtStartOfDay.getMillis)

      override def reads(json: JsValue) = json match {
        case JsNumber(valueCandidate) =>
          JsSuccess(new LocalDate(valueCandidate.toLong))
        case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsnumber"))))
      }
    }
  }
}
