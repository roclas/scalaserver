import java.net.{InetAddress, ServerSocket, Socket}

import akka.actor.{Actor, Props}
import akka.routing.RoundRobinPool
import akka.pattern.pipe

import scala.concurrent.{Future, Promise}

class HttpServerActor(address: String, port: Int, processorsAmount: Int, shutdown: Promise[Unit]) extends Actor {

  val requestProcessors = context.actorOf(
    RoundRobinPool(processorsAmount)
      .props(Props(new HttpRequestActor(context.self))))

  val serverSocket: ServerSocket = new ServerSocket(port, 1, InetAddress.getByName(address))

  handleNextRequest

  override def receive: Receive = {

    case request: Socket =>
      requestProcessors forward request
      handleNextRequest


    case HttpServerActor.Stop =>
      println("stopping httpServerActor")
      shutdown.success()

  }

  override def unhandled(message: Any): Unit = {
    println(s"${sender.path} is requesting something weird to HttpServerActor")
    super.unhandled(message)
  }

  //  Asynchronously wait for an incoming request and send it to myself
  def handleNextRequest = {
    import context.dispatcher
    Future(serverSocket.accept()) pipeTo self
  }

}

object HttpServerActor {
  val Stop = "stop"
  val Shutdown = "/SHUTDOWN"
}