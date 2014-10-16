package my.com

import java.io.{File, PrintWriter}

import scopt.OptionParser

import scala.io.Source
/**
 * Created by roman on 25/09/14.
 */

object main {
  def parse(args: Array[String]) = {
    val parser = new scopt.OptionParser[Config]("pepper-stone") {
      head("Pepper-stone merger for OpenClinica data", "0.1")
      opt[String]('i', "input") valueName "<file>" required() action { (x, c) =>
        c.copy(in = x) } text "Input file in tab-delimeted format"
      opt[String]('o', "out") valueName "<file>" required() action { (x, c) =>
        c.copy(out = x) } text "Output file in tab-delimeted format"
      opt[Unit]("header") action { (_, c) =>
        c.copy(header = true) } text "Keep header in output file"
      opt[Unit]("split") action { (_, c) =>
        c.copy(split = true) } text "Split into separate files by events"
      opt[Unit]("verbose") action { (_, c) =>
        c.copy(verbose = true) } text "Become verbose"
      opt[Unit]("debug") hidden() action { (_, c) =>
        c.copy(debug = true) } text "Debug option"
      note("some notes.\n")
      help("help") text "prints this usage text"
    }
    // parser.parse returns Option[C]
    parser.parse(args, Config()) map { config =>
      process(config)
    } getOrElse {
      // arguments are bad, error message will have been displayed
    }
  }

  def process(config: Config) = {
    val table = Source.fromFile(config.in).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
    val meta = getMeta(table)
    val data = getData(table)
    val dataset = new Dataset(meta, data)
    val merged = dataset.getMerged
    makeFile(config.out, merged.getRows.mkString("\n"))
  }

  def main(args: Array[String]): Unit = {
    parse(args)
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
