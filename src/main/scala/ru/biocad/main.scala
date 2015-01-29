package ru.biocad

/**
 * Created by roman on 25/09/14.
 */

object main {
  def main(args: Array[String]): Unit = {
    parse(args)
  }

  def parse(args: Array[String]) = {
    val parser = new scopt.OptionParser[OCConfig]("pepper-stone") {
      head("Pepper-stone merger for OpenClinica data", "0.2")
      opt[String]('i', "input") valueName "<folder>" required() action { (x, c) =>
        c.copy(in = x)
      } text "Input folder"
      opt[String]('o', "out") valueName "<file>" required() action { (x, c) =>
        c.copy(out = x)
      } text "Output file (or folder if -f specified)"
      note("some notes.\n")
      help("help") text "prints this usage text"
    }
    parser.parse(args, OCConfig()) map { config =>
      process(config)
    } getOrElse {
      parser.showUsageAsError
    }
  }

  def process(config: OCConfig) = {
    val ds = OCDataLoader.XLSLoadFromFolder(config.in)
    val merged = ds.merge

    val f = merged.ocTable.table.map(_._1.frame).toList.distinct
    val c = merged.ocMeta.frames.map(x => x.key -> x.description)
    merged.toString
    // ExportServant.exportEachFrameAtAllEvents(merged, config.out)
    // ExportServant.exportEachValueAtAllEvents(merged, config.out)
    // OCDataExporter.exportFramesOrdered(merged, config.out)
  }
}
