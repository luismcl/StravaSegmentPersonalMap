package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.util.Timeout
import domain.Athlete
import play.api.Configuration
import play.api.mvc._
import services.actors.UpdateUserDataRequest

import scala.concurrent.ExecutionContext

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

  routes.HomeController.index().url
  val msg = Some(s""""Please, wait few seconds while the map is build. <strong><a href="${routes.HomeController.index().url}">Refresh Map</a></strong>""")

  def authorize() = Action { implicit request: Request[AnyContent] =>

    val uri =s"$stravaOauthUri?client_id=$client&response_type=code&redirect_uri=$redirectUri"
    Redirect(uri)
  }

  def authorizeCallback(code:String) = Action.async { implicit request: Request[AnyContent] =>
    import akka.pattern.ask

    import scala.concurrent.duration._
    implicit val timeout: Timeout = 5.seconds

    val future =  loadDataActor ? UpdateUserDataRequest(client, secret, code)
    future.map {
      case (athlete: Athlete) => Ok(views.html.index(Some(athlete), apiKey, msg))withCookies Cookie("athlete", s"${athlete._id}")
    }
  }

}
