package my.com

import java.io.{File, PrintWriter}

import scala.io.Source

/**
 * Created by roman on 25/09/14.
 */

object main {
  def main(args: Array[String]): Unit = {
    val table = Source.fromFile(args(0)).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
    val meta = getMeta(table)
    val data = getData(table)
    val dataset = new Dataset(data, meta)
    dataset.getMerged
    makeFile(args(1), dataset.toString)
  }

  def getMeta(table: Array[Array[String]]): Array[Array[String]] = {
    val metaLength = 3 // magic number: in TSV all metadata rows has length <= 3 cells
    table.filter(_.length <= metaLength)
  }

  def getData(table: Array[Array[String]]): Array[Array[String]] = {
    val dataLength = 3 // magic number: in TSV all data rows has length > 3 cells
    table.filter(_.length > dataLength)
  }

  def makeFile(fileName: String, data: String) = {
    val writer = new PrintWriter(new File(fileName))
    val res = this.toString
    writer.write(res)
    writer.close()
  }
}
