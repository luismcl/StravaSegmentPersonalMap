package domain

import java.util.Date
import javastrava.api.v3.model.{StravaAthlete, StravaSegmentEffort}

import org.mongodb.scala.bson.ObjectId

object Athlete {
  def apply(athlete:StravaAthlete, segments: List[SegmentResume]): Athlete =
    new Athlete(new ObjectId(), s"${athlete.getFirstname} ${athlete.getLastname}", athlete.getId, athlete.getCity, segments)
}
case class Athlete(_id: ObjectId, name:String, stravaID:Int, city:String, segments:List[SegmentResume])

object SegmentResume {
  def apply(sse: StravaSegmentEffort): SegmentResume = {
    new SegmentResume(sse.getSegment.getName, sse.getSegment.getClimbCategory.getId,
      sse.getSegment.getEndLatlng.getLatitude.doubleValue, sse.getSegment.getEndLatlng.getLongitude.doubleValue(),
      sse.getSegment.getDistance.doubleValue, Date.from(sse.getStartDate.toInstant), sse.getElapsedTime, sse.getId,
      sse.getSegment.getAverageGrade.doubleValue())
  }
}
case class SegmentResume(name:String, climbCategory:Int, latitude:Double, longitude:Double,
                         distance:Double, lastDate:Date, elapsedTime:Int, segmentEffortId:Long,
                         averageGrade:Double)