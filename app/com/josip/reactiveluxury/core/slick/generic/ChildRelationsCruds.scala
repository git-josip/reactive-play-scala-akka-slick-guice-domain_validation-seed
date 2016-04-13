package com.josip.reactiveluxury.core.slick.generic

import com.josip.reactiveluxury.configuration.DatabaseProvider
import com.josip.reactiveluxury.core.Asserts
import slick.lifted
import com.josip.reactiveluxury.core.slick.postgres.MyPostgresDriver.api._
import scala.concurrent.Future
import com.josip.reactiveluxury.configuration.CustomExecutionContext._

trait ChildRelationsCrudRepository [ChildRelationItem <: ChildRelation[ChildRelationItem]] {
  def insert(item: ChildRelationItem): Future[Int]
  def insertAll(items: Seq[ChildRelationItem]): Future[Option[Int]]
}

abstract class ChildRelationsCrudRepositoryImpl[T <: Table[ChildRelationItem], ChildRelationItem <: ChildRelation[ChildRelationItem]]
(val databaseProvider: DatabaseProvider, val query: lifted.TableQuery[T]) extends ChildRelationsCrudRepository[ChildRelationItem] {
  Asserts.argumentIsNotNull(databaseProvider)
  Asserts.argumentIsNotNull(query)

  override def insert(item: ChildRelationItem): Future[Int] = {
    Asserts.argumentIsNotNull(item)

    for {
      result <- databaseProvider.db.run((query += item).transactionally) handleError()
    } yield result
  }

  override def insertAll(items: Seq[ChildRelationItem]): Future[Option[Int]] = {
    Asserts.argumentIsNotNull(items)

    this.databaseProvider.db.run((query ++= items).transactionally)
  }
}