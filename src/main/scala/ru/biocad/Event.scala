package my.com

import scala.collection.mutable.ArrayBuffer

/**
 * Created by roman on 27/09/14.
 */
class Event(val event: String, val data: Map[String, Frame]) {

  def this(event: String, data: Array[Array[String]]) = {
    this(event, {
        val frames = data.head.map(x => new HeaderParser(x.trim).frame)
        val frameMap = frames.zipWithIndex.groupBy(_._1).map(tpl => tpl._1 -> tpl._2.map(_._2))
        val result = frameMap.map{case(e, cols) => e -> new Frame(e, data.map(row => cols.map(row(_))))}
        result
      }
    )
  }

  def getMerged(framesToMerge: Array[Array[String]]): Event = {
    new Event(event, getMergeFrames(framesToMerge))
  }

  def getMergeFrames(framesToMerge: Array[Array[String]]): Map[String, Frame] = {
    if (framesToMerge != null) {
      val newFrames = framesToMerge.map {_.flatMap(data.get).reduceLeft(_ + _)}
      val r = framesToMerge.flatMap(x => x)
      val mData = collection.mutable.Map(data.filter(x => !r.contains(x._1)).toSeq: _*)
      newFrames.foreach(f => mData(f.frame) = f)
      mData.toMap
    }
    else data
  }

  def getMergedData(framesToMerge: Array[Array[String]]): Array[Array[String]] = {
    val mergedFrames = getMergeFrames(framesToMerge)
    val d = mergedFrames.map(_._2.data.transpose)
    val buf = Array.fill(d.head.size){new ArrayBuffer[String]}
    d.foreach(x => x.zipWithIndex.foreach { case (str, idx) =>
      buf(idx) ++= str
    })
    buf.map(_.toArray)
  }

  def getRows: Array[String] = {
    val frameRows = data.map(_._2.getRows)
    val buf = Array.fill[String](frameRows.head.size){""}
    frameRows.map(x => x.zipWithIndex.reverseIterator.foreach{ case (str, row) =>
      buf(row) += str + "\t"
    })
    buf
  }
}
