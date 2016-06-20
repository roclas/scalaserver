import java.net.Socket
import java.net.ServerSocket
import java.net.InetAddress
import java.io._
import akka.actor._

object Main{
  val port = 7070
  val WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot"
  val SHUTDOWN_COMMAND = "/SHUTDOWN"
  val stop="stop"

  def main(args: Array[String]) = {
    println("starting Scala Static Web Server!")
    println(s"WEB_ROOT=${WEB_ROOT}")
    akka.Main.main(Array(classOf[MainActor].getName))
  }
}

class MainActor extends Actor {
  var httpServer: ActorRef=null
  override def preStart(): Unit = {
    println(s"${self.path} preStarting...")
    httpServer= context.actorOf(Props[HttpServerActor], "httpServer")
    httpServer ! "start"
  }
  def receive = {
    case Main.stop =>
      println(s"requesting stop to MainActor from ${sender.path}")
      httpServer ! Main.stop
      context.actorSelection("/user/*") ! PoisonPill
      context.stop(self)
    case _ => println(s"${sender.path} is requesting something weird to MainActor")
  }
}

class HttpServerActor extends Actor {
  private var shutdown = false
  override def receive: Receive = {
    case "start" =>{
      implicit val serverSocket: ServerSocket = new ServerSocket(Main.port, 1, InetAddress.getByName("127.0.0.1"))
      while (!shutdown) {
        val socket = serverSocket.accept()
        //val requestActor = context.actorOf(RoundRobinPool(5).props(Props[HttpRequestActor]))
        val requestActor = context.actorOf(Props[HttpRequestActor])
        requestActor ! socket
      }
      sender ! Main.stop
     }
    case Main.stop =>
      println("stopping httpServerActor")
      context.stop(self)
    case _ => println(s"${sender.path} is requesting something weird to HttpServerActor")
  }
}

class HttpRequestActor extends Actor {

  override def receive: Receive = {
    case socket: Socket =>
        val input = socket.getInputStream()
        val output = socket.getOutputStream()
        val request = new Request(input)
        request.parse()
        val response= new Response(output)
        response.setRequest(request)
        response.sendStaticResource()
        socket.close() //check if the previous URI is a shutdown command
        val shutdown = request.uri.toString.equals(Main.SHUTDOWN_COMMAND)
        if(shutdown){
          println(s"requesting ${request.uri} from ${sender.path}")
          context.actorSelection("/user/app") ! Main.stop
        }
  }
}
