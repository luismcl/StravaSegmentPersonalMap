package services.actors

import java.time.LocalDateTime
import java.util.{Calendar, Date}
import javastrava.api.v3.auth.impl.retrofit.AuthorisationServiceImpl
import javastrava.api.v3.service.Strava
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import domain.Athlete
import kiambogo.scrava.ScravaClient
import services.actors.LoadDataActor.UpdateUserDataRequest

object LoadDataActor {
  case class UpdateUserDataRequest(clientId: Integer, clientSecret: String, authorisationCode: String)
}

class LoadDataActor @Inject()(@Named("activitiesActor") activitiesActor: ActorRef,
                              @Named("segmentsActor") segmentsActor: ActorRef,
                              @Named("updateDatabaseActor") updateDatabaseActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case UpdateUserDataRequest(clientId, clientSecret, authorisationCode) => {
      val service = new AuthorisationServiceImpl
      val token = service.tokenExchange(clientId, clientSecret, authorisationCode)
      val athlete:Athlete = Athlete(token.getAthlete, token.getToken)
      sender ! athlete
      val from = Calendar.getInstance
      from.add(Calendar.YEAR,-1)
      activitiesActor ! ActivitiesRequest(athlete, from.getTime, Calendar.getInstance.getTime)
    }
    case ActivitiesResponse(athlete, activities) =>
      segmentsActor ! SegmentResumeRequest(athlete, activities)
    case SegmentsResponse(athlete, segments) => {
      updateDatabaseActor ! UpdateDatabaseRequest(athlete, segments)
    }
  }
}

