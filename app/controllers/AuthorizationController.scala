package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import play.api.Configuration
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.actors.LoadDataActor.UpdateUserDataRequest

@Singleton
class AuthorizationController @Inject()(cc: ControllerComponents,@Named("loadDataActor") loadDataActor: ActorRef, configuration: Configuration) extends AbstractController(cc) {

  private val secret:String = configuration.get[String]("strava.secret")
  private val stravaOauthUri=configuration.get[String]("strava.uris.oauth ")
  private val redirectUri = configuration.get[String]("strava.uris.redirect")
  private val client:Int = configuration.get[Int]("strava.client")

  def authorize() = Action { implicit request: Request[AnyContent] =>

    val uri =s"$stravaOauthUri?client_id=$client&response_type=code&redirect_uri=$redirectUri"

    Redirect(uri)
  }

  def authorizeCallback(code:String) = Action { implicit request: Request[AnyContent] =>
    loadDataActor ! UpdateUserDataRequest(client, secret, code)
    Ok
  }

}
