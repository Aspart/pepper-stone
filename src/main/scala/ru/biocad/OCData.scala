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
    List(ocMeta.toString, ocTable.toString).mkString("\n")
  }

  def exportFrameValue(outputDir: String, patientOrder: String = ""): Unit = {
    ocTable.exportFrameValue(outputDir, ocMeta, patientOrder)
  }

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
        col -> patients.zip(dat).map{case(p,d) => p -> d}.toMap
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