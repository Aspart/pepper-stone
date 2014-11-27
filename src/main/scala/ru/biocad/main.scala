package my.com

import java.io.{File, PrintWriter}

import my.com.meta.{DatasetMeta, EventMeta, FrameMeta}
import ru.biocad.{MergeProcessor, ExportServant}
import ru.biocad.data.{DatasetBuilder, DatasetLoader}
import ru.biocad.meta.{ColumnMeta, ColumnParser}
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

  def process(config: Config) = {
    val ds = DatasetLoader.XLSLoad(config.in)
    val merged = MergeProcessor.merge(ds)

    ExportServant.exportEachValueAtAllEvents(merged.patients ++ merged.data, config.out)
  }

  def main(args: Array[String]): Unit = {
    parse(args)
  }
}
