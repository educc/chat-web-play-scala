package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}


object GuestRoomActor {
  case class Message(msg: String)

  def props(out: ActorRef, room: ActorRef) = Props.create(classOf[GuestRoomActor], out, room)
}

class GuestRoomActor(out: ActorRef, roomRef: ActorRef) extends Actor with ActorLogging with Timers {
  import GuestRoomActor._

  roomRef ! RoomActor.AddGuest(self)

  override def receive: Receive = {

    case Message(msg) =>
      out ! msg // sending message to the web browser

    case msg: String => //message from web browser client
      log.info("from browser: " + msg)
      roomRef ! RoomActor.BroadcastMessage(msg)
  }

}
