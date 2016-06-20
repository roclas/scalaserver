import java.io._

/*
HTTP Response = Status-Line
*(( general-header | response-header | entity-header ) CRLF)
CRLF
[ message-body ]
Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
*/

object Response {
  val _404: String = "404.html"
  val BUFFER_SIZE: Int = 1024
  val bytes: Array[Byte] = new Array[Byte](Response.BUFFER_SIZE)
  val FileNotFoundMessage:String = """HTTP/1.1 404 File Not Found
                              |Content-Type: text/html
                              |Content-Length: 23
                              |
                              |<h1>File Not Found</h1>"""
  def Headers200Ok(length:Long):String=s"""HTTP/1.1 200 OK
        |Date: ${java.util.Calendar.getInstance().getTime()}
        |Server: Apache/2.4.7 (Ubuntu)
        |Last-Modified: ${java.util.Calendar.getInstance().getTime()}
        |ETag: "2d26-530743d181846"
        |Accept-Ranges: bytes
        |Content-Length: ${length}
        |Vary: Accept-Encoding
        |Connection: close
        |Content-Type: text/html
        |
        |""".stripMargin

}
class Response (output:OutputStream) {
  var request: Request = null
  def setRequest(r: Request) { request = r }
  def sendStaticResource() {
    var file = new File(Server.WEB_ROOT, request.uri)
    val printStream: PrintStream = new PrintStream(output)
    try {
      if (!file.exists()) printStream.print(Response.FileNotFoundMessage)
      else{
        printStream.print(Response.Headers200Ok(file.length()))
        val fis = new FileInputStream(file)
        Iterator.continually (fis.read).takeWhile(-1 !=).foreach(output.write)
        fis.close()
      }
    }
  }
}

