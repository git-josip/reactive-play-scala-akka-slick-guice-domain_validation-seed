package com.josip.reactiveluxury.core.service

import com.josip.reactiveluxury.configuration.actor.ActorFactory
import com.josip.reactiveluxury.core.pagination.Pagination
import com.josip.reactiveluxury.core.slick.generic.{CrudRepository, Entity}
import com.josip.reactiveluxury.core.utils.DateUtils
import com.josip.reactiveluxury.core.Asserts
import com.josip.reactiveluxury.module.actor.actionlog.ActionLogCreateMsg
import com.josip.reactiveluxury.module.domain.actionlog.{ActionType, ActionDomainType, ActionLogEntity}
import com.josip.reactiveluxury.module.domain.user.SystemUser
import play.api.libs.json.Format
import scala.concurrent.Future
import com.josip.reactiveluxury.configuration.CustomExecutionContext._

trait EntityService[EntityItem <: Entity[EntityItem]] {
  def create(item: EntityItem)(userId: Option[Long] = None)(implicit format: Format[EntityItem]): Future[EntityItem]
  def createAll(items: List[EntityItem])(userId: Option[Long] = None)(implicit format: Format[EntityItem]): Future[List[EntityItem]]
  def update(item: EntityItem)(userId: Option[Long] = None)(implicit format: Format[EntityItem]): Future[EntityItem]

  def tryGetById(id: Long): Future[Option[EntityItem]]
  def tryGetByExternalId(externalId: String): Future[Option[EntityItem]]

  def getAll: Future[List[EntityItem]]
  def getAllPaginated(pagination: Pagination): Future[List[EntityItem]]

  def deleteById(id: Long)

  def getById(id: Long): Future[EntityItem] = {
    this.tryGetById(id)
      .map(_.getOrElse(throw new RuntimeException(s"entity with this id does not exist. id: '$id', class: '${this.getClass}'"))
      )
  }

  def getByExternalId(externalId: String): Future[EntityItem] = {
    Asserts.argumentIsNotNull(externalId)

    this.tryGetByExternalId(externalId)
      .map(_.getOrElse(throw new RuntimeException(s"entity with this externalId does not exist. externalId: '$externalId', class: '${this.getClass}'"))
      )
  }
}

abstract class EntityServiceImpl[EntityItem <: Entity[EntityItem], EntityRepository <: CrudRepository[EntityItem]]
(val entityRepository: EntityRepository)
(implicit entityClassManifest: Manifest[EntityItem]) extends EntityService[EntityItem] {
  Asserts.argumentIsNotNull(entityRepository)
  Asserts.argumentIsNotNull(entityClassManifest)

  override def create(item: EntityItem)(userId: Option[Long] = None)(implicit format: Format[EntityItem]): Future[EntityItem] = {
    Asserts.argumentIsNotNull(item)

    for {
      externalId <- this.entityRepository.generateUniqueExternalId handleError()
      createdEntity <- this.entityRepository.insert(item.copyWith(("externalId", Some(externalId)))) handleError()
      _ <- {

        val actionUserId = userId match {
          case Some(id) => id
          case None => SystemUser.id
        }

        val createdAction = ActionLogEntity.of[EntityItem, EntityItem](
          userId      = actionUserId,
          domainType  = ActionDomainType.getByEntityClass(entityClassManifest.runtimeClass),
          domainId    = createdEntity.id.get,
          actionType  = ActionType.CREATED,
          before      = None,
          after       = Some(createdEntity)
        )
        ActorFactory.actionLogActorRouter ! ActionLogCreateMsg(createdAction)
        Future.successful({})
      }
    } yield createdEntity
  }

  def createAll(items: List[EntityItem])(userId: Option[Long] = None)(implicit format: Format[EntityItem]): Future[List[EntityItem]] = {
    Asserts.argumentIsNotNull(items)
    Asserts.argumentIsNotNull(userId)

    val insertedValues = items.map(item => {
      for {
        insertValue <- this.create(item)(userId) handleError()
      } yield insertValue
    })

    Future.sequence(insertedValues)
  }

  override def update(item: EntityItem)(userId: Option[Long] = None)(implicit format: Format[EntityItem]): Future[EntityItem] = {
    Asserts.argumentIsNotNull(item)

    for {
      updatedEntity <- this.entityRepository.update(item.copyWith(("modifiedOn", Some(DateUtils.nowDateTimeUTC)))) handleError()
      _ <- {
        val actionUserId = userId match {
          case Some(id) => id
          case None => SystemUser.id
        }
        val createdAction = ActionLogEntity.of[EntityItem, EntityItem](
          userId      = actionUserId,
          domainType  = ActionDomainType.getByEntityClass(entityClassManifest.runtimeClass),
          domainId    = item.id.get,
          actionType  = ActionType.UPDATED,
          before      = Some(item),
          after       = Some(updatedEntity)
        )
        ActorFactory.actionLogActorRouter ! ActionLogCreateMsg(createdAction)
        Future.successful({})
      }
    } yield updatedEntity
  }

  override def deleteById(id: Long) {
    this.entityRepository.deleteById(id)
  }

  override def tryGetById(id: Long): Future[Option[EntityItem]] = {
    this.entityRepository.findById(id)
  }

  override def tryGetByExternalId(externalId: String): Future[Option[EntityItem]] = {
    Asserts.argumentIsNotNull(externalId)

    this.entityRepository.findByExternalId(externalId)
  }

  override def getAll: Future[List[EntityItem]] = {
    this.entityRepository.findAll
  }

  override def getAllPaginated(pagination: Pagination): Future[List[EntityItem]] = {
    this.entityRepository.findPaginatedOrdered(pagination)
  }
}
