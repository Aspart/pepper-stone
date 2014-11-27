package ru.biocad.data

import my.com.meta.DatasetMetaBuilder

import scala.io.Source

/**
 * Created by roman on 26/11/14.
 */
object DatasetLoader {
  def XLSLoad(file: String) = {
    val (header, data, patients) = loadFile(file)
    val metaBuilder = new DatasetMetaBuilder
    metaBuilder.parseHeader(header, data.map(_.head))
    new Dataset(metaBuilder.build, data, patients)
  }

  private def loadFile(file: String): (Array[Array[String]], Array[Array[String]], Array[Array[String]]) = {
    val table = Source.fromFile(file).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray // -1 to parse even if no data in columns
    val header = getMeta(table)
    val data = getData(table).transpose.dropRight(1).map(_.map(_.trim)) // dropRight as -1 in split ^
    val patients = data.take(2)
    (header, data.drop(2), patients)
  }

  private def getMeta(table: Array[Array[String]]): Array[Array[String]] = {
    val metaLength = 3 // magic number: in TSV all metadata rows has length <= 3 cells
    table.filter(_.length <= metaLength)
  }

  private def getData(table: Array[Array[String]]): Array[Array[String]] = {
    val dataLength = 3 // magic number: in TSV all data rows has length > 3 cells
    table.filter(_.length > dataLength)
  }
}
