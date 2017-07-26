package services.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging}
import domain.Athlete
import org.mongodb.scala._
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

case class ReadAthleteDataRequest(athleteId: Int)
case class LookupError(e:Throwable)
class ReadDatabaseActor @Inject()(configuration: Configuration, db: MongoDatabase) (implicit executionContext: ExecutionContext)extends Actor with ActorLogging {

  private val collection: MongoCollection[Athlete] = db.getCollection("athlete")

  override def receive: Receive = {
    case ReadAthleteDataRequest(athleteId) => {
      import org.mongodb.scala.model.Filters._

      val snd = sender()
      collection.find(equal("stravaID", athleteId)).limit(1).head().onComplete({
        case Success(result: Athlete) => {
          log.info(s"Read Athlete $athleteId with Name ${result.name} Loaded")
          snd ! result
        }
        case Failure(e) => {
          e.printStackTrace()
          log.error(s"Read Athlete $athleteId Failed {}", e)
          sender() ! LookupError(e)
        }
      })
    }
  }
}
