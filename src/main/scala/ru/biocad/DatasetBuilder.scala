package ru.biocad

import my.com.meta.{DatasetMeta, EventMeta}
import ru.biocad.meta.{ColumnMeta, ColumnParser}

/**
 * Created by roman on 17/11/14.
 */
class DatasetBuilder {
  var columns = Map[String, Array[String]]()
  var events = Array[EventMeta]()
  var data = Array[Array[String]]()
  var colToMerge = Map[String, Map[String, String]]()

  def setData(data: Array[Array[String]]) = this.data = data

  def setEvents(ev: Array[EventMeta]) = this.events = ev

  def setColToMerge(col: Map[String, Map[String, String]]) = this.colToMerge = col

  def groupData(data: Array[Array[String]]) = {
    val result = data.map { x =>
      ColumnParser.parse(x(0).trim) -> x
    }.groupBy(_._1.event).map(x => x._1 -> x._2.groupBy(_._1.frame).map(x => x._1 -> x._2.map(x => x._1 -> x._2.drop(1)).toMap).toMap).toMap
    result
  }

  def mergeColumns(data: Array[Array[String]], merge: Map[String, Map[String, String]]) = {
    val evCols = data
    null
  }

  def getMergedData(colToMerge: Map[String, Map[String, String]]) = {
    val result = scala.collection.mutable.Map[String, Array[String]]()
    val patCount = data.head.size
    data.foreach { x =>
      val cid = ColumnParser.parse(x(0).trim)
      val newFrame = colToMerge.getOrElse(cid.event, Map[String, String]()).getOrElse(cid.frame, cid.frame)
      val newCid = new ColumnMeta(cid.value, cid.event, newFrame, cid.version)
      val inres = result.getOrElse(newCid.toString, Array.fill(patCount) {
        ""
      })
      result(newCid.toString) = inres.zip(x).map {
        case (od, nd) =>
          if (od.isEmpty)
            nd
          else
            od
      }
    }
    result.values.toArray
  }

  def split(data: Array[Array[String]]): Unit = {
    data.map(x => x(0).trim -> x).map{ case(k,v) => ColumnParser.parse(k) -> v}.groupBy(_._1.event).map{ case (k, v) => k -> v.groupBy(_._1.frame)}
  }
}
