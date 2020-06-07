package controllers

import actors.RoomManagerActor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.{Inject, Named, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class RoomController @Inject()(@Named("room-manager") roomManagerActor: ActorRef,
                               cc: ControllerComponents)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {
    implicit val timeout: Timeout = 5.seconds

  def createRoom = Action.async {
    (roomManagerActor ? RoomManagerActor.Create)
      .mapTo[RoomManagerActor.RoomCreated]
      .map { msg =>
        Redirect("/chat/" + msg.roomName)
      }
  }

}
