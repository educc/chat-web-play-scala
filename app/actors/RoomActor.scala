package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}


object RoomActor {
  case class BroadcastMessage(msg: String)
  case class AddGuest(ref: ActorRef)

  def props  = Props[RoomActor]
}

class RoomActor extends Actor with ActorLogging {
  import RoomActor._

  var guests = Seq[ActorRef]()

  override def receive: Receive = {

    case AddGuest(ref) =>
      guests = guests :+ ref

    case BroadcastMessage(msg) =>
      guests.foreach { ref =>
        ref ! GuestRoomActor.Message(msg)
      }
  }
}
