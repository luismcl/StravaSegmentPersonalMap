package domain

import java.util.Date
import javastrava.api.v3.model.StravaAthlete
import play.api.libs.functional.syntax._

import kiambogo.scrava.models.{SegmentEffort => StravaSegmentEffort, SegmentSummary => StravaSegment}
import play.api.libs.json.{Json, OFormat, __}

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

object Athlete extends ((Int, String, String, Date, String) => Athlete) {
  def apply(athlete:StravaAthlete, token:String): Athlete =
    new Athlete(athlete.getId, s"${athlete.getFirstname} ${athlete.getLastname}", athlete.getCity, new Date,token)

  def apply(_id: Int, name: String, city: String, lastUpdate: Date, authToken: String): Athlete = new Athlete(_id, name, city, lastUpdate, authToken)

  implicit val athleteReads = (
    (__ \ '_id).read[Int] ~
      (__ \ 'name).read[String] ~
      (__ \ 'city).read[String] ~
      (__ \ 'lastUpdate).read[Date] ~
      (__ \ 'authToken).read[String]
    )(Athlete.apply(_:Int, _:String,_:String, _:Date,_:String))

  implicit val athleteFormat: OFormat[Athlete] = Json.format[Athlete]

}
case class Athlete(_id:Int, name:String, city:String, lastUpdate:Date, authToken:String)

object Point{
  def apply(coordinates: List[Double]): Point = new Point("Point", coordinates)
}
case class Point(`type`:String, coordinates:List[Double])

