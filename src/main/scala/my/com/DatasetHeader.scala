package my.com

/**
 * Handle header of OpenClinica data
 */
class DatasetHeader(val name: String, val description: String, val status: String,
                    val study: String, val id: String, val date: String,
                    val subjects: Int, val events: Array[EventHeader]) {

  def getFramesToMerge: Map[String, Array[Array[String]]] = {
    val result = events.map { e =>
      e.key -> e.frames.map { frame =>
        val valName = frame.description.split(" - ")(0)
        val frameNames = e.frames.map(x => x.description.split(" - ")(0) -> x.key)
        frameNames.filter(_._1 == valName).map(_._2).toList
      }.filter(_.size > 1).distinct.map(_.toArray)
    }.filter(_._2.nonEmpty).toMap
    result
  }

  def reduceByMerged(mergedFrames: Map[String, Array[Array[String]]]) = {
    // TODO: change header according to information about merge
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
}
