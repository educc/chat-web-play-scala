package controllers

import actors.{GuestRoomActor, RoomManagerActor}
import akka.actor.{ActorRef, ActorSystem}
import javax.inject.{Inject, Named, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import play.api.Logging
import play.api.libs.streams.ActorFlow

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class ChatController @Inject()(@Named("room-manager") roomManagerActor: ActorRef,
                               cc: ControllerComponents)
                              (implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) with Logging {

  implicit val timeout: Timeout = 5.seconds

  def room(roomId: String) = Action.async {
    this.findRoom(roomId).map {
      case Some(_) =>
        Ok(views.html.chat(roomId))
      case _ =>
        NotFound
    }
  }

  def socket(roomId: String) = WebSocket.acceptOrResult[String, String] { request =>
    logger.info("on socket")
    this.findRoom(roomId).map {
      case Some(roomRef) =>
        Right(ActorFlow.actorRef { out =>
          GuestRoomActor.props(out, roomRef)
        })
      case _ =>
        Left(Forbidden)
    }
  }

  def findRoom(roomId: String) = {
    (roomManagerActor ? RoomManagerActor.FindRoom(roomId))
      .mapTo[RoomManagerActor.RoomFound]
      .map(_.roomActor)
  }
}
