package ru.biocad

import java.io.File

import ru.biocad.data.DatasetLoader

/**
 * Created by roman on 26/11/14.
 */
object UniverseSaver {
  def main(args: Array[String]) = {
    val path = args(0)
    val rec = getFilesToOpen(path)
    val result = rec.map(x => getFrameNames(x)).reduce(_ ++ _)
    val m = scala.collection.mutable.Map[String, Array[String]]()
    result.foreach { x =>
      m(x._1) = m.getOrElse(x._1, Array[String]()) ++ x._2
    }
    m.foreach { case (ev, fr) =>
      println(ev)
      fr.foreach(println)
    }
  }

  def getFilesToOpen(base: String): Array[String] = {
    val these = new File(base).listFiles
    these.filter(_.getName.endsWith(".xls")).map(_.getAbsolutePath)
  }

  def getFrameNames(path: String) = {
    val ds = DatasetLoader.XLSLoad(path)
    val result = ds.meta.events.map(ev => ev.toString -> ev.frames.map(_.toString))
    result
  }
}
