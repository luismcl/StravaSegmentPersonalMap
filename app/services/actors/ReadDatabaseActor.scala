package services.actors

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging}
import domain.{Athlete, SegmentEffort}
import org.mongodb.scala._
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

case class ReadSegmentAthleteDataRequest(athleteId: Int)
case class ReadAthleteDataRequest(athleteId: Int)

case class LookupError(e:Throwable)
case class AthleteNotFound(athleteId: Int)

class ReadDatabaseActor @Inject()(configuration: Configuration, db: MongoDatabase) (implicit executionContext: ExecutionContext)extends Actor with ActorLogging {

  private val segmentEffortcollection: MongoCollection[SegmentEffort] = db.getCollection("segmenteffort")
  private val athleteCollection: MongoCollection[Athlete] = db.getCollection("athlete")
  import org.mongodb.scala.model.Filters._

  override def receive: Receive = {
    case ReadSegmentAthleteDataRequest(athleteId) => {
      val snd = sender()
      segmentEffortcollection.find(equal("athleteId", athleteId)).toFuture().onComplete({
        case Success(result: Seq[SegmentEffort]) => {
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

    case ReadAthleteDataRequest(athleteId) => {
      val snd = sender()
      athleteCollection.find(equal("_id", athleteId)).limit(1).head().onComplete({
        case Success(result: Athlete) => {
          log.info(s"Read Athlete $athleteId with Name ${result.name} Loaded")
          snd ! result
        }
        case Success(null) => {
          log.info(s"Read Athlete $athleteId does not exist")
          snd ! AthleteNotFound(athleteId)
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
