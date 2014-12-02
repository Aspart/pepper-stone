package my.com

import scala.collection.mutable
import scala.io.Source

/**
 * Created by roman on 28/10/14.
 */
class Mapper {
  def createMapper(file: String): Map[String, Map[String, (String, Map[String, String])]] = {
    val mapper = Source.fromFile(file).getLines().map(_.split(";"))
      .drop(1) /* drop header */
      .map { x => if (x.length == 6) x.dropRight(1) else x} /* drop description */
      .toArray.dropRight(1) /* drop strange shit at the bottom */
      .filter(_.length == 5) /* filter strange shit at the bottom */
    var lastFrame = ""
    var lastOldValueName = ""
    var lastNewValueName = ""
    val valsMap = mutable.Map[String, String]() // old value to new value
    val namesMap = mutable.Map[String, (String, Map[String, String])]() // old value name to new value name
    val result = mutable.Map[String, Map[String, (String, Map[String, String])]]() // result map for each exam
    mapper.map(row =>
      if (row.nonEmpty) {
        if (row(0).nonEmpty) {
          if (lastFrame.nonEmpty) {
            result(lastFrame) = namesMap.toMap
          }
          namesMap.clear()
          lastFrame = row(0)
        }
        if (row(1).nonEmpty && row(2).nonEmpty) {
          if (lastNewValueName.nonEmpty && lastOldValueName.nonEmpty) {
            namesMap(lastOldValueName) = (lastNewValueName, valsMap.toMap)
          }
          valsMap.clear()
          lastOldValueName = row(1)
          lastNewValueName = row(2)
        }
        if (row(3).nonEmpty && row(4).nonEmpty)
          valsMap(row(3)) = row(4)
      }
    )
    result.toMap
  }
}


