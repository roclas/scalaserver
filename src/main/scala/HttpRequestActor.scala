import java.net.Socket

import akka.actor.{Actor, ActorRef}

class HttpRequestActor(server: ActorRef) extends Actor {

  override def receive: Receive = {

    case requestSocket: Socket =>
        val input = requestSocket.getInputStream
        val output = requestSocket.getOutputStream
        val request = new Request(input)
        request.parse()
        val shutdown = request.uri.toString.equals(HttpServerActor.Shutdown)
        if(shutdown){
          println(s"requesting ${request.uri} from ${sender.path}")
          server ! HttpServerActor.Stop
        } else {
          val response= new Response(output)
          response.setRequest(request)
          response.sendStaticResource()
          requestSocket.close()
        }

  }
}
