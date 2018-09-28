package services.actors

import java.util.Calendar
import javastrava.api.v3.model.StravaAthlete
import javax.inject.Inject

import akka.actor.{Actor, ActorLogging}
import domain.{Athlete, SegmentEffort}
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.UpdateResult

case class UpdateAthleteRequest(athlete: Athlete)

class UpdateAthleteActor @Inject()(db: MongoDatabase) extends Actor with ActorLogging {

  private val athleteCollection: MongoCollection[Athlete] = db.getCollection("athlete")

  override def receive: Receive = {
    case UpdateAthleteRequest(athlete) => {

      athleteCollection.updateOne(equal("_id", athlete._id),
        combine(set("name", athlete.name),
          set("city", athlete.city),
          set("authToken", athlete.authToken),
          set("lastUpdate", Calendar.getInstance.getTime)),
        UpdateOptions.apply().upsert(true)).subscribe(new Observer[UpdateResult] {
        override def onNext(result: UpdateResult): Unit = log.info(s"Athlete ${athlete._id} updated: ${result.getMatchedCount}")

        override def onError(e: Throwable): Unit = {
          e.printStackTrace()
          log.error(s"Athlete ${athlete._id} Failed {}", e)
        }
        override def onComplete(): Unit = log.debug(s"Complete update of ${athlete._id}")
      })
    }
  }
}
