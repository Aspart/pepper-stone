package my.com

import java.text.ParseException
import java.util.regex.Pattern

/**
 * Created by roman on 25/09/14.
 */
class HeaderParser(val src: String) {

  val (variableName, exam, frame) = getHeaderFields(src)

  def getHeaderFields(src: String): (String, String, String) = {
    val m = Pattern.compile("^(.*)_(E\\d*)_(C\\d*)(?:_(\\d*))*$").matcher(src)
    if (!m.matches) {
      throw new ParseException("Wrong header - no match to: " + src, -1)
    }
    if (m.groupCount != 4 && m.groupCount != 5) {
      throw new ParseException("Wrong header group count: " + m.groupCount.toString, -1)
    }
    val variableName = if(m.group(4) != null){ m.group(1) + "_" + m.group(4) } else { m.group(1) }
    val exam = m.group(2)
    val frame = m.group(3)
    (variableName, exam, frame)
  }


  def isSameTitle(b:HeaderParser): Boolean = {
    if(variableName == b.variableName && exam == b.exam)
      true
    else
      false
  }

  override def toString(): String = src
}
