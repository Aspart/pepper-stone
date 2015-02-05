package ru.biocad

import java.io.{PrintWriter, File}

/**
 * Created by rdegtiarev on 29/01/15.
 */

/**
 * Store data from OpenClinica dataset.
 * @param table - column -> patient -> value map
 */
case class OCTable(table: Map[OCColumn, Map[OCPatient, String]]) {
  def +(that: OCTable): OCTable = {
    // TODO: add event to table.getOrElse(...)
    val keys = table.keySet.toSeq ++ that.table.keySet.toSeq
    OCTable(
      (table.keySet.toSeq ++ that.table.keySet.toSeq).map{col =>
        col -> {
            val a = table.getOrElse(col, Map[OCPatient, String]())
            val b = that.table.getOrElse(col, Map[OCPatient, String]())
            a ++ b
          }
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
      result(newCol) = {
        val tmp = result.getOrElse(newCol, Map[OCPatient, String]())
        (x.keys ++ tmp.keys).toArray.distinct.map{p =>
          p -> {
            val a = tmp.getOrElse(p, "")
            val b = x.getOrElse(p, "")
            if(a.nonEmpty && b.nonEmpty && a != b) {
              throw new RuntimeException(s"Data for patient $p in column $newCol present twice")
            } else if(a.nonEmpty) {
              a
            } else {
              b
            }
          }
        }
      }.toMap
    }

    OCTable(result.toMap)
  }

  override def toString: String = {
    val patients = table.values.map(_.keys).reduce(_++_).toList.distinct
    val cols = table.map{case(col, data) => col.toString +: patients.map(p => data.getOrElse(p, ""))}
    val tab = Iterable("Study Subject ID" +: patients.map(_.id)) ++ cols
    val result = tab.transpose.map(_.mkString("\t")).mkString("\n")
    result
  }

  // TODO: implement functions below
  /**
   * Create dir for each event, create dir for each frame in event, save all values from this event+frame to single file
   * @param outputDir - output directory
   */
  def exportEventFrame(outputDir: String) = {

  }

  /**
   * Create dir for each frame, create file for each value, save value at all events
   * @param outputDir - output directory
   */
  def exportFrameValue(outputDir: String, ocMeta: OCMeta) = {
    val frameLib = ocMeta.getFramesLib.map(f => f.key -> f.description).toMap
    val patients = table.values.map(_.keys).reduce(_++_).toSeq.distinct.sortBy(_.toString)
    val frameValues = table.groupBy(_._1.frame).map(x => x._1 -> x._2.groupBy(_._1.withVer))
    val export = frameValues.map{case(frame, values) => frame ->
      values.map{ case(value, columns) =>
          value -> columns.map{case(column, data) =>
            column -> patients.map(p => data.getOrElse(p, "")).toArray
          }.toSeq.sortBy(_._1.event.stripPrefix("E"))
      }
    }
    val export2 = export.map{case(frameFile, frameData) =>
      (frameFile + " - " + frameLib(frameFile)) -> frameData.map{case(valueFile, valueData) =>
        valueFile -> (Array("" +: patients.map(_.id).toArray) ++ valueData.map(x => x._1.toString +: x._2))
      }
    }
    export2.foreach{case(dirName, frameData) =>
      val currentDir = new File(outputDir, dirName)
      if(!currentDir.exists() || !currentDir.isDirectory) {
        currentDir.mkdir()
      }
      frameData.foreach{case(fileName, valueData) =>
        val currentFile = new File(currentDir, fileName)
        val writer = new PrintWriter(currentFile)
        valueData.transpose.map(_.mkString("\t")).foreach(l => writer.println(l))
        writer.close()
      }
    }
  }

  /**
   * Create file and save all values from #order
   * @param outputDir - output directory
   * @param order - array of ordered columns to be exported
   */
  def exportOrdered(outputDir: String, order: Array[OCColumn]) = {

  }
}
