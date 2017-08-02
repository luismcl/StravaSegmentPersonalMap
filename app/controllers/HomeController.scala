package controllers


import javax.inject._

import akka.actor.ActorRef
import akka.util.Timeout
import domain.Athlete
import play.api.Configuration
import play.api.mvc._
import services.actors.{AthleteNotFound, ReadAthleteDataRequest}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               configuration: Configuration,
                               @Named("readDatabaseActor") readDatabaseActor: ActorRef)
                              (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  private val apiKey: String = configuration.get[String]("maps.api.secret")
  val msg = Some(s"""Please <a href="${routes.AuthorizationController.authorize().url}">Login</a> to load your segments""")

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout: Timeout = 5.seconds


  def index() = Action.async { implicit request: Request[AnyContent] =>

    request.cookies.get("athlete") match {
      case Some(cookie) => {
        val future = readDatabaseActor ? ReadAthleteDataRequest(cookie.value.toInt)

        future.map {
          case (athlete: Athlete) => Ok(views.html.index(Some(athlete), apiKey))
          case (AthleteNotFound(athleteId)) => Ok(views.html.index(None, apiKey, msg)).discardingCookies(DiscardingCookie("athlete"))
        }
      }
      case None => {
        Future {
          Ok(views.html.index(None, apiKey, msg))
        }(executionContext)
      }

    }
  }
}
