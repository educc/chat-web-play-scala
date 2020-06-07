package actors

import java.util.stream.Collectors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}

import scala.concurrent.duration._

object GuestRoomActor {
  case class Message(msg: String)

  def props(out: ActorRef, room: ActorRef): Props = Props.create(classOf[GuestRoomActor], out, room)
}

class GuestRoomActor(out: ActorRef, roomRef: ActorRef) extends Actor with ActorLogging with Timers {
  import GuestRoomActor._

  var userName = ""
  roomRef ! RoomActor.AddGuest(self)


  override def receive: Receive = {

    case Message(msg) =>
      out ! msg // sending message to the web browser

    case msg: String => //message from web browser client
      log.info("from browser: " + msg)
      processCommand(msg)

    case Tick =>
      out ! "server.heartbeat"
  }


  override def postStop(): Unit = {
    if (!userName.isEmpty) {
      roomRef ! RoomActor.BroadcastMessage(f"system: bye $userName")
    }
    roomRef ! RoomActor.RemoveGuest(self)
    super.postStop()
  }

  private def processCommand(msg: String) = {
    msg match {
      case s"cmd:name=$userName" =>
        this.userName = userName
        roomRef ! RoomActor.BroadcastMessage("system: welcome " + this.userName)
      case _ =>
        roomRef ! RoomActor.BroadcastMessage(msg)
    }
  }

  private case object Tick
  timers.startTimerAtFixedRate("heartbeat", Tick, 3.seconds)

}
