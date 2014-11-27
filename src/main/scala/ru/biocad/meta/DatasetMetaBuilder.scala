package my.com.meta

import ru.biocad.meta.{ColumnMeta, ColumnParser}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by roman on 19/10/14.
 */
class DatasetMetaBuilder {
  private var name = ""
  private var description = ""
  private var status = ""
  private var study = ""
  private var id = ""
  private var date = ""
  private var subjects = 0
  private var events = Array[EventMeta]()
  private var frames = Array[FrameMeta]()
  private var values = Array[ColumnMeta]()

  def setName(name: String) = this.name = name
  def setDescription(descr: String) = this.description = descr
  def setStatus(status: String) = this.status = status
  def setStudy(study: String) = this.study = study
  def setID(id: String) = this.id = id
  def setDate(date: String) = this.date = date
  def setSubjects(subjects: Int) = this.subjects = subjects
  def setEvents(events: Array[EventMeta]) = {
    this.events = events
    this.frames = events.map(_.frames).reduce(_++_).distinct
    this.values = frames.map(_.values).reduce(_++_).distinct
  }

  def this(meta: DatasetMeta) = {
    this()
    this.setDate(meta.date)
    this.setDescription(meta.description)
    this.setEvents(meta.events)
    this.setID(meta.id)
    this.setName(meta.name)
    this.setStatus(meta.status)
    this.setStudy(meta.study)
    this.setSubjects(meta.subjects)
  }

  def build: DatasetMeta = new DatasetMeta(name, description, status, study, id, date, subjects, events, frames, values)

  def parseHeader(meta: Array[Array[String]], columns: Array[String]) = {
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

    val valuesMeta = columns.map(ColumnParser.parse).groupBy(_.frame)
    val eventMeta = meta.map(_.mkString("\t")).filter(!_.contains(":")).map(_.split("\t")).drop(1)
    val events = eventMeta.zipWithIndex.map{ case (row, index) =>
      if(row(0).startsWith("Study Event Definition ")) { // begin of event section
        var a = index+1
        val frameBuf = new ArrayBuffer[FrameMeta]
        while(a != eventMeta.length && !eventMeta(a)(0).startsWith("Study Event Definition")) { // parse all frames
          frameBuf += new FrameMeta(eventMeta(a), valuesMeta.getOrElse(eventMeta(a)(2), Array[ColumnMeta]()))
          a += 1
        }
        new EventMeta(row, frameBuf.toArray)
      } else {
        null
      }
    }.filter(_ != null)

    setEvents(createFramesWithoutMetaProvided(columns.map(ColumnParser.parse), events))
  }

  // TODO: this seems to be an error situation, should never add anything
  def createFramesWithoutMetaProvided(cm: Array[ColumnMeta], em: Array[EventMeta]): Array[EventMeta] = {
    val frameBuffer = ArrayBuffer[FrameMeta]()
    val columnMeta = cm.groupBy(_.event).map{case(event, vals) => event -> vals.groupBy(_.frame)}
    columnMeta.zip(columnMeta.map{case (key, value) =>
      em.map(x => x.key -> x).toMap.getOrElse(key, null)
    }).map{ case(_eventParsed, _eventProvided) =>
      val trueFrames = _eventParsed._2.map{ case(_frame, _values) =>
          new FrameMeta("CRF"+_frame.stripPrefix("C"), "Noname frame " + _frame, _frame, _values)
      }
      // should be empty frame
      val unknownFrames = trueFrames.filter(fr => !_eventProvided.frames.map(_.key).contains(fr.key)).toArray
      if(unknownFrames.size != 0)
        println("Possible error in " + _eventProvided.name)
      val concat = unknownFrames ++ _eventProvided.frames
      new EventMeta(_eventProvided.name, _eventProvided.description, _eventProvided.key, concat)
    }.toArray
  }
}
