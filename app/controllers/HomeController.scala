package controllers


import javax.inject._

import akka.actor.ActorRef
import akka.util.Timeout
import domain.Athlete
import play.api.Configuration
import play.api.mvc._
import services.actors.{AthleteNotFound, ReadAthleteDataRequest}

import scala.concurrent.{Await, ExecutionContext, Future}


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

  def view() = Action.async { implicit request: Request[AnyContent] =>


    val otherAthleteFuture = request.queryString("athlete") match {
      case athleteId :: tail => {
        val future = readDatabaseActor ? ReadAthleteDataRequest(athleteId.toInt)
        future.map {
          case (athlete: Athlete) => Some(athlete)
          case (AthleteNotFound(athleteId)) => None
        }
      } case _ => {
        Future { None
        }(executionContext)
      }
    }

    val otherAthlete = Await.result(otherAthleteFuture, 5 seconds)

    request.cookies.get("athlete") match {
      case Some(cookie) => {
        val future = readDatabaseActor ? ReadAthleteDataRequest(cookie.value.toInt)

        future.map {
          case (athlete: Athlete) => Ok(views.html.viewAthleteMap(Some(athlete),otherAthlete, apiKey))
          case (AthleteNotFound(athleteId)) =>  Ok(views.html.viewAthleteMap(None, otherAthlete, apiKey, msg)).discardingCookies(DiscardingCookie("athlete"))
        }
      }
      case None => {
        Future {
          Ok(views.html.viewAthleteMap(None,otherAthlete, apiKey, msg))
        }(executionContext)
      }

    }
  }

  def about() = Action.async { implicit request: Request[AnyContent] =>
    request.cookies.get("athlete") match {
      case Some(cookie) => {
        val future = readDatabaseActor ? ReadAthleteDataRequest(cookie.value.toInt)

        future.map {
          case (athlete: Athlete) => Ok(views.html.about(Some(athlete)))
          case (AthleteNotFound(athleteId)) => Ok(views.html.about(None)).discardingCookies(DiscardingCookie("athlete"))
        }
      }
      case None => {
        Future {
          Ok(views.html.about(None))
        }(executionContext)
      }

    }
  }

  def policy() = Action.async { implicit request: Request[AnyContent] =>
    request.cookies.get("athlete") match {
      case Some(cookie) => {
        val future = readDatabaseActor ? ReadAthleteDataRequest(cookie.value.toInt)

        future.map {
          case (athlete: Athlete) => Ok(views.html.policy(Some(athlete)))
          case (AthleteNotFound(athleteId)) => Ok(views.html.policy(None)).discardingCookies(DiscardingCookie("athlete"))
        }
      }
      case None => {
        Future {
          Ok(views.html.policy(None))
        }(executionContext)
      }

    }
  }
}
