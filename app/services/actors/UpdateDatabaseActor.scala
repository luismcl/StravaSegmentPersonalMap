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

case class UpdateSegmentRequest(segment: SegmentEffort)

class UpdateDatabaseActor @Inject()(db: MongoDatabase) extends Actor with ActorLogging {

  private val segmentEffortCollection: MongoCollection[SegmentEffort] = db.getCollection("segmenteffort")


  override def receive: Receive = {
    case UpdateSegmentRequest(segment) => {

      segmentEffortCollection.updateOne(equal("_id", segment._id),
        combine(set("athleteId", segment.athleteId),
          set("elapsedTime", segment.elapsedTime),
          set("komRank", segment.komRank),
          set("komRank", segment.lastDate),
          set("segment.averageGrade", segment.segment.averageGrade),
          set("segment.climbCategory", segment.segment.climbCategory),
          set("segment.distance", segment.segment.distance),
          set("segment.segment", segment.segment.elevationHigh),
          set("segment.end", segment.segment.end),
          set("segment.name", segment.segment.name),
          set("segment.segment", segment.segment.start),
          set("segment.stravaSegmentId", segment.segment.stravaSegmentId)),
        UpdateOptions.apply().upsert(true)).subscribe(new Observer[UpdateResult] {
        override def onNext(result: UpdateResult): Unit = log.debug(s"Athlete ${athlete._id} updated: ${result.getMatchedCount} segment")

        override def onError(e: Throwable): Unit = {
          e.printStackTrace()
          log.error(s"Athlete ${athlete._id} Failed {}", e)
        }

        override def onComplete(): Unit = println("Completed")
      })
    }
  }
}
