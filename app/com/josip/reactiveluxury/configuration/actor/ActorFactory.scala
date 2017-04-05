package com.josip.reactiveluxury.configuration.actor

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Props}
import akka.routing.FromConfig
import com.josip.reactiveluxury.configuration.DatabaseProvider
import com.josip.reactiveluxury.module.actor.actionlog.ActionLogActor

@Singleton()
class ActorFactory @Inject() (databaseProvider: DatabaseProvider) {
  val actorSystem = ActorSystem("reactiveluxury")

  actorSystem.actorOf(Props(new ActionLogActor(databaseProvider)).withRouter(FromConfig()), name = "actionLogActorRouter")
}
