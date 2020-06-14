package controllers

import actors.RoomManagerActor
import actors.RoomManagerActor.{GuestByRoomSummarize, RoomAndTotalGuestsModel}
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class StatisticsController @Inject()(@Named("room-manager") roomManagerActor: ActorRef,
                               cc: ControllerComponents)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val timeout: Timeout = 3.seconds

  implicit val roomAndTotalGuestsWrites: Format[RoomAndTotalGuestsModel] = Json.format[RoomAndTotalGuestsModel]
  implicit val roomSummarizeWrites: Format[GuestByRoomSummarize] = Json.format[GuestByRoomSummarize]

  def roomStatistics = Action.async {
    (roomManagerActor ? RoomManagerActor.GuestByRoom)
      .mapTo[RoomManagerActor.GuestByRoomSummarize]
      .map { msg =>
        Ok(Json.toJson(msg))
      }
  }

}
