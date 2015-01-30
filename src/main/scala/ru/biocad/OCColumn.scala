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
 * @param repeatEvent - repeat event id, integer
 * @param frame - frame C##
 * @param version - last number
 */
case class OCColumn(value: String, event: String, repeatEvent: Int = 0, frame: String, version: Int = 0) {
  def changeFrame(frame: String) = OCColumn(value, event, repeatEvent, frame, version)

  override def toString = {
    value + "_" + event + "_" +
      (if(repeatEvent!=0) repeatEvent.toString + "_" else "") +
      frame +
      (if(version!=0) "_" + version.toString else "")
  }

  def eventAndFrame = List(event, frame).mkString("_")

  def isEqWithVer(that: OCColumn) = withVer == that.withVer

  def withVerAndFrame = withVer + "_" + frame

  def withVer = value + (if (version != 0) "_" + version.toString else "")

  override def equals(o: Any) = o match {
    case that: OCColumn => this == that
    case _ => false
  }

  def ==(that: OCColumn): Boolean = this.toString == that.toString
  
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
      val value = m.group(1)
      val event = m.group(2)
      val repEvent = if (m.group(3) != null) m.group(3).toInt else 0
      val frame = m.group(4)
      val ver = if (m.group(5) != null) m.group(5).toInt else 0
      new OCColumn(value, event, repEvent, frame, ver)
    } else {
      throw new ParseException("Wrong header - no match to: " + s, -1)
    }
  }
}