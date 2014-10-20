package my.com

/**
 * Create frame header (typically from tab-delim string) to handle data.
 * CRF2	01_General data and history - bev v.2	C2
 * name = CRF2
 * descr = 01_General data and history - bev v.2
 * key = C2
 */

class FrameHeader(val name: String, val description: String, val key: String) {
  def this(header: String) = {
    this(header.split("\t")(0), header.split("\t")(1), header.split("\t")(2))
  }
  def this(header: Array[String]) = {
    this(header(0), header(1), header(2))
  }

  override def toString = List(name, description, key).mkString("\t")
}
