import scala.concurrent.Future
import javax.inject._

import akka.actor.ActorSystem
import play.api.inject.ApplicationLifecycle

@Singleton
class ApplicationLifecycleModule @Inject() (
  lifecycle: ApplicationLifecycle,
  actorSystem: ActorSystem
) {

  lifecycle.addStopHook { () =>
    Future.successful({
      actorSystem.terminate()
    })
  }
}
