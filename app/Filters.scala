import javax.inject.{Inject, Singleton}

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter

@Singleton
class Filters @Inject() (
  corsFilter: CORSFilter

) extends HttpFilters {
  override val filters = Seq(corsFilter)
}
