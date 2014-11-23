package ru.biocad.meta

/**
 * Created by roman on 12/11/14.
 */
class ColumnMeta ( val value: String, val event: String, val frame: String, val version: Int = 0) {
  override def toString = {
    val v = List(value, event, frame).mkString("_")
    if (version != 0)
      v + "_" + version.toString
    else
      v
  }

  def isAffected = List(event, frame).mkString("_")

  def versionedName = if(version != 0) value + "_" + version.toString else value

  def isSameValue(that: ColumnMeta) = value == that.value && version == that.version

  def == (that: ColumnMeta): Boolean = {
    if(this.value == that.value && this.version == that.version)
      true
    else
      false
  }

  override def equals(o: Any) = o match {
    case that: ColumnMeta => that.value == this.value && that.version == this.version
    case _ => false
  }

  override def hashCode = value.toUpperCase.hashCode
}
