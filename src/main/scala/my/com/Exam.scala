package my.com

import scala.collection.mutable.ArrayBuffer

/**
 * Created by roman on 27/09/14.
 */
class Exam(table: Array[Array[String]], val exam: String, frames: Map[String,Array[Int]]) {
  val data = frames.map{ case (frame, fields) =>
      frame -> new Frame(frame, fields.map(col => table.map(_(col))))
  }.toMap

  def mergeFrames(framesToMerge: Array[Array[String]]): Map[String, Frame] = {
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
    val mergedFrames = mergeFrames(framesToMerge)
    val d = mergedFrames.map(_._2.data.transpose)
    val buf = Array.fill(d.head.size){new ArrayBuffer[String]}
    d.foreach(x => x.zipWithIndex.foreach { case (str, idx) =>
      buf(idx) ++= str
    })
    buf.map(_.toArray)
  }
}
