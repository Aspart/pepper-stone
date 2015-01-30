package ru.biocad

/**
 * Created by rdegtiarev on 29/01/15.
 */

/**
 * Store data from OpenClinica dataset.
 * @param table - column -> patient -> value map
 */
case class OCTable(table: Map[OCColumn, Map[OCPatient, String]]) {
  def +(that: OCTable): OCTable = {
    OCTable(
      (table.keySet ++ that.table.keySet).map{col =>
        col -> (table.getOrElse(col, Map[OCPatient, String]()) ++ that.table.getOrElse(col, Map[OCPatient, String]()))
      }.toMap
    )
  }

  /**
   * Find same frames in ocMeta and process merge of data related to these frames. Duplicate frames could be found using description field
   * @param ocMeta contains info about original OpenClinica frames
   * @return
   */
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

  // TODO: review OCDataExporter and implement functions below

  /**
   * Create dir for each event, create dir for each frame in event, save all values from this event+frame to single file
   * @param dir - output directory
   */
  def exportEventFrameValue(dir: String) = {

  }

  /**
   * Create dir for each frame, create file for each value, save value at all events
   * @param dir - output directory
   */
  def exportFrameValueEvent(dir: String) = {

  }

  /**
   * Create file and save all values from #order
   * @param dir - output directory
   * @param order - array of ordered columns to be exported
   */
  def exportOrdered(dir: String, order: Array[OCColumn]) = {

  }
}
