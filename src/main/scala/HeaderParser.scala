import java.text.ParseException
import java.util.regex.Pattern

/**
 * Created by roman on 25/09/14.
 */
class HeaderParser(title: String) {
  val m = Pattern.compile("^(.*)_(E\\d*)_(C\\d*)(?:_(\\d*))*$").matcher(title)
  if (!m.matches) {
    throw new ParseException("Wrong header - no match to: " + title, -1)
  }
  if (m.groupCount != 4 && m.groupCount != 5) {
    throw new ParseException("Wrong header group count: " + m.groupCount.toString, -1)
  }
  val src = title
  val grps = m.groupCount()
  val variableName = if(m.group(4) != null){ m.group(1) + "_" + m.group(4) } else { m.group(1) }
  val exam = m.group(2)
  val frame = m.group(3)

  def isSameTitle(b:HeaderParser): Boolean = {
    if(variableName == b.variableName && exam == b.exam)
      true
    else
      false
  }

  override def toString(): String = src
}
