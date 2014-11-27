package ru.biocad

import java.io.{File, PrintWriter}

import ru.biocad.meta.ColumnParser

/**
 * Created by roman on 23/11/14.
 */
object ExportServant {
  def exportEachValueAtAllEvents(data: Array[Array[String]], baseOutPath: String): Unit = {
    val patients = data.take(2)
    val tmp = data.drop(2).map(x => ColumnParser.parse(x(0).trim) -> x)
      .groupBy(_._1.frame)
      .map(x => x._1 -> x._2.groupBy(_._1.versionedName))

    tmp.foreach{ case (f, dat) =>
      // attempt to create the directory here
      val currentDir = new File(baseOutPath, f)
      val success = currentDir.mkdir()
      dat.foreach{ case(v, cols) =>
        val currentFile = new File(currentDir, v+".txt")
        val writer = new PrintWriter(currentFile)
        val rows = (patients ++ cols.map(x => Array(x._1.event) ++ x._2.drop(1))).transpose.map(_.mkString("\t"))
        writer.write(rows.mkString("\n"))
        writer.close()
      }
    }
  }
}
