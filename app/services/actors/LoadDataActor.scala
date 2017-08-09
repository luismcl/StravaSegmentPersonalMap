package services.actors

import java.time.LocalDateTime
import java.util.{Calendar, Date}
import javastrava.api.v3.auth.impl.retrofit.AuthorisationServiceImpl
import javastrava.api.v3.auth.model.Token
import javastrava.api.v3.service.Strava
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import domain.Athlete
import play.api.mvc.DiscardingCookie

import scala.concurrent.ExecutionContext

case class UpdateUserDataRequest(clientId: Integer, clientSecret: String, authorisationCode: String)
case class UpdateDataBaseRequest(athlete: Athlete)



class LoadDataActor @Inject()(@Named("activitiesActor") activitiesActor: ActorRef,
                              @Named("segmentsActor") segmentsActor: ActorRef,
                              @Named("updateDatabaseActor") updateDatabaseActor: ActorRef,
                              @Named("readDatabaseActor") readDatabaseActor: ActorRef)
                             (implicit executionContext: ExecutionContext) extends Actor with ActorLogging {

  import akka.pattern.ask
  import scala.concurrent.duration._
  implicit val timeout: Timeout = 5.seconds

  override def receive: Receive = {
    case UpdateUserDataRequest(clientId, clientSecret, authorisationCode) => {
      val service = new AuthorisationServiceImpl
      val token = service.tokenExchange(clientId, clientSecret, authorisationCode)

      val future = readDatabaseActor ? ReadAthleteDataRequest(token.getAthlete.getId)
      //If user exist in the DB, does not update.
      val snd = sender()
      future.map {
        case (athlete: Athlete) => {
          log.info(s"Athlete ${athlete._id} Already exist in the Dabatase")
          snd ! athlete
        }
        case (AthleteNotFound(athleteId)) => {
          val athlete:Athlete = Athlete(token.getAthlete, token.getToken)
          snd!athlete

          log.info(s"Load Activities for Athlete ${athlete._id}")
          val from = Calendar.getInstance
          from.add(Calendar.MONTH,-6)
          activitiesActor ! ActivitiesRequest(athlete, from.getTime, Calendar.getInstance.getTime)
        }
      }

    }case UpdateDataBaseRequest(athlete) => {
        log.info(s"Update Activities for Athlete ${athlete._id}")
        val from = athlete.lastUpdate
        activitiesActor ! ActivitiesRequest(athlete, from, Calendar.getInstance.getTime)
    }
    case ActivitiesResponse(athlete, activities) =>
      segmentsActor ! SegmentResumeRequest(athlete, activities)
    case SegmentsResponse(athlete, segments) => {
      updateDatabaseActor ! UpdateDatabaseRequest(athlete, segments)
    }
  }
}

