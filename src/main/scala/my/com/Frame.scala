package my.com

/**
 * Created by roman on 27/09/14.
 */
class Frame(val frame: String, val data: Array[Array[String]]) {
  def +(that: Frame) = {
    val arrayOfHeaders = scala.collection.mutable.ArrayBuffer[String]()
    arrayOfHeaders ++= this.data(0).map{x => new HeaderParser(x.trim).variableName}
    arrayOfHeaders ++= that.data(0).map{x => new HeaderParser(x.trim).variableName}
    val unique = arrayOfHeaders.distinct
    val mergedFrame = Array.ofDim[String](this.data.length, unique.length)
    val thisReIndex = this.data(0).zipWithIndex.map{ case (x, index) => index -> unique.indexOf(new HeaderParser(x.trim).variableName)}
    thisReIndex.foreach{ case(oldCol, newCol) =>
      this.data.zipWithIndex.foreach{ case(field, row) =>
        if(!field(oldCol).isEmpty) mergedFrame(row)(newCol) = field(oldCol)
      } }
    val thatReIndex = that.data(0).zipWithIndex.map{ case (x, index) => index -> unique.indexOf(new HeaderParser(x.trim).variableName)}
    thatReIndex.foreach{ case (oldCol, newCol) =>
      that.data.zipWithIndex.foreach{ case(field, row) =>
        if(!field(oldCol).isEmpty) mergedFrame(row)(newCol) = field(oldCol)
      }
    }
    new Frame(frame, mergedFrame)
  }

  def getRows: Array[String] = {
    data.map(_.mkString("\t"))
  }
}
