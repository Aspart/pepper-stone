package ru.biocad.meta

import java.text.ParseException
import java.util.regex.Pattern

/**
 * Parse column header. Format: VALUE_NAME_E##_C##_##
 * If last two digits exist - value name is VALUE_NAME_##, else VALUE_NAME
 * E## for event
 * C## for frame
 */
object ColumnParser {
  def parse(src: String): ColumnMeta = {
    val m = Pattern.compile("^(.*)_(E\\d*)_(C\\d*)(?:_(\\d*))*$").matcher(src)
    val m2 = Pattern.compile("^(.*)_(E\\d*)(?:_(\\d*))*_(C\\d*)(?:_(\\d*))*$").matcher(src)
    if (m.matches) {
      if (m.groupCount != 4 && m.groupCount != 5) {
        throw new ParseException("Wrong header group count: " + m.groupCount.toString, -1)
      }

      val value = m.group(1)
      val event = m.group(2)
      val frame = m.group(3)
      val ver = if(m.group(4) != null) m.group(4).toInt else 0
      new ColumnMeta(value, event, frame, ver)
    } else if (m2.matches) {
      val value = m2.group(1)
      val event = m2.group(2)
      val frame = m2.group(4)
      val ver = if(m2.group(3) != null) m2.group(3).toInt else 0
      new ColumnMeta(value, event, frame, ver)
    } else {
      throw new ParseException("Wrong header - no match to: " + src, -1)
    }
  }
}
