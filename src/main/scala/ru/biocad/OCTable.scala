package ru.biocad

/**
 * Created by rdegtiarev on 29/01/15.
 */

case class OCTable(table: Map[OCColumn, Map[OCPatient, String]]) {
  def +(that: OCTable): OCTable = {
    OCTable(
      (table.keySet ++ that.table.keySet).map{col =>
        col -> (table.getOrElse(col, Map[OCPatient, String]()) ++ that.table.getOrElse(col, Map[OCPatient, String]()))
      }.toMap
    )
  }

  def merge(ocMeta: OCMeta): OCTable = {
    val framesLib = ocMeta.getFramesLib.map(x => x.description -> x).toMap
    val colToMerge = ocMeta.events.flatMap(_.frames.map(fr => fr.key -> framesLib(fr.description).key)).toMap
    val result = scala.collection.mutable.Map[OCColumn, Map[OCPatient, String]]()

    table.foreach { case (col, x) =>
      val newCol = OCColumn(col.value, col.event, col.repeatEvent, colToMerge(col.frame), col.version)
      result(newCol) = result.getOrElse(newCol, Map[OCPatient, String]()) ++ x
    }

    OCTable(result.toMap)
  }
}
