package my.com

/**
 * Handle header of event, for example "Study Event Definition 1	Screening	E1"
 * @param name - commonly "Study Event Definition #"
 * @param description - description of event, "Screening", "Therapy", etc.
 * @param key - E# or E##, etc.
 */
class EventHeader(val name: String, val description: String, val key: String, val frames: Array[FrameHeader]) {
  def this(header: String, frames: Array[FrameHeader]) = {
    this(header.split("\t")(0), header.split("\t")(1), header.split("\t")(2), frames)
  }

  def this(header: Array[String], frames: Array[FrameHeader]) = {
    this(header(0), header(1), header(2), frames)
  }

  override def toString = List(name, description, key).mkString("\t")
}
