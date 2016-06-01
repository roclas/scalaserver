import java.io.InputStream
import scala.io.Source

class Request(input: InputStream) {
  var uri:String=_

  def parse() {
    val lines = Source.fromInputStream(input).getLines()
    if (lines.hasNext) {
      uri = parseUri(lines.next())
    }
  }

  def parseUri(requestString: String): String = {
    val pattern = """[^ ]* *([^ ]*) *[\s\S]*""".r
    val pattern(result) = requestString
    result
  }

}