package actors

import actors.HelloActor.SayHello
import akka.actor.{Actor, Props}

object HelloActor {
  case class SayHello(name: String)

  def props = Props[HelloActor]
}

class HelloActor extends Actor {

  override def receive: Receive = {
    case SayHello(name) =>
      sender() ! "Hello, " + name
  }
}
