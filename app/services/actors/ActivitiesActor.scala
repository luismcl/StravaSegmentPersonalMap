package services.actors

import java.util.Date

import akka.actor.{Actor, ActorLogging}
import domain.Athlete
import kiambogo.scrava.ScravaClient
import kiambogo.scrava.models.PersonalActivitySummary


case class ActivitiesRequest(athlete:Athlete, from: Date, to: Date)
case class BadRequest()
case class ActivitiesResponse(athlete:Athlete, activities:Seq[PersonalActivitySummary])

class ActivitiesActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case ActivitiesRequest(athlete, from, to) =>
      val client = new ScravaClient(athlete.authToken)
      val activities = client.listAthleteActivities(before=Some((to.getTime/1000L).toInt), after=Some((from.getTime/1000L).toInt), retrieveAll=true)
      log.debug(s"Load ${activities.size } activities")
      sender ! ActivitiesResponse(athlete,
        activities.filter(a => a.`type`.equalsIgnoreCase("ride")
          && a.total_elevation_gain > 150
      ))
    case _ => sender ! BadRequest
  }
}