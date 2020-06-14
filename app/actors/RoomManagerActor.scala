package actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}

object RoomManagerActor {
  case object Create
  case class FindRoom(roomName: String)

  case class RoomCreated(roomName: String)
  case class RoomFound(roomActor: Option[ActorRef])

  def props: Props = Props[RoomManagerActor]
}

class RoomManagerActor extends Actor with ActorLogging {
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
      val actorFound = rooms.get(name)
      sender() ! RoomFound(actorFound)

    case e: Terminated =>
      rooms.find(_._2 == e.actor).foreach { found =>
        log.info("removing actor room: " + found._1)
        rooms -= found._1
      }
  }
}
