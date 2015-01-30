package ru.biocad

import java.io.{FileInputStream, BufferedInputStream, InputStream, File}

import scala.io.Source

/**
 * Created by roman on 26/11/14.
 */

/**
 * Load data from xls files to OCData
 */
object OCDataLoader {
  /**
   * Load OpenClinica files from folder
   * @param folder source folder with *.xls files from OpenClinica
   * @return OCData merged from all files from folder
   */
  def XLSLoadFromFolder(folder: String): OCData = {
    new File(folder).listFiles.filter(_.getName.endsWith(".xls")).map(x => XLSLoad(new BufferedInputStream(new FileInputStream(x)))).reduce(_ + _)
  }

  /**
   * Create OCTableMeta and OCTableData from source file
   * @param is stream with OpenClinica data
   * @return OCData from file specified
   */
  def XLSLoad(is: InputStream): OCData = {
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

    val table = tableLines.drop(1).map(_.dropRight(2)).transpose.zip(header).map{case(dat,col) =>
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
