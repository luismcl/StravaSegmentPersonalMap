package controllers


import javax.inject._

import akka.actor.ActorRef
import play.api.Configuration
import play.api.mvc._
import services.actors.LoadDataActor.UpdateUserDataRequest


@Singleton
class HomeController @Inject()(cc: ControllerComponents, @Named("loadDataActor") loadDataActor: ActorRef, configuration: Configuration) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
