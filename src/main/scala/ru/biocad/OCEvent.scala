package ru.biocad

/**
 * Handle header of event, for example "Study Event Definition 1	Screening	E1"
 * @param name - commonly "Study Event Definition #"
 * @param description - description of event, "Screening", "Therapy", etc.
 * @param key - E# or E##, etc.
 */
case class OCEvent(name: String, description: String, key: String, frames: Array[OCFrame]) {
  def +(that: OCEvent): OCEvent = OCEvent(name, description, key, (frames ++ that.frames).distinct)

  def ==(that: OCFrame): Boolean = this.name == that.name && this.description == that.description && that.key == this.key

  override def equals(o: Any) = o match {
    case that: OCEvent => this == that
    case _ => false
  }

  def framesToMerge: Array[Array[String]] = {
    frames.map{frame =>
      frames.map(x => x.description -> x.key).filter(_._1 == frame.description).map(_._2)
    }.filter(_.size > 1).distinct.map(_.toArray)
  }

  override def toString = List(name, description, key).mkString("\t")
}

object OCEvent {
  def apply(header: String, framesMeta: Array[OCFrame]): OCEvent = {
    val sp = header.split("\t")
    new OCEvent(sp(0), sp(1), sp(2), framesMeta)
  }

  def apply(header: Array[String], framesMeta: Array[OCFrame]): OCEvent = {
    OCEvent(header(0), header(1), header(2), framesMeta)
  }
}
