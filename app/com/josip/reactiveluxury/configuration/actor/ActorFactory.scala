package com.josip.reactiveluxury.configuration.actor

import akka.actor.{Props, ActorSystem}
import akka.routing.FromConfig
import com.josip.reactiveluxury.module.actor.actionlog.ActionLogActor

object ActorFactory {
  val actorSystem = ActorSystem("reactiveluxury")

  val actionLogActorRouter = actorSystem.actorOf(Props[ActionLogActor].withRouter(FromConfig()), name = "actionLogActorRouter")
}
