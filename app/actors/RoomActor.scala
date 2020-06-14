package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}


object RoomActor {
  case class BroadcastMessage(msg: String)
  case class AddGuest(ref: ActorRef)
  case class RemoveGuest(ref: ActorRef)
  case class HowManyGuest(replyTo: ActorRef)
  case class GuestCount(roomName: String, count: Int)

  def props(roomName: String): Props = Props.create(classOf[RoomActor], roomName)
}

class RoomActor(roomName: String) extends Actor with ActorLogging {
  import RoomActor._

  private var guests = Map.empty[ActorRef, Int] withDefaultValue 0

  override def receive: Receive = {

    case AddGuest(ref) =>
      guests += ref -> 0
      log.info("welcome, total guests: " + guests.size)

    case RemoveGuest(ref) =>
      log.info(ref.toString())
      guests -= ref

      log.info("Removing guest. Total guests: " + guests.size)
      if (guests.isEmpty) {
        context.stop(self) // stop actor when is a empty room
        log.info("stopping actor room: " + self)
      }

    case HowManyGuest(replyTo) =>
      replyTo ! GuestCount(roomName, guests.size)

    case BroadcastMessage(msg) =>
      guests.keys.foreach {ref =>
        ref ! GuestRoomActor.Message(msg)
      }
  }
}
