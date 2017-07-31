package services.actors

import java.time.ZonedDateTime

import akka.actor.{Actor, ActorLogging}
import domain.{Athlete, SegmentEffort}
import kiambogo.scrava.ScravaClient
import kiambogo.scrava.models.{ActivitySummary, DetailedActivity, PersonalActivitySummary, PersonalDetailedActivity}


case class SegmentResumeRequest(athlete:Athlete, activities:Seq[PersonalActivitySummary])

case class SegmentsResponse(athlete:Athlete, segments:List[SegmentEffort])

class SegmentsActor extends Actor with ActorLogging{
  implicit val localDateOrdering: Ordering[ZonedDateTime] = Ordering.by(_.toLocalDate.toEpochDay)
  val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

  override def receive: Receive = {
    case SegmentResumeRequest(athlete, activities) => {

      val client = new ScravaClient(athlete.authToken)


      val segmentEfforts = activities.flatMap(activity => client.retrieveActivity(activity.id) match {
        case a:PersonalDetailedActivity => a.segment_efforts
        case a:DetailedActivity => a.segment_efforts
        case a:PersonalActivitySummary => Nil
        case a:ActivitySummary => Nil
      })

      val segments = segmentEfforts.filter(s=> s.segment.climb_category > 1)
      val groupedSegmentsEffort = segments.map(segmentEffort => (segmentEffort.segment.id,segmentEffort)).groupBy {_._1}
      val segmentsResume =  groupedSegmentsEffort.map(s => s._2.sortBy(_._2.start_date).head._2).map(SegmentEffort(_))
      sender ! SegmentsResponse(athlete, segmentsResume.toList)
    }
    case _ => log.info("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRoooooooooooooo")
  }
}
