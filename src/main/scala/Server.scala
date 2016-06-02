import java.net.Socket
import java.net.ServerSocket
import java.net.InetAddress
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException
import java.io.File

import roclas.servers.servlet1.HttpServer1

object Server {
  val port = 7070
  val WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot"
  val SHUTDOWN_COMMAND = "/SHUTDOWN"

  def main(args: Array[String]) = {
    println("starting Scala Static Web Server!")
    println(s"WEB_ROOT=${WEB_ROOT}")
    val server = new Server()
    //println("Now we support servlets as well!")
    //val server = new HttpServer1();
    server.await()

  }
}

class Server {
  private var shutdown = false

  def await() = {
      val serverSocket: ServerSocket = new ServerSocket(Server.port, 1, InetAddress.getByName("127.0.0.1"))
      //TODO:exceptions???
      while (!shutdown) {
        println(s"the server is in a while loop waiting for requests...")
        val socket = serverSocket.accept() // until no request is received by the socket, it stops
        val input = socket.getInputStream() //TODO:exceptions???
        val output = socket.getOutputStream() //TODO:exceptions???
        val request = new Request(input)
        request.parse()
        val response= new Response(output)
        response.setRequest(request)
        response.sendStaticResource()
        socket.close() //check if the previous URI is a shutdown command
        shutdown = request.uri.equals(Server.SHUTDOWN_COMMAND);
      }
    }
}


