package com.josip.reactiveluxury.core.slick.postgres

import com.github.tminglei.slickpg._

trait MyPostgresDriver extends ExPostgresDriver
  with PgDateSupport {

  override val api = MyAPI

  object MyAPI extends API with DateTimeImplicits {
  }
}

object MyPostgresDriver extends MyPostgresDriver