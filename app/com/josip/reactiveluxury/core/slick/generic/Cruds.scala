package com.josip.reactiveluxury.core.slick.generic

import java.util.UUID
import com.josip.reactiveluxury.configuration.DatabaseProvider
import com.josip.reactiveluxury.core.pagination.Pagination
import com.josip.reactiveluxury.core.{Asserts, GeneratedId}
import org.joda.time.DateTime
import com.josip.reactiveluxury.core.slick.postgres.MyPostgresDriver.api._
import slick.lifted
import scala.concurrent.Future
import com.josip.reactiveluxury.configuration.CustomExecutionContext._

trait CrudRepository[EntityItem <: Entity[EntityItem]] {
  def count: Future[Int]

  def findAll: Future[List[EntityItem]]
  def findPaginatedOrdered(pagination: Pagination): Future[List[EntityItem]]
  def findById(id: Long): Future[Option[EntityItem]]
  def findByExternalId(externalId: String): Future[Option[EntityItem]]

  def insert(item: EntityItem): Future[EntityItem]
  def insertAll(items: Seq[EntityItem]): Future[Option[Int]]
  def update(item: EntityItem): Future[EntityItem]
  def deleteById(id: Long): Future[Int]

  def generateUniqueExternalId: Future[String]
}

abstract class CrudRepositoryImpl[T <: Table[EntityItem] with IdentifiableTable, EntityItem <: Entity[EntityItem]](val databaseProvider: DatabaseProvider, val query: lifted.TableQuery[T]) extends CrudRepository[EntityItem]{
  Asserts.argumentIsNotNull(databaseProvider)
  Asserts.argumentIsNotNull(query)

  val queryWithId = this.query returning query.map(_.id)

  override def count: Future[Int] = this.databaseProvider.db.run(query.length.result)

  override def findAll: Future[List[EntityItem]] = {
    this.databaseProvider.db.run(this.query.result).map(_.toList)
  }

  def findPaginated(pagination: Pagination): Future[List[EntityItem]] = {
    this.databaseProvider.db.run(this.query.drop(pagination.offset).take(pagination.itemsPerPage).result).map(_.toList)
  }

  def findPaginatedOrdered(pagination: Pagination): Future[List[EntityItem]] = {
    this.databaseProvider.db
      .run(this.query.drop(pagination.offset).take(pagination.itemsPerPage).sortBy(_.id.desc).result)
      .map(_.toList)
  }

  override def findById(id: Long): Future[Option[EntityItem]] = {
    Asserts.argumentIsNotNull(id)

    val action = this.query.filter(_.id === id).result.headOption
    databaseProvider.db.run(action)
  }

  override def findByExternalId(externalId: String): Future[Option[EntityItem]] = {
    Asserts.argumentIsNotNull(externalId)

    val action = this.query.filter(_.externalId === externalId).result.headOption
    databaseProvider.db.run(action)
  }

  override def insert(item: EntityItem): Future[EntityItem] = {
    Asserts.argumentIsNotNull(item)

    for {
      generatedId <- databaseProvider.db.run((queryWithId += item).transactionally).map(id => GeneratedId(id)) handleError()
      entity <- this.findById(generatedId.id) handleError()
    } yield entity.get
  }

  override def insertAll(items: Seq[EntityItem]): Future[Option[Int]] = {
    Asserts.argumentIsNotNull(items)

    this.databaseProvider.db.run((query ++= items).transactionally)
  }

  override def update(item: EntityItem): Future[EntityItem] = {
    Asserts.argumentIsNotNull(item)

    for {
      _ <- this.databaseProvider.db.run(this.query.filter(_.id === item.id).update(item).transactionally) handleError()
      entity <- this.findById(item.id.get) handleError()
    } yield entity.get
  }

  override def deleteById(id: Long): Future[Int] = {
    Asserts.argumentIsNotNull(id)

    this.databaseProvider.db.run(this.query.filter(_.id === id).delete.transactionally) handleError()
  }

  override def generateUniqueExternalId: Future[String] = {
    val externalIdCandidate = UUID.randomUUID.toString

    val eventualEventualString: Future[Future[String]] = for {
      userCandidate <- this.findByExternalId(externalIdCandidate)
    } yield {
      if (userCandidate.isDefined) this.generateUniqueExternalId
      else Future.successful(externalIdCandidate)
    }

    eventualEventualString.flatMap(f => f)
  }
}

trait IdentifiableTable {
  def id: slick.lifted.Rep[Long]
  def externalId: slick.lifted.Rep[String]
  def createdOn: slick.lifted.Rep[DateTime]
  def modifiedOn: slick.lifted.Rep[DateTime]
}
