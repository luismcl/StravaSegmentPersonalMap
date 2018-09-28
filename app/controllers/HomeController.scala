package controllers


import javax.inject._
import akka.actor.ActorRef
import akka.util.Timeout
import domain.Athlete
import play.api.Configuration
import play.api.mvc._
import services.actors.{AthleteNotFound, ReadAthleteDataRequest, UpdateSegmentDatabaseRequest, UpdateUserDataRequest}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               configuration: Configuration,
                               @Named("readDatabaseActor") readDatabaseActor: ActorRef,
                               @Named("loadDataActor") loadDataActor: ActorRef)
                              (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  private val apiKey: String = configuration.get[String]("maps.api.secret")
  val msg = Some(s"""Please <a href="${routes.AuthorizationController.authorize().url}">Login</a> to load your segments""")


  private def readAthleteFromSession()(implicit request: Request[AnyContent]): Option[Athlete] = {
    request.session.get("athleteJS") match {
      case Some(athleteString) => {
        val jsValueAthlete: JsValue = Json.parse(athleteString)
        val athleteJsResult: JsResult[Athlete] = Json.fromJson[Athlete](jsValueAthlete)
        athleteJsResult match {
          case JsSuccess(athlete: Athlete, path: JsPath) => Some(athlete)
          case e: JsError => None
        }
      }
      case None => None
    }
  }


  def index() = Action { implicit request: Request[AnyContent] =>

    readAthleteFromSession match {
      case Some(athlete: Athlete) => Ok(views.html.index(Some(athlete), apiKey))
      case None => Ok(views.html.index(None, apiKey, msg)).withNewSession
    }
  }

  def updateDatabase() = Action { implicit request: Request[AnyContent] =>

    readAthleteFromSession match {
      case Some(athlete: Athlete) => {
        loadDataActor ! UpdateSegmentDatabaseRequest(athlete)
        Ok(views.html.index(Some(athlete), apiKey, AuthorizationController.msg))
      }
      case None => Ok(views.html.index(None, apiKey, msg)).withNewSession
    }
  }


  def view(athleteID: Option[String]) = Action.async { implicit request: Request[AnyContent] =>
    import akka.pattern.ask
    import scala.concurrent.duration._
    implicit val timeout: Timeout = 5.seconds

    val otherAthleteFuture = athleteID match {
      case Some(athleteId) => {
        val future = readDatabaseActor ? ReadAthleteDataRequest(athleteId.toInt)
        future.map {
          case (athlete: Athlete) => Some(athlete)
          case (AthleteNotFound(athleteId)) => None
        }
      }
      case None => {
        Future {
          None
        }(executionContext)
      }
    }

    otherAthleteFuture.map {
      case otherAthlete: Option[Athlete] => {
        readAthleteFromSession match {
          case athlete: Option[Athlete] => Ok(views.html.viewAthleteMap(athlete, otherAthlete, apiKey))
          case _ => Ok(views.html.viewAthleteMap(None, otherAthlete, apiKey, msg))
        }
      } case _ => {
        readAthleteFromSession match {
          case Some(athlete: Athlete) => NotFound(views.html.index(Some(athlete), apiKey, Some(s"Athlete: ${athleteID.getOrElse("invalid")} Not Found"))).withNewSession
          case _ => NotFound(views.html.index(None, apiKey, Some(s"Athlete: ${athleteID.getOrElse("invalid")} Not Found"))).withNewSession
        }
      }
    }


  }

  def about() = Action { implicit request: Request[AnyContent] =>
    readAthleteFromSession match {
      case Some(athlete: Athlete) => Ok(views.html.about(Some(athlete)))
      case None => Ok(views.html.about(None)).withNewSession
    }
  }

  def policy() = Action { implicit request: Request[AnyContent] =>
    readAthleteFromSession match {
      case Some(athlete: Athlete) => Ok(views.html.policy(Some(athlete)))
      case None => Ok(views.html.policy(None)).withNewSession
    }
  }
}
