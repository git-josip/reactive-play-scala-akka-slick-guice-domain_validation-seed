package com.josip.reactiveluxury.configuration

import javax.inject.Singleton
import play.api.db.DB
import play.api.Play.current
import slick.driver.PostgresDriver.api._

@Singleton()
class DatabaseProvider {
  lazy val db = Database.forDataSource(DB.getDataSource())
}
