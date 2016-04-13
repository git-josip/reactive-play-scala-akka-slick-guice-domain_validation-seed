package com.josip.reactiveluxury.core.slick.utils

import slick.dbio.Effect
import slick.jdbc.{GetResult, SQLActionBuilder}
import slick.jdbc.SetParameter.SetUnit
import slick.profile.SqlStreamingAction

object SlickUtils {
  def staticQueryToSQLActionBuilder(query: String): SQLActionBuilder = {
    SQLActionBuilder(Seq(query), SetUnit)
  }

  def staticQueryToStreamingAction[T](query: String)(implicit rconv: GetResult[T]): SqlStreamingAction[Vector[T], T, Effect] = {
    SlickUtils.staticQueryToSQLActionBuilder(query).as[T]
  }
}
