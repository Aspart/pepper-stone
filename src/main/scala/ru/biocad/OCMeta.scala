package ru.biocad

/**
 * Meta for OCTable class
 * @param events - meta about all events in OCTable
 */
case class OCMeta(events: Array[OCEvent]) {
  def +(that: OCMeta): OCMeta = OCMeta(that.events ++ this.events)

  /**
   * Get unique frames (from description)
   * @return
   */
  def getFramesLib: Array[OCFrame] = {
    val frames = events.map(_.frames).reduce(_ ++ _).distinct
    val framesToMerge = frames.map {frame =>
      frame.description -> frames.filter(_.description == frame.description).map(_.key)
    }.filter(_._2.size > 1).toMap

    frames.filter{fr => !framesToMerge.values.reduce(_ ++ _).contains(fr.key)} ++
      framesToMerge.map { case (desc, keys) =>
        frames.filter(f => keys.contains(f.key)).reduce(_ + _)
    }
  }

  /**
   * Merge meta frames
   * @return
   */
  def merge: OCMeta = {
    val framesLib = getFramesLib.map(x => x.description -> x).toMap
    // merge frames
    OCMeta(events.map(event =>
      OCEvent(event.name, event.key, event.description, event.frames.map(f => framesLib(f.description)).distinct)
    ))
  }

  override def toString = {
    List(
      "Study Event Definitions\t" + events.length.toString,
      events.map(e => List(e.toString, e.frames.map(_.toString).mkString("\n")).mkString("\n")).mkString("\n")).mkString("\n")
  }
}

object OCMeta {
  def apply(lines: Array[Array[String]], header: Array[OCColumn]): OCMeta = {
    // parse events, frames, values
    val eventLines = lines.map(_.mkString("\t")).filter(!_.contains(":")).map(_.split("\t")).drop(1)

    // check for frames not in header
    val frameNames = createFramesLib(eventLines)
    val eventNames = createEventsLib(eventLines)

    val eventsMeta = createEvents(header, eventNames, frameNames)

    OCMeta(eventsMeta)
  }

  private def createFramesLib(eventMeta: Array[Array[String]]): Map[String, OCFrame] = {
    eventMeta.filter(!_(0).startsWith("Study Event Definition")).map(x => x(2) -> OCFrame(x(0), x(1).split(" - ")(0), x(2), Array[OCColumn]())).toMap
  }

  private def createEventsLib(eventMeta: Array[Array[String]]): Map[String, OCEvent] = {
    eventMeta.filter(_(0).startsWith("Study Event Definition")).map(x => x(2) -> OCEvent(x(0), x(1), x(2), Array[OCFrame]())).toMap
  }

  private def createEvents(columns: Array[OCColumn], eventNames: Map[String, OCEvent], frameNames: Map[String, OCFrame]): Array[OCEvent] = {
    columns.groupBy(_.event).map { case (event, evValues) =>
      val eventTemplate = eventNames.get(event).get
      OCEvent(eventTemplate.name,
        eventTemplate.description,
        eventTemplate.key,
        evValues.groupBy(_.frame).map { case (frame, frValues) =>
          frameNames.getOrElse(frame, OCFrame("CRF" + frame.stripPrefix("C"), "", frame, frValues)) match {
            case t => OCFrame (t.name, t.description, t.key, frValues)
          }
        }.toArray
      )
    }.toArray
  }
}
