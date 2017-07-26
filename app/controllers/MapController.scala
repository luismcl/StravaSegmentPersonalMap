package controllers
import play.api.Configuration
import javax.inject._

import akka.actor._
import akka.util.Timeout
import domain.Athlete
import play.api.mvc._
import services.actors.ReadAthleteDataRequest
import play.api.routing._
import scala.concurrent.ExecutionContext

class MapController  @Inject()(cc: ControllerComponents, @Named("readDatabaseActor") readDatabaseActor: ActorRef, configuration:Configuration) (implicit executionContext: ExecutionContext)extends AbstractController(cc) {

  import scala.concurrent.duration._
  import akka.pattern.ask
  implicit val timeout: Timeout = 5.seconds
  private val apiKey:String = configuration.get[String]("maps.api.secret")

  def printMap(athleteId:Int) = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.map(athleteId,apiKey))
  }

  def buildmap(athleteId:Int) = Action.async  { implicit request: Request[AnyContent]  =>
    val future =  readDatabaseActor ? ReadAthleteDataRequest(athleteId)
    future .mapTo[Athlete].map { athlete =>
      Ok(views.js.buildmap.render(athlete)).as("text/javascript")
    }
  }

}
