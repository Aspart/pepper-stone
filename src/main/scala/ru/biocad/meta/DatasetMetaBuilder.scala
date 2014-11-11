package my.com.meta

import my.com.meta.FrameMeta

import scala.collection.mutable.ArrayBuffer

/**
 * Created by roman on 19/10/14.
 */
class DatasetMetaBuilder {
  var name = ""
  var description = ""
  var status = ""
  var study = ""
  var id = ""
  var date = ""
  var subjects = 0
  var events = Array[EventMeta]()
  var frames = Array[FrameMeta]()
  def setName(name: String) = this.name = name
  def setDescription(descr: String) = this.description = descr
  def setStatus(status: String) = this.status = status
  def setStudy(study: String) = this.study = study
  def setID(id: String) = this.id = id
  def setDate(date: String) = this.date = date
  def setSubjects(subjects: Int) = this.subjects = subjects
  def setEvents(events: Array[EventMeta]) = this.events = events
  def setFrames(frames: Array[FrameMeta]) = this.frames = frames

  def getHeader: DatasetMeta = new DatasetMeta(name, description, status, study, id, date, subjects, events, frames)

  def parseHeader(meta: Array[Array[String]]) = {
    val sup = meta.map(_.mkString("\t").split(":").map(_.trim)).take(7)
    sup.foreach{row => row(0) match {
        case "Dataset Name" => setName(row(1))
        case "Dataset Description" => setDescription(row(1))
        case "Item Status" => setStatus(row(1))
        case "Study Name" => setStudy(row(1))
        case "Protocol ID" => setID(row(1))
        case "Date" => setDate(row(1))
        case "Subjects" => setSubjects(row(1).toInt)
      }
    }
    val eventMeta = meta.map(_.mkString("\t")).filter(!_.contains(":")).map(_.split("\t")).drop(1)
    val frameBuffer = ArrayBuffer[FrameMeta]()
    val events = eventMeta.zipWithIndex.map{ case (row, index) =>
      if(row(0).startsWith("Study Event Definition ")) { // begin of event section
        var a = index+1
        val buf = new ArrayBuffer[FrameMeta]
        while(a != eventMeta.length && !eventMeta(a)(0).startsWith("Study Event Definition")) { // parse all frames
          buf += new FrameMeta(eventMeta(a))
          a += 1
        }
        frameBuffer ++= buf
        new EventMeta(row, buf.toArray)
      } else {
        null
      }
    }.filter(_ != null)
    setFrames(frameBuffer.toArray)
    setEvents(events)
  }
}
