package com.josip.reactiveluxury.core.service

import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.core.slick.generic.{ChildRelationsCrudRepository, ChildRelation}
import scala.concurrent.Future

trait ChildRelationService[ChildRelationItem <: ChildRelation[ChildRelationItem]] {
  def create(item: ChildRelationItem): Future[Int]
  def createAll(items: List[ChildRelationItem]): Future[Option[Int]]
}

abstract class ChildRelationServiceImpl[ChildRelationItem <: ChildRelation[ChildRelationItem], ChildRelationRepo <: ChildRelationsCrudRepository[ChildRelationItem]]
(val childRelationRepository: ChildRelationRepo) extends ChildRelationService[ChildRelationItem] {
  Asserts.argumentIsNotNull(childRelationRepository)

  override def create(item: ChildRelationItem): Future[Int] = {
    Asserts.argumentIsNotNull(item)

    this.childRelationRepository.insert(item)
  }

  override def createAll(items: List[ChildRelationItem]): Future[Option[Int]] = {
    Asserts.argumentIsNotNull(items)

    this.childRelationRepository.insertAll(items)
  }
}
