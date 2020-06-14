package actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}

import scala.concurrent.duration._

object RoomManagerActor {
  case object Create
  case object GuestByRoom
  case class FindRoom(roomName: String)

  case class GuestByRoomSummarize(list: List[RoomAndTotalGuestsModel])
  case class RoomCreated(roomName: String)
  case class RoomFound(roomActor: Option[ActorRef])

  case class RoomAndTotalGuestsModel(roomName: String, totalGuests: Int)

  def props: Props = Props[RoomManagerActor]
}

class RoomManagerActor extends Actor with ActorLogging {
  import RoomManagerActor._

  private var rooms = Map[String, ActorRef]()

  override def receive: Receive = {
    case Create =>
      val name = UUID.randomUUID().toString
      val roomRef = context.actorOf(RoomActor.props(name))
      context.watch(roomRef)
      rooms += name -> roomRef
      sender() ! RoomCreated(name)

    case FindRoom(name) =>
      val actorFound = rooms.get(name)
      sender() ! RoomFound(actorFound)

    case GuestByRoom =>
      val queryActor = context.actorOf(RoomQueryActor.props(rooms.size, 3.seconds, sender()))
      rooms.values.foreach { it =>
        it ! RoomActor.HowManyGuest(queryActor)
      }

    case RoomQueryActor.TotalGuestByRoom(data, originalSender) =>
      val newData = data.map(it => new RoomAndTotalGuestsModel(it._1, it._2))
      originalSender ! GuestByRoomSummarize(newData)

    case e: Terminated =>
      rooms.find(_._2 == e.actor).foreach { found =>
        log.info("removing actor room: " + found._1)
        rooms -= found._1
      }
  }
}
