package controllers

import actors.RoomManagerActor
import akka.actor.ActorRef
import javax.inject.{Inject, Named, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
@Singleton
class ChatController @Inject()(@Named("room-manager") roomManagerActor: ActorRef,
                               cc: ControllerComponents)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5.seconds

  def room(roomId: String) = Action.async {
    (roomManagerActor ? RoomManagerActor.FindRoom(roomId))
      .mapTo[RoomManagerActor.RoomFound]
      .map { msg =>
        msg.roomActor match {
          case Some(actorRef) =>
            Ok(views.html.chat(roomId))
          case _ =>
            NotFound
        }
      }
  }

}
