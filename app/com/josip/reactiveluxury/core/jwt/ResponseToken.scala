package com.josip.reactiveluxury.core.jwt

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.{Format, Json}

case class ResponseToken(token: String) {
  Asserts.argumentIsNotNullNorEmpty(token)
}

object ResponseToken {
  implicit val jsonFormat : Format[ResponseToken] = Json.format[ResponseToken]
}
