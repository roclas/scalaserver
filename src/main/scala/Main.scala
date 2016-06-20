import java.io._

import akka.actor._

import scala.concurrent.duration.Duration
import scala.concurrent.Promise

object Main extends App {
  val address = "127.0.0.1"
  val port = 7070
  val processorsAmount = 5
  val WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot"

  println("starting Scala Static Web Server!")
  println(s"WEB_ROOT=$WEB_ROOT")

  val system = ActorSystem("StaticServerSystem")
  val shutdown = Promise[Unit]()

  val httpServerActor = system.actorOf(Props(
    new HttpServerActor(address, port, processorsAmount, shutdown)))

  import scala.concurrent.ExecutionContext.Implicits.global
  shutdown.future.map{_ =>
    system.shutdown()
    system.awaitTermination(Duration.Inf)
    System.exit(0)
  }

}
