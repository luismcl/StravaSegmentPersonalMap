package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.util.Timeout
import domain.Athlete
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import services.actors.{AthleteNotFound, UpdateSegmentDatabaseRequest, UpdateUserDataRequest}

import scala.concurrent.ExecutionContext

object AuthorizationController{
  val msg = Some(s""""Please, wait few seconds while the map is build. <strong><a href="${routes.HomeController.index().url}">Refresh Map</a></strong>""")
}

@Singleton
class AuthorizationController @Inject()(cc: ControllerComponents,
                                        @Named("loadDataActor") loadDataActor: ActorRef,
                                        configuration: Configuration)
                                       (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  private val secret:String = configuration.get[String]("strava.secret")
  private val stravaOauthUri=configuration.get[String]("strava.uris.oauth ")
  private val redirectUri = configuration.get[String]("strava.uris.redirect")
  private val client:Int = configuration.get[Int]("strava.client")
  private val apiKey: String = configuration.get[String]("maps.api.secret")

  def authorize() = Action { implicit request: Request[AnyContent] =>
    val uri =s"$stravaOauthUri?client_id=$client&response_type=code&redirect_uri=$redirectUri"
    Redirect(uri)
  }

  private implicit val athleteFormat = Json.format[Athlete]

  def authorizeCallback(code:String) = Action.async { implicit request: Request[AnyContent] =>
    import akka.pattern.ask

    import scala.concurrent.duration._
    implicit val timeout: Timeout = 5.seconds

    val future =  loadDataActor ? UpdateUserDataRequest(client, secret, code)
    future.map {
      case (athlete: Athlete) => Ok(views.html.index(Some(athlete), apiKey, AuthorizationController.msg)).withSession("athleteJS"->Json.toJson(athlete).toString())
      case (AthleteNotFound(athleteId)) => NotFound(views.html.index(None, apiKey, Some(s"Athlete: $athleteId Not Found"))).withNewSession
    }
  }
}
