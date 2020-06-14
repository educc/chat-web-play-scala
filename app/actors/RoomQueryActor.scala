package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}

import scala.concurrent.duration.FiniteDuration

object RoomQueryActor {
  case class TotalGuestByRoom(data: List[(String, Int)], sender: ActorRef)

  def props(expectedResponses: Int, timeout: FiniteDuration, sender: ActorRef) = Props(classOf[RoomQueryActor], expectedResponses, timeout, sender)
}

class RoomQueryActor(expectedResponses: Int, timeout: FiniteDuration, sender: ActorRef) extends Actor with ActorLogging with Timers {
  import RoomQueryActor._

  private var responsesReceived = 0
  private var responses = List[(String, Int)]()

  override def receive: Receive = {
    case RoomActor.GuestCount(roomName, totalGuests)  =>
      responsesReceived += 1
      responses =  Tuple2(roomName, totalGuests) :: responses

      if (responsesReceived >= expectedResponses) {
        sendDataToParent()
        context.stop(self)
      }

    case Tick =>
      sendDataToParent()
      context.stop(self)
  }

  private case object Tick

  override def preStart(): Unit = {
    timers.startSingleTimer("timeout", Tick, timeout)
    super.preStart()
  }

  def sendDataToParent() = {
    context.parent ! TotalGuestByRoom(responses, sender)
  }
}
