package services.actors

import java.time.ZonedDateTime
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef}
import domain.{Athlete, SegmentEffort}
import kiambogo.scrava.ScravaClient
import kiambogo.scrava.models.{DetailedActivity, PersonalActivitySummary, PersonalDetailedActivity, RateLimitException, SegmentEffort => StravaSegmentEffort}


case class SegmentResumeRequest(athlete: Athlete, activities: Seq[PersonalActivitySummary])

case class SegmentsResponse(athlete: Athlete, segments: List[SegmentEffort])

class SegmentsActor @Inject()(@Named("updateDatabaseActor") updateDatabaseActor: ActorRef)
  extends Actor with ActorLogging {

  implicit val localDateOrdering: Ordering[ZonedDateTime] = Ordering.by(_.toLocalDate.toEpochDay)
  val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")


  def processSegmentEffort(segmentEfforts: List[StravaSegmentEffort]): Unit = {
    segmentEfforts.filter(s => s.segment.climb_category > 1).foreach(
      stravaSegmentEffort => updateDatabaseActor ! UpdateSegmentRequest(SegmentEffort(stravaSegmentEffort))
    )
  }

  override def receive: Receive = {
    case SegmentResumeRequest(athlete, activities) => {

      val client = new ScravaClient(athlete.authToken)
      activities.foreach(activity =>
        try {
          client.retrieveActivity(activity.id) match {
            case a: PersonalDetailedActivity => processSegmentEffort(a.segment_efforts)
            case a: DetailedActivity => processSegmentEffort(a.segment_efforts)
            case _ => log.debug(s"No Segment effort for ${activity.id}")
          }
        } catch {
          case RateLimitException(_) => {
            //send to recover
          }
        })

    }
    case _ => log.info("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRoooooooooooooo")
  }


}
