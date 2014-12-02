package my.com.meta

/**
 * Handle header of OpenClinica data
 */
class DatasetMeta(val name: String, val description: String, val status: String,
                  val study: String, val id: String, val date: String, val subjects: Int,
                  val events: Array[EventMeta]
                   ) {

  val frames = events.map(_.frames).reduce(_ ++ _).distinct
  val values = frames.map(_.values).reduce(_ ++ _).distinct

  def columnsToMerge: Map[String, Map[String, String]] = {
    getFramesToMerge.map { case (eventKey, arrFrameKeys) =>
      eventKey -> arrFrameKeys.map(x =>
        x.map(_ -> x(0)).filter(x => x._1 != x._2).toMap)
        .reduce(_ ++ _)
    }
  }

  def getFramesToMerge: Map[String, Array[Array[String]]] = {
    val result = events.map { e =>
      e.key -> e.framesToMerge
    }.filter(_._2.nonEmpty).toMap
    result
  }

  def getFramesLib: Array[FrameMeta] = {
    val framesToMerge = frames.map { frame =>
      val frameName = frame.description
      val frameNames = frames.map(x => x.description -> x.key)
      frameName -> frameNames.filter(_._1 == frameName).map(_._2).toArray
    }.filter(_._2.size > 1).toMap
    val superFramesMap = frames.map(f => f.key -> f)
    val restFrames = frames.filter { fr => !framesToMerge.values.reduce(_ ++ _).contains(fr.key)}
    val unitedFrames = framesToMerge.map { case (fname, keys) =>
      val res = superFramesMap.filter { case (key, frame) =>
        keys.contains(key)
      }
        .toMap.values.reduce(_ + _)
      new FrameMeta(res.name, fname, res.key, res.values)
    }
    restFrames ++ unitedFrames
  }

  override def toString = {
    List("Dataset Name:\t" + name,
      "Dataset Description:\t" + description,
      "Item Status:\t" + status,
      "Study Name:\t" + study,
      "Protocol ID:\t" + id,
      "Date:\t" + date,
      "Subjects:\t" + subjects,
      "Study Event Definitions\t" + events.length.toString,
      events.map(e => List(e.toString, e.frames.map(_.toString).mkString("\n")).mkString("\n")).mkString("\n")).mkString("\n")
  }

  def +(that: DatasetMeta): DatasetMeta = {
    new DatasetMeta(
      name,
      description,
      status,
      study,
      id,
      date,
      subjects,
      that.events ++ this.events
    )
  }
}
