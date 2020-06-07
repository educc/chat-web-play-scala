package actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}

object RoomManagerActor {
  case object Create
  case class RoomCreated(roomName: String)
  case class FindRoom(roomName: String)
  case class RoomFound(roomActor: Option[ActorRef])

  def props: Props = Props[RoomManagerActor]
}

class RoomManagerActor extends Actor {
  import RoomManagerActor._

  private var rooms = Map[String, ActorRef]()

  override def receive: Receive = {
    case Create =>
      val name = UUID.randomUUID().toString
      val roomRef = context.actorOf(RoomActor.props)
      context.watch(roomRef)
      rooms += name -> roomRef
      sender() ! RoomCreated(name)

    case FindRoom(name) =>
      rooms.get(name) match {
        case Some(actor) =>
          sender() ! RoomFound(Option(actor))
        case _  =>
          sender ! RoomFound(Option.empty)
      }
  }
}
