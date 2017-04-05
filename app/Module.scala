import java.util.TimeZone
import javax.inject.Inject

import com.google.inject.AbstractModule
import org.joda.time.DateTimeZone
import play.api.{Configuration, Environment}

class Module @Inject() (
  environment: Environment,
  configuration: Configuration
) extends AbstractModule {
  override def configure(): Unit = {
    // bind as singelton configuration parts
    bind(classOf[ApplicationLifecycleModule]).asEagerSingleton()

    // set all dates to be default UTC timezone
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    DateTimeZone.setDefault(DateTimeZone.UTC)
  }
}
