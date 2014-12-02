package my.com

import ru.biocad.data.DatasetLoader
import ru.biocad.{ExportServant, MergeProcessor}

/**
 * Created by roman on 25/09/14.
 */

object main {
  def main(args: Array[String]): Unit = {
    parse(args)
  }

  def parse(args: Array[String]) = {
    val parser = new scopt.OptionParser[Config]("pepper-stone") {
      head("Pepper-stone merger for OpenClinica data", "0.2")
      opt[String]('i', "input") valueName "<file/folder>" required() action { (x, c) =>
        c.copy(in = x)
      } text "Input file/folder"
      opt[Unit]('f', "folder") action { (_, c) =>
        c.copy(folder = true)
      } text "Specify if input is folder"
      opt[String]('o', "out") valueName "<file>" required() action { (x, c) =>
        c.copy(out = x)
      } text "Output file (or folder if -f specified)"
      opt[Unit]("header") action { (_, c) =>
        c.copy(header = true)
      } text "Keep header in output file"
      opt[Unit]("verbose") action { (_, c) =>
        c.copy(verbose = true)
      } text "Become verbose"
      opt[Unit]("debug") hidden() action { (_, c) =>
        c.copy(debug = true)
      } text "Debug option"
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
    val ds = if (config.folder) DatasetLoader.XLSLoadFromFolder(config.in) else DatasetLoader.XLSLoad(config.in)
    val merged = MergeProcessor.merge(ds)

    ExportServant.exportEachFrameAtAllEvents(merged, config.out)
  }
}
