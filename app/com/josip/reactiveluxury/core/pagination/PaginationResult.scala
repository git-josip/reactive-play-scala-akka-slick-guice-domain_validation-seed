package com.josip.reactiveluxury.core.pagination

import com.josip.reactiveluxury.core.Asserts
import play.api.libs.json._

case class PaginationResult[TItem](
  currentPagination : Pagination,
  totalItemCount    : Long,
  items             : Seq[TItem]
) {
  Asserts.argumentIsNotNull(currentPagination)
  Asserts.argumentIsTrue(totalItemCount >= 0)
  Asserts.argumentIsNotNull(items)

  def totalPagesCount = {
    (totalItemCount / currentPagination.itemsPerPage) + (if (totalItemCount % currentPagination.itemsPerPage > 0) 1 else 0)
  }
}

object PaginationResult
{
  implicit def jsonWrites[T](implicit fmt: Writes[T]): Writes[PaginationResult[T]] = new Writes[PaginationResult[T]] {
    def writes(ts: PaginationResult[T]) = JsObject(Seq(
      "currentPagination" -> Json.toJson(ts.currentPagination),
      "totalItemCount" -> JsNumber(ts.totalItemCount),
      "totalPagesCount" -> JsNumber(ts.totalPagesCount),
      "items" -> JsArray(ts.items.map(Json.toJson(_)))
    ))
  }
}
