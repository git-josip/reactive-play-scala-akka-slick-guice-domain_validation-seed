package com.josip.reactiveluxury.core.pagination

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json.Json

case class Pagination(
  pageNumber  : Int,
  itemsPerPage: Int
) {
  Asserts.argumentIsNotNull(pageNumber)
  Asserts.argumentIsTrue(pageNumber > 0)
  Asserts.argumentIsNotNull(itemsPerPage)
  Asserts.argumentIsTrue(itemsPerPage >= 1)

  lazy val offset = itemsPerPage * (pageNumber - 1)
}

object Pagination {
  final val ALL = Pagination(1, Integer.MAX_VALUE)
  final val DEFAULT = Pagination(1, 25)

  implicit val jsonWFormat = Json.format[Pagination]
}
