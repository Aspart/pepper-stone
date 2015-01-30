package ru.biocad

/**
 * Meta for OCTable class
 * @param events - meta about all events in OCTable
 */
case class OCMeta(events: Array[OCEvent]) {
  val frames = events.map(_.frames).reduce(_ ++ _).distinct
  val values = frames.map(_.columns).reduce(_ ++ _).distinct

  def +(that: OCMeta): OCMeta = OCMeta(that.events ++ this.events)

  /**
   * Get unique frames (from description)
   * @return
   */
  def getFramesLib: Array[OCFrame] = {
    val framesToMerge = frames.map {frame =>
      frame.description -> frames.filter(_.description == frame.description).map(_.key)
    }.filter(_._2.size > 1).toMap

    frames.filter{fr => !framesToMerge.values.reduce(_ ++ _).contains(fr.key)} ++ framesToMerge.map { case (desc, keys) =>
      frames.filter(f => keys.contains(f.key)).reduce(_ + _) match {
        case res => OCFrame(res.name, desc, res.key, res.columns)
      }
    }
  }

  /**
   * Merge meta frames
   * @return
   */
  def merge: OCMeta = {
    val framesLib = getFramesLib.map(x => x.description -> x).toMap
    val remap = scala.collection.mutable.Map[String, String]()
    // merge frames
    val result = events.map { event =>
      val superFrames = event.frames.map { fr =>
        val template = framesLib(fr.description)
        val trueValues = template.columns.map(v => new OCColumn(v.value, event.key, v.repeatEvent, template.key, v.version))
        remap(fr.key) = template.key
        new OCFrame(template.name, template.description, template.key, trueValues)
      }
      // create list of frames to remove and remove duplicates
      val framesToFilter = event.framesToMerge
      val filteredFrames = superFrames.filter(fr => !framesToFilter.map(_.drop(1)).fold(Array.empty)(_ ++ _).contains(fr.key))
      new OCEvent(event.name, event.key, event.description, filteredFrames)
    }
    OCMeta(result)
  }

  override def toString = {
    List(
      "Study Event Definitions\t" + events.length.toString,
      events.map(e => List(e.toString, e.frames.map(_.toString).mkString("\n")).mkString("\n")).mkString("\n")).mkString("\n")
  }
}

object OCMeta {
  def apply(meta: Array[Array[String]], header: Array[OCColumn]): OCMeta = {
    // parse events, frames, values
    val eventMeta = meta.map(_.mkString("\t")).filter(!_.contains(":")).map(_.split("\t")).drop(1)

    val frameNames = createFrameNames(eventMeta)
    val eventNames = createEventNames(eventMeta)
    val eventsMeta = createEventsMeta(header, eventNames, frameNames)

    // check for frames not in header
    OCMeta(eventsMeta)
  }

  private def createFrameNames(eventMeta: Array[Array[String]]): Map[String, OCFrame] = {
    eventMeta.filter(!_(0).startsWith("Study Event Definition")).map(x => x(2) -> OCFrame(x(0), x(1).split(" - ")(0), x(2), Array[OCColumn]())).toMap
  }

  private def createEventNames(eventMeta: Array[Array[String]]): Map[String, OCEvent] = {
    eventMeta.filter(_(0).startsWith("Study Event Definition")).map(x => x(2) -> OCEvent(x(0), x(1), x(2), Array[OCFrame]())).toMap
  }

  private def createEventsMeta(columns: Array[OCColumn], eventNames: Map[String, OCEvent], frameNames: Map[String, OCFrame]): Array[OCEvent] = {
    columns.groupBy(_.event).map { case (event, evValues) =>
      val eventTemplate = eventNames.get(event).get
      new OCEvent(eventTemplate.name,
        eventTemplate.description,
        eventTemplate.key,
        evValues.groupBy(_.frame).map { case (frame, frValues) =>
          frameNames.getOrElse(frame, new OCFrame("CRF" + frame.stripPrefix("C"), "", frame, frValues)) match {
            case t => OCFrame (t.name, t.description, t.key, frValues)
          }
        }.toArray
      )
    }.toArray
  }
}
