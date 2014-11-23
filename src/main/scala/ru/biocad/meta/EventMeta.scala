package my.com.meta


/**
 * Handle header of event, for example "Study Event Definition 1	Screening	E1"
 * @param name - commonly "Study Event Definition #"
 * @param description - description of event, "Screening", "Therapy", etc.
 * @param key - E# or E##, etc.
 */
class EventMeta(val name: String, val description: String, val key: String, val frames: Array[FrameMeta]) {

  def this(header: String, frames: Array[FrameMeta]) = {
    this(header.split("\t")(0), header.split("\t")(1), header.split("\t")(2), frames)
  }

  def this(header: Array[String], frames: Array[FrameMeta]) = {
    this(header(0), header(1), header(2), frames)
  }

  def getFrameKeysByDescr(descrs: Array[String]): Map[String, String] = {
    val keyMap = frames.map{ x =>
      x.description.split(" - ")(0).drop(3) -> x.key
    }.toMap
    descrs.map { x =>
      x -> keyMap.getOrElse(x, null)
    }.toMap
  }

  def framesToMerge: Array[Array[String]] = {
    frames.map { frame =>
      frames.map(x => x.description.split(" - ")(0) -> x.key).filter(_._1 == frame.description.split(" - ")(0)).map(_._2).toList
    }.filter(_.size > 1).distinct.map(_.toArray)
  }

  override def toString = List(name, description, key).mkString("\t")
}
