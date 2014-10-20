package my.com

import scala.collection.mutable.ArrayBuffer

/**
 * Created by roman on 28/09/14.
 */
class Dataset(val header: DatasetHeader, val data: Map[String, Event], val patients: Array[Array[String]]) {
  def createEvents(data: Array[Array[String]]): scala.collection.immutable.Map[String, Event] = {
    val events = data.head.map(x => new HeaderParser(x.trim).event)
    val eventMap = events.zipWithIndex.groupBy(_._1).map(tpl => tpl._1 -> tpl._2.map(_._2)).toMap
    val result = eventMap.map{case(e, cols) => e -> new Event(e, data.map(row => cols.map(row(_))))}
    result
  }

  def this(header: DatasetHeader, data: Array[Array[String]]) = {
    this(header, {
      val tmp = data.map(_.drop(2).dropRight(1))
      val events = tmp.head.map(x => new HeaderParser(x.trim).event)
      val eventMap = events.zipWithIndex.groupBy(_._1).map(tpl => tpl._1 -> tpl._2.map(_._2)).toMap
      val result = eventMap.map{case(e, cols) => e -> new Event(e, tmp.map(row => cols.map(row(_))))}
      result}, data.map(_.take(2)))
  }

  def this(header: DatasetHeader, data: Array[Array[String]], pat: Array[Array[String]]) = {
    this(header, {
      val events = data.map(_.drop(2).dropRight(1)).head.map(x => new HeaderParser(x.trim).event)
      val eventMap = events.zipWithIndex.groupBy(_._1).map(tpl => tpl._1 -> tpl._2.map(_._2)).toMap
      val result = eventMap.map{case(e, cols) => e -> new Event(e, data.map(row => cols.map(row(_))))}
      result}, pat)
  }

  private def getFramesToMerge(metaData: Array[Array[String]]): Map[String, Array[Array[String]]] = {
    val eventFrames = metaData.zipWithIndex.map{ case (field, index) =>
      if(field(0).startsWith("Study Event Definition ")) { // begin of event section
        var a = index+1
        val buf = new ArrayBuffer[Array[String]]
        while(a != metaData.length && !metaData(a)(0).startsWith("Study Event Definition")) { // parse all frames
          buf += metaData(a)
          a += 1
        }
        field(2) -> buf.toArray
      } else {
        null
      }
    }.filter(_ != null)
    val result = eventFrames.map { case (event, frames) =>
      event -> frames.map { frame =>
        val valName = frame(1).split(" - ")(0)
        val frameNames = frames.map(x => x(1).split(" - ")(0) -> x(2))
        frameNames.filter(_._1 == valName).map(_._2).toList
      }.filter(_.size > 1).distinct.map(_.toArray)
    }.filter(_._2.nonEmpty).toMap
    result
  }

  def getMerged: Dataset = {
    val framesToMerge = header.getFramesToMerge
    val tst = data.map { case (eventName, eventData) =>
      eventName -> new Event(eventName, eventData.getMergeFrames(framesToMerge.getOrElse(eventData.event, null)))
    }
    // TODO: use merged header instead of old header
    val merged = new Dataset(header, tst, patients)
    merged
  }

  def getRows: Array[String] = {
    val eventRows = data.map(_._2.getRows)
    val buf = Array.fill[String](eventRows.head.size){""}
    eventRows.map(x => x.zipWithIndex.foreach{ case (str, row) =>
      buf(row) += str
    })
    buf.zipWithIndex.map{ case(str, idx) => patients(idx).mkString("\t") + "\t" + str.dropRight(1) }
  }
}
