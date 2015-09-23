package com.josip.reactiveluxury.core.response

import play.api.libs.json.Json

case class GlobalMessagesRestResponse
(
  info      : List[String] = List.empty,
  warnings  : List[String] = List.empty,
  errors    : List[String] = List.empty
)

object GlobalMessagesRestResponse
{
  implicit val jsonFormat = Json.format[GlobalMessagesRestResponse]

  val EMPTY = GlobalMessagesRestResponse()
}

