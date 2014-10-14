package my.com

import java.text.ParseException
import java.util.regex.Pattern

/**
 * Parse column header. Format: VALUE_NAME_E##_C##_##
 * If last two digits exist - value name is VALUE_NAME_##, else VALUE_NAME
 * E## for event
 * C## for frame
 */
class HeaderParser(val src: String) {

  val (variableName, event, frame) = getHeaderFields(src)

  def getHeaderFields(src: String): (String, String, String) = {
    val m = Pattern.compile("^(.*)_(E\\d*)_(C\\d*)(?:_(\\d*))*$").matcher(src)
    if (!m.matches) {
      throw new ParseException("Wrong header - no match to: " + src, -1)
    }
    if (m.groupCount != 4 && m.groupCount != 5) {
      throw new ParseException("Wrong header group count: " + m.groupCount.toString, -1)
    }
    val variableName = if(m.group(4) != null){ m.group(1) + "_" + m.group(4) } else { m.group(1) }
    val event = m.group(2)
    val frame = m.group(3)
    (variableName, event, frame)
  }


  def isSameTitle(b:HeaderParser): Boolean = {
    if(variableName == b.variableName && event == b.event)
      true
    else
      false
  }

  override def toString(): String = src
}
