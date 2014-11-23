package my.com.meta

import ru.biocad.meta.ColumnMeta

/**
 * Create frame header (typically from tab-delim string) to handle data.
 * CRF2	01_General data and history - bev v.2	C2
 * name = CRF2
 * descr = 01_General data and history - bev v.2
 * key = C2
 */

class FrameMeta(val name: String, val description: String, val key: String, val values: Array[ColumnMeta]) {
  def this(header: String, values: Array[ColumnMeta]) = {
    this(header.split("\t")(0), header.split("\t")(1), header.split("\t")(2), values)
  }
  
  def this(header: Array[String], values: Array[ColumnMeta]) = {
    this(header(0), header(1), header(2), values)
  }

  def + (that: FrameMeta) = {
    val arrayOfHeaders = scala.collection.mutable.ArrayBuffer[ColumnMeta]()
    arrayOfHeaders ++= this.values
    arrayOfHeaders ++= that.values
    val unique = arrayOfHeaders.distinct
    new FrameMeta(name, description, key, unique.toArray)
  }

  override def toString = List(name, description, key).mkString("\t")
}
