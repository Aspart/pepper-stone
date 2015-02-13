package ru.biocad

import java.text.ParseException
import java.util.regex.Pattern

/**
 * Created by roman on 12/11/14.
 */

/**
 * Container to store column data
 * @param value - value name
 * @param event - event E##
 * @param frame - frame C##
 * @param version - last number
 */
case class OCColumn(value: String, event: String, repeatEvent: Int = 0, frame: String, version: Int = 0) {
  def changeFrame(newFrame: String) = OCColumn(value, event, repeatEvent, newFrame, version)
  def changeEventAndFrame(newEvent: String, newFrame: String) = OCColumn(value, newEvent, repeatEvent, newFrame, version)

  def getVersion = if (version != 0) "_" + version.toString else ""
  def getEventVersion = if (repeatEvent != 0) "_" + repeatEvent.toString else ""

  override def toString = s"${value}_$event${getEventVersion}_$frame" + getVersion

  def withVerAndFrame = s"${withVer}_$frame"

  def withVer = value + getVersion

  override def equals(o: Any) = o match {
    case that: OCColumn => this.toString == that.toString
    case _ => false
  }

  def ==(that: OCColumn): Boolean = this.withVerAndFrame == that.withVerAndFrame

  override def hashCode = toString.hashCode
}

/**
 * Parse column header. Format: VALUE_NAME_E##_##_C##_##
 * If last two digits exist - value name is VALUE_NAME_##, else VALUE_NAME
 * E##_## for event: if last two digits not exist E## else E##_##
 * C## for frame
 */
object OCColumn {
  def apply(s: String) = {
    val m = Pattern.compile("^(.*)_(E\\d*)(?:_(\\d*))*_(C\\d*)(?:_(\\d*))*$").matcher(s)
    if (m.matches) {
      if (m.groupCount != 4 && m.groupCount != 5) {
        throw new ParseException("Wrong header group count: " + m.groupCount.toString, -1)
      }
      val value = {
        val tmp = m.group(1)
        if(tmp == "MAX_D_mm") {
          "MAX_D"
        } else if (tmp == "SUM_mm") {
          "SUM"
        } else
          tmp
      }
      val event = m.group(2)
      val repeat = if (m.group(3) != null) m.group(3).toInt else 0
      val frame = m.group(4)
      val ver = if (m.group(5) != null) m.group(5).toInt else 0
      new OCColumn(value, event, repeat, frame, ver)
    } else {
      throw new ParseException("Wrong header - no match to: " + s, -1)
    }
  }
}