import java.util.TimeZone
import com.josip.reactiveluxury.configuration.actor.ActorFactory
import com.josip.reactiveluxury.core.communication.CORSFilter
import org.joda.time.DateTimeZone
import play.api.mvc.WithFilters
import play.api._

object Global extends WithFilters(CORSFilter) with GlobalSettings
{
  override def beforeStart(app: Application) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    DateTimeZone.setDefault(DateTimeZone.UTC)
  }

  override def onStop(app: play.api.Application) {
    ActorFactory.actorSystem.shutdown()
  }
}
