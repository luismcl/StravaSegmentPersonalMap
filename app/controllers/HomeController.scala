package controllers


import javax.inject._

import akka.actor.ActorRef
import play.api.Configuration
import play.api.mvc._
import services.actors.LoadDataActor.UpdateUserDataRequest


@Singleton
class HomeController @Inject()(cc: ControllerComponents, @Named("loadDataActor") loadDataActor: ActorRef, configuration: Configuration) extends AbstractController(cc) {

  private val secret:String = configuration.get[String]("strava.secret")
  private val client:Int = configuration.get[Int]("strava.client")
  private val testToken:String = configuration.get[String]("strava.testToken")


  def index() = Action { implicit request: Request[AnyContent] =>
    loadDataActor ! UpdateUserDataRequest(client, secret, testToken)
    Ok
  }
}
