package my.com

/**
 * Handle header of OpenClinica data
 */
class DatasetHeader(val name: String, val description: String, val status: String,
                    val study: String, val id: String, val date: String,
                    val subjects: Int, val events: Array[EventHeader]) {

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
