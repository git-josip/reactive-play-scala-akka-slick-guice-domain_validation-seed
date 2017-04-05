package com.josip.reactiveluxury.configuration

import javax.inject.{Inject, Singleton}

import play.api.db.DBApi
import slick.jdbc.PostgresProfile.api._

@Singleton()
class DatabaseProvider @Inject() (dbApi: DBApi) {
  lazy val db = Database.forDataSource(dbApi.database("default").dataSource, None)
}
