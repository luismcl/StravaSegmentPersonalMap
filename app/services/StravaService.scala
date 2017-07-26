package services

import java.time.{LocalDateTime, ZonedDateTime}
import javastrava.api.v3.auth.impl.retrofit.AuthorisationServiceImpl
import javastrava.api.v3.model.{StravaSegment, StravaSegmentEffort}
import javastrava.api.v3.model.reference.StravaActivityType
import javastrava.api.v3.service.Strava


class StravaService {
  import collection.JavaConverters._
  def getUserActivities(): Map[StravaSegment, StravaSegmentEffort] ={
    val service = new AuthorisationServiceImpl
    val token = service.tokenExchange(4054, "e1655cc183019e240489a63e62c4e2144cd7e655", "906447b638e9a95b6649a23a47797c0d8d633ed4")
    val strava = new Strava(token)
    //val athlete = strava.getAthlete(1342951)

    val lastWeek = LocalDateTime.now().minusDays(60l)


    val rides = strava.listAllAuthenticatedAthleteActivities(LocalDateTime.now, lastWeek).asScala.filter(_.getType == StravaActivityType.RIDE)
    println("act")
    strava.clearCache
    val segments = rides.flatMap(ride=> strava.getActivity(ride.getId, true).getSegmentEfforts.asScala.filter(s=> s.getSegment.getClimbCategory.getId >= 2 && !s.getHidden))

    implicit val localDateOrdering: Ordering[ZonedDateTime] = Ordering.by(_.toLocalDate.toEpochDay)
    segments.map(segmentEsffort => (segmentEsffort.getSegment,segmentEsffort)).groupBy {_._1} map (s => s._2.sortBy(_._2.getStartDate).head)
  }



}
