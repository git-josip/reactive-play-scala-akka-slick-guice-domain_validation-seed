package com.josip.reactiveluxury.configuration.actor

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Props}
import akka.routing.FromConfig
import com.josip.reactiveluxury.configuration.DatabaseProvider
import com.josip.reactiveluxury.module.actor.actionlog.ActionLogActor

import scala.concurrent.ExecutionContext

@Singleton()
class ActorFactory @Inject() (databaseProvider: DatabaseProvider, actorSystem: ActorSystem, implicit val ec : ExecutionContext) {
  actorSystem.actorOf(Props(new ActionLogActor(databaseProvider, ec)).withRouter(FromConfig()), name = ActionLogActor.NAME)
}
