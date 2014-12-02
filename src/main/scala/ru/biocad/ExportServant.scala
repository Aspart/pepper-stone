package ru.biocad

import java.io.{File, PrintWriter}

import ru.biocad.data.Dataset
import ru.biocad.meta.ColumnParser

/**
 * Created by roman on 23/11/14.
 */
object ExportServant {
  def exportEachValueAtAllEvents(ds: Dataset, baseOutPath: String): Unit = {
    val patients = ds.patients
    val tmp = ds.data.map(x => ColumnParser.parse(x(0).trim) -> x)
      .groupBy(_._1.frame)
      .map(x => x._1 -> x._2.groupBy(_._1.versionedName))

    val nameHelper = ds.meta.frames.map(f => f.key -> (f.key + " - " + f.description)).toMap

    tmp.foreach { case (f, dat) =>
      // attempt to create the directory here
      val currentDir = new File(baseOutPath, nameHelper.getOrElse(f, f))
      val success = currentDir.mkdir()
      dat.foreach { case (v, cols) =>
        val currentFile = new File(currentDir, v + ".txt")
        val writer = new PrintWriter(currentFile)
        val rows = (patients ++ cols.map(x => Array(x._1.event) ++ x._2.drop(1))).transpose.map(_.mkString("\t"))
        writer.write(rows.mkString("\n"))
        writer.close()
      }
    }
  }

  def exportEachFrameAtAllEvents(ds: Dataset, baseOutPath: String): Unit = {
    val patients = ds.patients
    val tmp = ds.data.map(x => ColumnParser.parse(x(0).trim) -> x)
      .groupBy(_._1.frame)
      .map { case (frame, frValues) =>
      frame -> frValues.groupBy(_._1.event).map { case (ev, evValues) =>
        ev -> evValues
      }
    }

    val sorted = tmp.map { case (frame, data) =>
      frame -> data.toSeq.sortBy(_._1.stripPrefix("E").toInt).map { case (ev, d) =>
        ev -> d.toSeq.sortBy(_._1.value).map(_._2)
      }
    }

    val nameHelper = ds.meta.frames.map(f => f.key -> (f.key + " - " + f.description)).toMap

    sorted.foreach { case (f, dat) =>
      // attempt to create the directory here
      val currentFile = new File(baseOutPath, nameHelper.getOrElse(f, f) + ".txt")
      val writer = new PrintWriter(currentFile)
      val dataRows = dat.map(_._2.transpose.map(_.toArray).toArray).toArray.transpose.map(_.reduce(_ ++ _))
      val result = patients.transpose.zip(dataRows).map(x => x._1 ++ x._2)
      writer.write(result.map(_.mkString("\t")).mkString("\n"))
      writer.close()
    }
  }
}
