package services.actors

import javastrava.api.v3.model.StravaAthlete
import javax.inject.Inject

import akka.actor.{Actor, ActorLogging}
import domain.{Athlete, SegmentResume}
import org.mongodb.scala._

case class UpdateDatabaseRequest(athlete: StravaAthlete, segments: List[SegmentResume])

class UpdateDatabaseActor @Inject()(db: MongoDatabase) extends Actor with ActorLogging {

  private val collection: MongoCollection[Athlete] = db.getCollection("athlete")

  override def receive: Receive = {
    case UpdateDatabaseRequest(athlete, segments) => {
      collection.insertOne(Athlete(athlete, segments)).subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = log.info(s"Athlete ${athlete.getId} created with ${segments.size} segments")
        override def onError(e: Throwable): Unit = {
          e.printStackTrace()
          log.error(s"Athlete ${athlete.getId} Failed {}", e)
        }

        override def onComplete(): Unit = println("Completed")
      })
    }
  }
}
