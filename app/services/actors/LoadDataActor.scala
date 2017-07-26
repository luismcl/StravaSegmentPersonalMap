package services.actors

import java.time.LocalDateTime
import javastrava.api.v3.auth.impl.retrofit.AuthorisationServiceImpl
import javastrava.api.v3.service.Strava
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
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
      activitiesActor ! ActivitiesRequest(new Strava(token), LocalDateTime.now().minusDays(60l), LocalDateTime.now())
    }
    case ActivitiesResponse(strava, activities) =>
      segmentsActor ! SegmentResumeRequest(strava, activities)
    case SegmentsResponse(strava, segments) => {
      updateDatabaseActor ! UpdateDatabaseRequest(strava.getAuthenticatedAthlete, segments)
    }
  }
}

