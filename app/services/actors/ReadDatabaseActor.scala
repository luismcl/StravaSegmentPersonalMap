package services.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging}
import domain.{Athlete, SegmentEffort}
import org.mongodb.scala._
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

case class ReadAthleteDataRequest(athleteId: Int)
case class LookupError(e:Throwable)
class ReadDatabaseActor @Inject()(configuration: Configuration, db: MongoDatabase) (implicit executionContext: ExecutionContext)extends Actor with ActorLogging {

  private val segmentEffortcollection: MongoCollection[SegmentEffort] = db.getCollection("segmenteffort")

  override def receive: Receive = {
    case ReadAthleteDataRequest(athleteId) => {
      import org.mongodb.scala.model.Filters._

      val snd = sender()
      segmentEffortcollection.find(equal("athleteId", athleteId)).toFuture().onComplete({
        case Success(result: List[SegmentEffort]) => {
          log.info(s"Read ${result.size} Athlete $athleteId segments efforts")
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
