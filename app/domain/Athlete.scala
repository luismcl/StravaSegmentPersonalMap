package domain

import java.util.Date
import javastrava.api.v3.model.StravaAthlete

import kiambogo.scrava.models.{AthleteSummary, SegmentSummary => StravaSegment, SegmentEffort => StravaSegmentEffort}

object SegmentEffort{
  val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  def apply(sse: StravaSegmentEffort):SegmentEffort = {
   new SegmentEffort(sse.id, sse.athlete("id"), format.parse(sse.start_date),
     Segment.apply(sse.segment), sse.kom_rank.getOrElse(0), sse.elapsed_time)
  }
  def apply(sse: StravaSegmentEffort, attemps:Int):SegmentEffort = {
    new SegmentEffort(sse.id, sse.athlete("id"), format.parse(sse.start_date),
      Segment.apply(sse.segment), sse.kom_rank.getOrElse(0), sse.elapsed_time)
  }
}
case class SegmentEffort(_id:Long, athleteId:Int, lastDate:Date, segment:Segment, komRank:Int, elapsedTime:Long)

object Segment{
  implicit def convertToPosition(position:List[Float]):Point = {
    Point(position.map(_.toDouble))
  }
  def apply(stravaSegment:StravaSegment): Segment ={
    new Segment(stravaSegment.id, stravaSegment.name, stravaSegment.climb_category,
      stravaSegment.distance.doubleValue(),
      stravaSegment.start_latlng, stravaSegment.end_latlng,
      stravaSegment.average_grade,
      stravaSegment.elevation_high)
  }
}
case class Segment(stravaSegmentId:Int, name:String, climbCategory:Int,
                   distance:Double, start:Point, end:Point, averageGrade:Double, elevationHigh:Double)

object Athlete {
  def apply(athlete:AthleteSummary, token:String): Athlete =
    new Athlete(athlete.id, s"${athlete.firstname} ${athlete.lastname}", athlete.city, new Date,token)

  def apply(athlete:StravaAthlete, token:String): Athlete =
    new Athlete(athlete.getId, s"${athlete.getFirstname} ${athlete.getLastname}", athlete.getCity, new Date,token)
}
case class Athlete(_id:Int, name:String, city:String, lastUpdate:Date, authToken:String)

object Point{
  def apply(coordinates: List[Double]): Point = new Point("Point", coordinates)
}
case class Point(`type`:String, coordinates:List[Double])

