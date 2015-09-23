package com.josip.reactiveluxury.core.json.customfomatters

import com.josip.reactiveluxury.core.messages.MessageKey
import com.josip.reactiveluxury.core.utils.StringUtils
import play.api.data.validation.ValidationError
import play.api.libs.json._

object CustomFormatter
{
  object Enum
  {
    def enumWritesByName[TEnum <: Enum[TEnum]]= new Writes[TEnum] {
      def writes(o: TEnum): JsValue = JsString(o.name)
    }

    def enumReadsByName[TEnum <: Enum[TEnum]](implicit classManifest: Manifest[TEnum]) = new Reads[TEnum] {
      def reads(json: JsValue) = json match {
        case JsString(valueCandidate) => {
          if (StringUtils.canParseEnum[TEnum](MessageKey("enum"), valueCandidate)) {
            JsSuccess(StringUtils.parseEnum(valueCandidate))
          } else {
            JsError(Seq(JsPath() -> Seq(ValidationError(s"Not valid '${classManifest.runtimeClass.getSimpleName}' value"))))
          }
        }
        case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
      }
    }
  }
}
