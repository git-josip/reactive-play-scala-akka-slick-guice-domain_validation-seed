package com.josip.reactiveluxury.core.authentication

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.{Json, Format}

case class Credentials(
                        email : String,
  password : String
) {
  Asserts.argumentIsNotNull(email)
  Asserts.argumentIsNotNull(password)
}

object Credentials {
  implicit val jsonFormat : Format[Credentials] = Json.format[Credentials]
}
