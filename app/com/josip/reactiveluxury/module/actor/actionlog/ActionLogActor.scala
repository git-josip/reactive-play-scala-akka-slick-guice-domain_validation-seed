package com.josip.reactiveluxury.module.actor.actionlog

import akka.actor.{Actor, ActorLogging}
import com.josip.reactiveluxury.core.utils.DateUtils
import com.josip.reactiveluxury.module.domain.actionlog.ActionLogEntity
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json
import com.josip.reactiveluxury.core.slick.postgres.MyPostgresDriver.api._

import com.josip.reactiveluxury.configuration.CustomExecutionContext._

case class ActionLogCreateMsg(actionLog: ActionLogEntity)

class ActionLogActor extends Actor with ActorLogging {
  private lazy val db = Database.forDataSource(DB.getDataSource())

  def receive = {
    case ActionLogCreateMsg(actionLog) =>
      log.debug(s"ActionLog received.")

      val action = ActionLogActor.INSERT_QUERY(actionLog).transactionally
      val actionInsertFuture = db.run(action)

      actionInsertFuture.onSuccess {
        case e =>
          log.info(s"Action logged. DomainType: '${actionLog.domainType.name}', DomainId: '${actionLog.domainId}', Action: '${actionLog.actionType.name}'")
      }
      actionInsertFuture.onFailure {
        case e =>
          log.info(s"Action insert failed.Exception: '${e.toString}'")
      }

    case _ => log.error("ActionLogActor received invalid message.")
  }
}

private object ActionLogActor
{
  def INSERT_QUERY(actionLog: ActionLogEntity) = {
    sqlu"""
      INSERT INTO action_log
      (
         user_id,
         domain_type,
         domain_id,
         action_type,
         before,
         after,
         created_on
      )  VALUES
      (
         ${actionLog.userId},
         ${actionLog.domainType.name},
         ${actionLog.domainId},
         ${actionLog.actionType.name},
         ${actionLog.before.map(Json.stringify)}::JSON,
         ${actionLog.after.map(Json.stringify)}::JSON,
         ${DateUtils.jodaDateTimeToSqlDate(actionLog.createdOn)}
      )
    """
  }
}
