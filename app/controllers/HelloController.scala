package controllers

import actors.HelloActor
import actors.HelloActor.SayHello
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class HelloController @Inject()(system: ActorSystem, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  val helloActor = system.actorOf(HelloActor.props, "hello-actor")

  implicit val timeout: Timeout = 5.seconds

  def sayHello(name: String) = Action.async {
    (helloActor ? SayHello(name))
      .mapTo[String]
      .map { msg =>
        Ok(msg)
      }
  }

}
