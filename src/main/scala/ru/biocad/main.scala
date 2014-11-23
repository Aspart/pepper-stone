package my.com

import java.io.{File, PrintWriter}

import my.com.meta.{DatasetMeta, EventMeta, FrameMeta, DatasetMetaBuilder}
import ru.biocad.DatasetBuilder
import ru.biocad.meta.ColumnMeta
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
      parser.showUsageAsError
    }
  }

  def load(file: String): (Array[Array[String]], Array[Array[String]]) = {
    val table = Source.fromFile(file).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
    val header = getMeta(table)
    val data = getData(table)
    (header, data)
  }

  def mergeFrameMeta(meta: DatasetMeta): Array[EventMeta] = {
    val framesLib = meta.getFramesLib.map(x => x.description -> x).toMap
    // merge frames
    meta.events.map { event =>
      val trueFrames = event.frames.map { fr =>
        val template = framesLib(fr.description.split(" - ")(0))
        val trueValues = template.values.map(v => new ColumnMeta(v.value, event.key, template.key, v.version))
        new FrameMeta(template.name, template.description, fr.key, trueValues)
      }
      // create list of frames to remove and remove duplicates
      val framesToFilter = event.framesToMerge
      val filteredFrames = trueFrames.filter(fr => !framesToFilter.map(_.drop(1)).fold(Array.empty)(_ ++ _).contains(fr.key))
      new EventMeta(event.name, event.key, event.description, filteredFrames)
    }
  }

  def process(config: Config) = {
    val (header, data) = load(config.in)
    val metaBuilder = new DatasetMetaBuilder
    metaBuilder.parseHeader(header, data(0).drop(2).dropRight(1).map(_.trim))
    val meta = metaBuilder.getHeader

    // Okay
    val trueEvents = mergeFrameMeta(meta)

    val datasetBuilder = new DatasetBuilder
    datasetBuilder.setEvents(trueEvents)
    datasetBuilder.setData(data.transpose.drop(2).dropRight(1))

    val trueData = datasetBuilder.getMergedData(meta.columnsToMerge)
    println("result")
//    if(!config.split)
//      makeFile(config.out, merged.getRows.mkString("\n"))
//    else {
//      val eventsForFile = merged.data.map{case(eventName, eventData) => eventName ->
//        eventData.getRows.zipWithIndex.map{case(row, idx) => merged.patients(idx).mkString("\t") + "\t" + row}.mkString("\n")}
//      eventsForFile.foreach{case(eventName, eventRows) => makeFile(config.out.split('.').init :+ eventName :+ config.out.split('.').last mkString ".", eventRows)}
//    }
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
    writer.write(data)
    writer.close()
  }
}
