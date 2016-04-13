package com.josip.reactiveluxury.module.dao.actionlog.sql

import com.josip.reactiveluxury.core.slick.custommapper.CustomSlickMapper.Postgres._
import com.josip.reactiveluxury.module.dao.user.sql.UserEntityMapper
import com.josip.reactiveluxury.module.domain.actionlog.{ActionDomainType, ActionLogEntity, ActionType}
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import com.josip.reactiveluxury.core.slick.postgres.MyPostgresDriver.api._

object ActionLogMapper
{
  final val ACTION_LOG_TABLE_NAME = "action_log"

  final val ID_COLUMN           = "id"
  final val USER_ID_COLUMN      = "user_id"
  final val DOMAIN_TYPE_COLUMN  = "domain_type"
  final val DOMAIN_ID_COLUMN    = "domain_id"
  final val ACTION_TYPE_COLUMN  = "action_type"
  final val BEFORE_COLUMN       = "before"
  final val AFTER_COLUMN        = "after"
  final val CREATED_ON_COLUMN   = "created_on"

  implicit val actionDomainTypeColumnType = enumColumnType[ActionDomainType]
  implicit val actionTypeColumnType       = enumColumnType[ActionType]

  class ActionLogMapper(tag: Tag) extends Table[ActionLogEntity](tag, ACTION_LOG_TABLE_NAME) {
    def id          = column[Long]            (ID_COLUMN,           O.PrimaryKey, O.AutoInc )
    def userId      = column[Option[Long]]    (USER_ID_COLUMN)
    def domainType  = column[ActionDomainType](DOMAIN_TYPE_COLUMN)
    def domainId    = column[Long]            (DOMAIN_ID_COLUMN)
    def actionType  = column[ActionType]      (ACTION_TYPE_COLUMN)
    def before      = column[JsValue]         (BEFORE_COLUMN)
    def after       = column[JsValue]         (AFTER_COLUMN)
    def createdOn   = column[DateTime]        (CREATED_ON_COLUMN)

    def user = foreignKey("id", userId, UserEntityMapper.UserTableDescriptor.user)(_.id.?)

    def * = (
      id.?,
      userId,
      domainType,
      domainId,
      actionType,
      before.?,
      after.?,
      createdOn
    ) <> ((ActionLogEntity.apply _).tupled, ActionLogEntity.unapply)
  }
  object ActionLogMapper {
    def query = TableQuery[ActionLogMapper]
    def queryWithId = query returning query.map(_.id)
  }
}
