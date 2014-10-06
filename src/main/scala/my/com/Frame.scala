package my.com

/**
 * Created by roman on 27/09/14.
 */
class Frame(val frame: String, val data: Array[Array[String]]) {
    def +(that: Frame) = {
      val arrayOfHeaders = scala.collection.mutable.ArrayBuffer[String]()
      arrayOfHeaders ++= this.data.map{x => new HeaderParser(x(0).trim).variableName}
      arrayOfHeaders ++= that.data.map{x => new HeaderParser(x(0).trim).variableName}
      val unique = arrayOfHeaders.distinct
      val mergedFrame = Array.ofDim[String](unique.length, this.data.head.length)
      this.data.zipWithIndex.map{ case (x, index) => index -> unique.indexOf(new HeaderParser(x(0).trim).variableName)}.
      foreach{ case(oldCol, newCol) =>
        this.data(oldCol).zipWithIndex.map{ case(field, row) =>
          if(!field.isEmpty) mergedFrame(newCol)(row) = field
        } }
      val reIndex = that.data.zipWithIndex.map{ case (x, index) => index -> unique.indexOf(new HeaderParser(x(0).trim).variableName)}
      reIndex.
      foreach{ case (oldCol, newCol) =>
        that.data(oldCol).zipWithIndex.map{ case(field, row) =>
          if(!field.isEmpty) mergedFrame(newCol)(row) = field
        }
      }
    new Frame(frame, mergedFrame)
  }
}
