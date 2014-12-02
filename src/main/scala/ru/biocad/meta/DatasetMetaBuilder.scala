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

  def setName(name: String) = this.name = name
  def setDescription(descr: String) = this.description = descr
  def setStatus(status: String) = this.status = status
  def setStudy(study: String) = this.study = study
  def setID(id: String) = this.id = id
  def setDate(date: String) = this.date = date
  def setSubjects(subjects: Int) = this.subjects = subjects
  def setEvents(events: Array[EventMeta]) = this.events = events

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

  def build: DatasetMeta = new DatasetMeta(name, description, status, study, id, date, subjects, events)

  def parseHeader(meta: Array[Array[String]], columns: Array[String]) = {
    // parse supheader
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
    // parse events, frames, values
    val eventMeta = meta.map(_.mkString("\t")).filter(!_.contains(":")).map(_.split("\t")).drop(1)

    val frameNames = createFrameNames(eventMeta)
    val eventNames = createEventNames(eventMeta)
    val eventsMeta = createEventsMeta(columns, eventNames, frameNames)

    // check for frames not in header
    setEvents(eventsMeta)
  }

  def createFrameNames(eventMeta: Array[Array[String]]): Map[String, FrameMeta] = {
    eventMeta.filter(!_(0).startsWith("Study Event Definition")).map(x => x(2) -> new FrameMeta(x(0), x(1).split(" - ")(0), x(2), Array[ColumnMeta]())).toMap
  }

  def createEventNames(eventMeta: Array[Array[String]]): Map[String, EventMeta] = {
    eventMeta.filter(_(0).startsWith("Study Event Definition")).map(x => x(2) -> new EventMeta(x(0), x(1), x(2), Array[FrameMeta]())).toMap
  }

  def createEventsMeta(columns: Array[String], eventNames: Map[String, EventMeta], frameNames: Map[String, FrameMeta]): Array[EventMeta] = {
    columns.map(ColumnParser.parse).groupBy(_.event).map{case(event, evValues) =>
      val eventTemplate = eventNames.get(event).get
      new EventMeta(eventTemplate.name,
        eventTemplate.description,
        eventTemplate.key,
        evValues.groupBy(_.frame).map{case(frame, frValues) =>
          val frameTemplate = frameNames.getOrElse(frame, new FrameMeta("CRF"+frame.stripPrefix("C"), "", frame, frValues))
          new FrameMeta(frameTemplate.name, frameTemplate.description, frameTemplate.key, frValues)
        }.toArray
      )
    }.toArray
  }
}
