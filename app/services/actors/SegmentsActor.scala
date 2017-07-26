package services.actors

import java.time.ZonedDateTime
import javastrava.api.v3.model.StravaActivity
import javastrava.api.v3.service.Strava

import akka.actor.{Actor, ActorLogging}
import domain.SegmentResume


case class SegmentResumeRequest(strava: Strava, activities:Seq[StravaActivity])

case class SegmentsResponse(strava: Strava, segments:List[SegmentResume])



class SegmentsActor extends Actor with ActorLogging{
  implicit val localDateOrdering: Ordering[ZonedDateTime] = Ordering.by(_.toLocalDate.toEpochDay)

  override def receive: Receive = {
    case SegmentResumeRequest(strava, activities) => {
      import collection.JavaConverters._
      strava.clearCache
      val segmentEfforts = activities.flatMap(activity=> strava.getActivity(activity.getId, true).getSegmentEfforts.asScala)
      val segments = segmentEfforts.filter(s=> s.getSegment.getClimbCategory.getId >= 2 && !s.getHidden)
      val groupedSegmentsEffort = segments.map(segmentEffort => (segmentEffort.getSegment.getId,segmentEffort)).groupBy {_._1}
      val segmentsResume =  groupedSegmentsEffort.map(s => s._2.sortBy(_._2.getStartDate).head._2).map(SegmentResume(_))
      sender ! SegmentsResponse(strava, segmentsResume.toList)
    }
    case _ => log.info("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRoooooooooooooo")
  }


}
