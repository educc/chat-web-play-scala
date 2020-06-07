package actors

import akka.actor.{Actor, ActorLogging, Props}


object RoomActor {
  def props(name: String) = Props(classOf[RoomActor], name)

}

class RoomActor(name: String) extends Actor with ActorLogging {

  override def receive: Receive = {
    case _ =>
      log.info("hi there")
  }
}
