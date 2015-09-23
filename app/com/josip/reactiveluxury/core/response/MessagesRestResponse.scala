package com.josip.reactiveluxury.core.response

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json

case class MessagesRestResponse(
  global    : Option[GlobalMessagesRestResponse]  = Some(GlobalMessagesRestResponse.EMPTY),
  local     : List[LocalMessagesRestResponse]     = List.empty
) {
  Asserts.argumentIsNotNull(global)
  Asserts.argumentIsNotNull(local)
}

object MessagesRestResponse {
  implicit val jsonFormat = Json.format[MessagesRestResponse]

  val EMPTY = MessagesRestResponse()
}
