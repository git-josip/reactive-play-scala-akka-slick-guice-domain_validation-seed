import com.josip.reactiveluxury.configuration.actor.ActorFactory

import scala.concurrent.Future
import javax.inject._
import play.api.inject.ApplicationLifecycle

@Singleton
class ApplicationLifecycleModule @Inject() (
  lifecycle: ApplicationLifecycle,
  actorFactory: ActorFactory
) {
  lifecycle.addStopHook { () =>
    Future.successful({
      actorFactory.actorSystem.terminate()
    })
  }


}
