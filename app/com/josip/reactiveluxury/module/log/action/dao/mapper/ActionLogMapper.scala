package com.josip.reactiveluxury.module.log.action.dao.mapper

import com.josip.reactiveluxury.core.slick.custommapper.CustomSlickMapper
import com.josip.reactiveluxury.module.log.action.domain.{ActionType, ActionLogEntity}
import com.josip.reactiveluxury.module.user.dao.sql.mapper.UserEntityMapper
import com.josip.reactiveluxury.module.log.action.domain.ActionDomainType
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import slick.driver.PostgresDriver.api._
import CustomSlickMapper.Postgres._

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
    def userId      = column[Long]            (USER_ID_COLUMN)
    def domainType  = column[ActionDomainType](DOMAIN_TYPE_COLUMN)
    def domainId    = column[Long]            (DOMAIN_ID_COLUMN)
    def actionType  = column[ActionType]      (ACTION_TYPE_COLUMN)
    def before      = column[JsValue]         (BEFORE_COLUMN)
    def after       = column[JsValue]         (AFTER_COLUMN)
    def createdOn   = column[DateTime]        (CREATED_ON_COLUMN)

    def user = foreignKey(UserEntityMapper.ID_COLUMN, userId, UserEntityMapper.UserDetailsTableDescriptor.query)(_.id)

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
