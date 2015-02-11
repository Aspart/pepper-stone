package ru.biocad

/**
 * Created by roman on 25/09/14.
 */

import org.slf4j._

object main {
  def main(args: Array[String]): Unit = {
    parse(args)
  }

  /**
   * Parse command line arguments
   * @param args command line argument array
   */
  def parse(args: Array[String]) = {
    val parser = new scopt.OptionParser[OCConfig]("pepper-stone") {
      head("Pepper-stone merger for OpenClinica data", "0.2")
      opt[String]('i', "input") valueName "<folder>" required() action { (x, c) =>
        c.copy(in = x)
      } text "Input folder"
      opt[String]('o', "out") valueName "<file>" required() action { (x, c) =>
        c.copy(out = x)
      } text "Output folder"
      opt[String]('s', "sort") valueName "<file>" action { (x, c) =>
        c.copy(sort = x)
      } text "File to load patient order from"
      note("some notes.\n")
      help("help") text "prints this usage text"
    }
    parser.parse(args, OCConfig()) map { config =>
      process(config)
    } getOrElse {
      parser.showUsageAsError
    }
  }

  /**
   * Main function
   * @param config from command line
   * @return nothing
   */
  def process(config: OCConfig) = {
    val logger = LoggerFactory.getLogger("OCM.logger")
    logger.info(s"OCM Begin")
    logger.info(s"Start loading from ${config.in}")
    val ds = OCData(config.in)
    logger.info(s"Merging frames")
    val merged = ds.merge
    logger.info(s"Writing results to ${config.out}")
    merged.exportFrameValue(config.out, config.sort)
    logger.info(s"OCM Done")
  }
}
