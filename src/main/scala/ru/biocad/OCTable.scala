package ru.biocad

import java.io.{FileInputStream, PrintWriter, File}

import scala.collection.immutable.Map

/**
 * Created by rdegtiarev on 29/01/15.
 */

/**
 * Store data from OpenClinica dataset.
 * @param data - column -> patient -> value map
 */
case class OCTable(data: Map[OCColumn, Map[OCPatient, String]]) {
  def +(that: OCTable): OCTable = {
    val columns = data.keySet.toSeq ++ that.data.keySet.toSeq
    OCTable(columns.map{col => col -> {
      data.getOrElse(col, Map[OCPatient, String]()) ++ that.data.getOrElse(col, Map[OCPatient, String]())
      }
    }.toMap)
  }

  /**
   * Find same frames in ocMeta and process merge of data related to these frames. Duplicate frames could be found using description field
   * @param ocMeta contains info about original OpenClinica frames
   * @return
   */
  def merge(ocMeta: OCMeta): OCTable = {
    val framesLib = ocMeta.getFramesLib.map(x => x.description -> x).toMap
    val colToMerge = ocMeta.events.flatMap(_.frames.map(fr => fr.key -> framesLib(fr.description).key)).toMap
    val columnBuffer = data.toSeq.map{ case (col, x) => col.changeFrame(colToMerge(col.frame)) -> x}
    val result = columnBuffer.map { case (col, colDat) =>
      col -> columnBuffer.filter(_._1.toString == col.toString).map(_._2).reduce[Map[OCPatient, String]]{ case (left, right) =>
        (left.keys.toSeq ++ right.keys.toSeq).distinct.map { p =>
          p -> {
            val l = left.getOrElse(p, "")
            val r = right.getOrElse(p, "")
            if (l.nonEmpty && r.nonEmpty && l != r)
              throw new RuntimeException(s"Data for patient $p in column $col present twice")
            else if (l.nonEmpty)
              l
            else
              r
          }
        }.toMap
      }
    }.toMap
    OCTable(result)
  }

  override def toString: String = {
    val patients = data.values.map(_.keys).reduce(_++_).toList.distinct
    val cols = data.map{case(col, ds) => col.toString +: patients.map(p => ds.getOrElse(p, ""))}
    val tab = Iterable("Study Subject ID" +: patients.map(_.id)) ++ cols
    val result = tab.transpose.map(_.mkString("\t")).mkString("\n")
    result
  }

  /**
   * Create dir for each frame, create file for each value, save value at all events
   * @param outputDir - output directory
   */
  def exportFrameValue(outputDir: String, ocMeta: OCMeta, patientOrder: String) = {
    val frameLib = ocMeta.getFramesLib.map(f => f.key -> f.description).toMap

    val patients = {
      if(patientOrder.isEmpty) {
        data.values.map(_.keys).reduce(_ ++ _).toSeq.distinct.sortBy(_.toString).toArray
      }
      else {
        val tmp = scala.io.Source.fromInputStream(new FileInputStream(new File(patientOrder))).getLines().map(_.split("\t").take(2)).toArray
        val idx = tmp.map(_.head).indexWhere(_=="Study Subject ID")
        tmp.drop(idx).map(OCPatient(_)).toArray
      }
    }
    val frameValues = data.groupBy(_._1.frame).map(x => x._1 -> x._2.groupBy(_._1.withVer))
    val export = frameValues.map{case(frame, values) => frame ->
      values.map{ case(value, columns) =>
          value -> columns.map{case(column, cdat) =>
            column -> patients.map(p => cdat.getOrElse(p, "")).toArray
          }.toSeq.sortBy(_._1.event.stripPrefix("E").toInt).toArray
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
        val currentFile = new File(currentDir, fileName+".txt")
        val writer = new PrintWriter(currentFile)
        valueData.transpose.map(_.mkString("\t")).foreach(l => writer.println(l))
        writer.close()
      }
    }
  }
}
