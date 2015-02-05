package ru.biocad

import java.io.{InputStream, BufferedInputStream, FileInputStream, File}

import scala.io.Source

/**
 * Created by roman on 28/09/14.
 */

/**
 * Container for meta table and data table
 * @param ocMeta container for events and frames
 * @param ocTable container for data
 */
case class OCData(ocMeta: OCMeta, ocTable: OCTable) {
  def +(that: OCData): OCData = OCData(ocMeta+that.ocMeta, ocTable+that.ocTable)

  def merge: OCData = {
    val newMeta = ocMeta.merge
    val newData = ocTable.merge(ocMeta)
    OCData(newMeta, newData)
  }

  override def toString: String = {
    List(ocMeta.toString,
    ocTable.toString).mkString("\n")
  }

  def exportFrameValue(outputDir: String): Unit = {
    ocTable.exportFrameValue(outputDir, ocMeta)
  }
//  def exportValues(ds: OCData, baseOutPath: String): Unit = {
//    val exportMap = OCDataExporter.magicSortHelper("/Users/roman/Projects/Biocad/clinical-sort")
//
//    val helperMap = (ds.patients ++ ds.data).transpose
//    val headerHelper = helperMap.head
//    val trueData = helperMap.drop(1).map(x => x.head -> x).toMap
//    val remapped = (Array(headerHelper) ++ exportMap.head._2._2.map(trueData.get(_).get)).transpose
//    val patients = remapped.take(2)
//    val data = remapped.drop(2)
//
//    val tmp = data.map(x => OCColumn(x(0).trim) -> x)
//      .groupBy(_._1.frame)
//      .map(x => x._1 -> x._2.groupBy(_._1.versionedName))
//
//    val nameHelper = ds.ocTableMeta.frames.map(f => f.key -> f.description).toMap
//
//    tmp.foreach { case (f, dat) =>
//      // attempt to create the directory here
//      val currentDir = new File(baseOutPath, f + " - " + nameHelper.getOrElse(f, f))
//      val success = currentDir.mkdir()
//      dat.foreach { case (v, cols) =>
//        val sortCols = cols.toSeq.sortBy(_._1.event.stripPrefix("E").toInt)
//        val currentFile = new File(currentDir, v + ".txt")
//        val writer = new PrintWriter(currentFile)
//        val rows = (patients ++ sortCols.map(x => Array(x._1.event) ++ x._2.drop(1))).transpose.map(_.mkString("\t"))
//        writer.write(rows.mkString("\n"))
//        writer.close()
//      }
//    }
//  }
//
//  def exportFrames(baseOutPath: String): Unit = {
//    val exportMap = .magicSortHelper("/Users/roman/Projects/Biocad/clinical-sort")
//
//    val helperMap = (ds.patients ++ ds.data).transpose
//    val headerHelper = helperMap.head
//    val trueData = helperMap.drop(1).map(x => x.head -> x).toMap
//    val remapped = (Array(headerHelper) ++ exportMap.head._2._2.map(trueData.get(_).get)).transpose
//    val patients = remapped.take(2)
//    val data = remapped.drop(2)
//
//    val tmp = data.map(x => OCColumn(x(0).trim) -> x)
//      .groupBy(_._1.frame)
//      .map { case (frame, frValues) =>
//      frame -> frValues.groupBy(_._1.event).map { case (ev, evValues) =>
//        ev -> evValues
//      }
//    }
//
//    val sorted = tmp.map { case (frame, data) =>
//      frame -> data.toSeq.sortBy(_._1.stripPrefix("E").toInt).map { case (ev, d) =>
//        ev -> d.toSeq.sortBy(_._1.value).map(_._2)
//      }
//    }
//
//    val nameHelper = ds.ocTableMeta.frames.map(f => f.key -> f.description).toMap
//
//    sorted.foreach { case (f, dat) =>
//      // attempt to create the directory here
//      val currentFile = new File(baseOutPath, f + " - " + nameHelper.getOrElse(f, f) + ".txt")
//      val writer = new PrintWriter(currentFile)
//      val dataRows = dat.map(_._2.transpose.map(_.toArray).toArray).toArray.transpose.map(_.reduce(_ ++ _))
//      val result = patients.transpose.zip(dataRows).map(x => x._1 ++ x._2)
//      writer.write(result.map(_.mkString("\t")).mkString("\n"))
//      writer.close()
//    }
//  }
//
//  def exportFramesOrdered(ds: OCData, baseOutPath: String): Unit = {
//    val exportMap = OCDataExporter.magicSortHelper("/Users/roman/Projects/Biocad/clinical-sort")
//
//    val helperMap = (ds.patients ++ ds.data).transpose
//    val headerHelper = helperMap.head
//    val trueData = helperMap.drop(1).map(x => x.head -> x).toMap
//    val remapped = (Array(headerHelper) ++ exportMap.head._2._2.map(trueData.get(_).get)).transpose
//    val patients = remapped.take(2)
//    val data = remapped.drop(2)
//
//    val tmp = data.map(x => OCColumn(x(0).trim) -> x)
//      .groupBy(_._1.frame)
//      .map(x => x._1 -> x._2.groupBy(_._1.versionedName))
//
//    val nameHelper = ds.ocTableMeta.frames.map(f => f.key -> f.description).toMap
//
//    tmp.foreach { case (f, dat) =>
//      val valuesExportMap = exportMap.get(nameHelper.get(f).get)
//      val currentFile = new File(baseOutPath, f + " - " + nameHelper.getOrElse(f, f) + ".txt")
//      val writer = new PrintWriter(currentFile)
//      val tmp = dat.map(_._2.toSeq.sortBy(x => eventIdToInt(x._1.event))).map(x => x.head._1 -> x).toSeq.sortBy(_._1.versionedName).map(_._2).map(_.map(_._2).transpose.map(_.toArray).toArray)
//      val tmp2 = dat.map(_._2.toSeq.sortBy(x => eventIdToInt(x._1.event)).map(_._2))
//
//      val tmpDataRows = dat.map(_._2.toSeq.sortBy(x => eventIdToInt(x._1.event))).map(x => x.head._1 -> x).toSeq.sortBy(_._1.versionedName).map(_._2).map(_.map(_._2).transpose.map(_.toArray).toArray).toArray.transpose.map(_.reduce(_ ++ _))
//      val dataRows = dat.map(_._2.toSeq.sortBy(x => eventIdToInt(x._1.event)).map(_._2).transpose.map(_.toArray).toArray).toArray.transpose.map(_.reduce(_ ++ _))
//      val result = patients.transpose.zip(tmpDataRows).map(x => x._1 ++ x._2)
//      writer.write(result.map(_.mkString("\t")).mkString("\n"))
//      writer.close()
//    }
//  }
//
//  private def magicSortHelper(sortDir: String): Map[String, (Array[String], Array[String])] = {
//    val these = new File(sortDir).listFiles
//    val files = these.filter(_.getName.endsWith(".txt")).map(_.getAbsolutePath)
//    val mappedResult = files.map(Source.fromFile(_).getLines().filter(!_.isEmpty).map(_.split("\t")).filter(_.length!=0).toArray) // -1 to parse even if no data in columns
//    val tmp = mappedResult.map{x =>
//        x(0).head -> (x(1).drop(1).map(_.toUpperCase), x.drop(2).map(_.head))
//      }.toMap
//    tmp
//  }
//
//  private def eventIdToInt(event: String): Int = {
//    event.stripPrefix("E").toInt
//  }
}

