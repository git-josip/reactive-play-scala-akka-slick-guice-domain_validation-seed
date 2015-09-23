package com.josip.reactiveluxury.core.authentication

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.{Json, Format}

case class Credentials(
  username : String,
  password : String
) {
  Asserts.argumentIsNotNull(username)
  Asserts.argumentIsNotNull(password)
}

object Credentials {
  implicit val jsonFormat : Format[Credentials] = Json.format[Credentials]
}
