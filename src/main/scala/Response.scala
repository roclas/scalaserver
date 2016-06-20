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
}
class Response (outputStream:OutputStream) {
  val output = outputStream
  var request: Request = null

  def setRequest(r: Request) { request = r }


  def sendStaticResource() {

    var file = new File(Main.WEB_ROOT, request.uri)
    var fis: InputStream = null
    try {//TODO:can this be improved?
      if (!file.exists()) { //TODO: this could be optimized by output.writting Resource.FileNotFoundMessage
        file = new File(s"${Main.WEB_ROOT}/../webroot2", Response._404)
      }
      fis = new FileInputStream(file)
      writeToResponse(fis)
    } finally {
      if (fis != null) fis.close()
    }
  }


  def writeToResponse(fis: InputStream) {
    val ch:Int = fis.read(Response.bytes, 0, Response.BUFFER_SIZE)
    if (ch != -1) {
      output.write(Response.bytes, 0, ch)
      writeToResponse(fis)
    }
  }
}