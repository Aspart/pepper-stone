package ru.biocad

/**
 * Create frame header (typically from tab-delim string) to handle data.
 * CRF2	01_General data and history - bev v.2	C2
 * name = CRF2
 * descr = 01_General data and history - bev v.2
 * key = C2
 */
case class OCFrame(name: String, description: String, key: String, columns: Array[OCColumn]) {
  def +(that: OCFrame): OCFrame = OCFrame(name, description, key, (columns ++ that.columns.map(_.changeFrame(key))).distinct)

  def ==(that: OCFrame): Boolean = this.name == that.name && this.description == that.description && that.key == this.key

  override def equals(o: Any) = o match {
    case that: OCFrame => that.name == this.name && that.description == this.description && that.key == this.key
    case _ => false
  }

  override def hashCode = toString.hashCode

  override def toString = List(name, description, key).mkString("\t")
}

object OCFrame {
  def apply(header: String, columnsMeta: Array[OCColumn]): OCFrame = {
    val sp = header.split("\t")
    new OCFrame(sp(0), sp(1).split(" - ")(0), sp(2), columnsMeta)
  }

  def apply(header: Array[String], columnsMeta: Array[OCColumn]) = {
    new OCFrame(header(0), header(1), header(2), columnsMeta)
  }
}
