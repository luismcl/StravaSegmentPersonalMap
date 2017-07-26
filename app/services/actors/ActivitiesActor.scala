package services.actors

import java.time.LocalDateTime
import javastrava.api.v3.model.StravaActivity
import javastrava.api.v3.model.reference.StravaActivityType
import javastrava.api.v3.service.Strava

import akka.actor.{Actor, ActorLogging}
import services.StravaService

case class ActivitiesRequest(strava: Strava, from: LocalDateTime, to: LocalDateTime)
case class BadRequest()
case class ActivitiesResponse(strava: Strava, activities:Seq[StravaActivity])

class ActivitiesActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case ActivitiesRequest(strava, from, to) =>
      val activities = strava.listAllAuthenticatedAthleteActivities(to, from)
      log.debug(s"Load ${activities.size()} activities for athlete ${strava.getAuthenticatedAthlete.getEmail}")
      import collection.JavaConverters._
      sender ! ActivitiesResponse(strava, activities.asScala.filter(_.getType == StravaActivityType.RIDE))
    case _ => sender ! BadRequest
  }
}