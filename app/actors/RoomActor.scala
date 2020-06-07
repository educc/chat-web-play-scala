package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}


object RoomActor {
  case class BroadcastMessage(msg: String)
  case class AddGuest(ref: ActorRef)
  case class RemoveGuest(ref: ActorRef)

  def props: Props = Props[RoomActor]
}

class RoomActor extends Actor with ActorLogging {
  import RoomActor._

  private var guests = Map.empty[ActorRef, Int] withDefaultValue 0

  override def receive: Receive = {

    case AddGuest(ref) =>
      guests += ref -> 0
      log.info("welcome, total guests: " + guests.size)

    case RemoveGuest(ref) =>
      log.info("Total guests: " + guests.size)
      log.info(ref.toString())
      guests -= ref
      log.info("Total after remove: " + guests.size)

    case BroadcastMessage(msg) =>
      guests.keys.foreach {ref =>
        ref ! GuestRoomActor.Message(msg)
      }
  }
}