/**
 * Load data from xls files to OCData
 */
object OCData {
    /**
     * Load OpenClinica files from folder
     * @param folder source folder with *.xls files from OpenClinica
     * @return OCData merged from all files from folder
     */
    def apply(folder: String): OCData = {
      new File(folder).listFiles.filter(_.getName.endsWith(".xls")).map(x => apply(new BufferedInputStream(new FileInputStream(x)))).reduce(_ + _)
    }

    /**
     * Create OCTableMeta and OCTableData from source file
     * @param is stream with OpenClinica data
     * @return OCData from file specified
     */
    def apply(is: InputStream): OCData = {
      val lines = Source.fromInputStream(is).getLines().filter(!_.isEmpty).map(_.split("\t",-1)).toArray // -1 to parse even if no data in columns
      val metaLines = getMetaLines(lines)
      val tableLines = getTableLines(lines)

      val header = getHeader(tableLines)

      val meta = getMeta(metaLines, header)
      val table = getTable(tableLines, header)

      new OCData(meta, table)
    }

    private def getMeta(metaLines: Array[Array[String]], header: Array[OCColumn]): OCMeta = {
      OCMeta(metaLines, header)
    }

    private def getTable(tableLines: Array[Array[String]], header: Array[OCColumn]): OCTable = {
      val patients = tableLines.drop(1).map(p => OCPatient(p.take(2)))

      val table = tableLines.drop(1).map(_.drop(2)).transpose.zip(header).map{case(dat,col) =>
        col -> patients.zip(dat).map{case(p,d) =>
          p -> d
        }.toMap
      }.toMap
      OCTable(table)
    }

    private def getHeader(lines: Array[Array[String]]): Array[OCColumn] = {
      lines(0).drop(2).dropRight(1).map(_.trim).map(OCColumn(_))
    }

    private def getMetaLines(lines: Array[Array[String]]): Array[Array[String]] = {
      val metaLength = 3 // magic number: in TSV all metadata rows has length <= 3 cells
      lines.filter(_.length <= metaLength)
    }

    private def getTableLines(table: Array[Array[String]]): Array[Array[String]] = {
      val dataLength = 3 // magic number: in TSV all data rows has length > 3 cells
      table.filter(_.length > dataLength)
    }
}