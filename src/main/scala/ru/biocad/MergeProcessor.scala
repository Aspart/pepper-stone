package ru.biocad

import my.com.meta.{DatasetMetaBuilder, FrameMeta, EventMeta, DatasetMeta}
import ru.biocad.data.{Dataset, DatasetBuilder}
import ru.biocad.meta.{ColumnParser, ColumnMeta}

/**
 * Created by roman on 26/11/14.
 */
object MergeProcessor {
  def merge(ds: Dataset): Dataset = {
    val newEvents = mergeFrameMeta(ds.meta)
    val newData = getMergedData(ds.meta, ds.data)
    val newMeta = new DatasetMetaBuilder(ds.meta)
    val newDataset = new DatasetBuilder(ds)
    newMeta.setEvents(newEvents)
    newDataset.setData(newData)
    newDataset.setMeta(newMeta)
    newDataset.build
  }

  private def getMergedData(meta: DatasetMeta, data: Array[Array[String]]) = {
    val colToMerge = frameRemap(meta)
    val result = scala.collection.mutable.Map[String, Array[String]]()
    val patCount = data.head.size
    data.foreach { x =>
      val cid = ColumnParser.parse(x(0).trim)
      val newFrame = colToMerge.getOrElse(cid.frame, cid.frame)
      val newCid = new ColumnMeta(cid.value, cid.event, newFrame, cid.version)
      val inres = result.getOrElse(newCid.toString, Array.fill(patCount) {""})
      result(newCid.toString) = inres.zip(x).map {
        case (od, nd) =>
          if (od.isEmpty)
            nd
          else
            od
      }
    }
    result.map{case(h, d) =>
      d(0) = h.toString
      d
    }
    result.values.toArray
  }

  private def mergeFrameMeta(meta: DatasetMeta): Array[EventMeta] = {
    val framesLib = meta.getFramesLib.map(x => x.description -> x).toMap
    val remap = scala.collection.mutable.Map[String, String]()
    // merge frames
    val result = meta.events.map { event =>
      val trueFrames = event.frames.map { fr =>
        val template = framesLib(fr.description.split(" - ")(0))
        val trueValues = template.values.map(v => new ColumnMeta(v.value, event.key, template.key, v.version))
        remap(fr.key) = template.key
        new FrameMeta(template.name, template.description, template.key, trueValues)
      }
      // create list of frames to remove and remove duplicates
      val framesToFilter = event.framesToMerge
      val filteredFrames = trueFrames.filter(fr => !framesToFilter.map(_.drop(1)).fold(Array.empty)(_ ++ _).contains(fr.key))
      new EventMeta(event.name, event.key, event.description, filteredFrames)
    }
    result
  }

  private def frameRemap(meta: DatasetMeta): Map[String, String] = {
    val framesLib = meta.getFramesLib.map(x => x.description -> x).toMap
    // merge frames
    meta.events.flatMap(_.frames.map( fr => fr.key -> framesLib(fr.description.split(" - ")(0)).key)).toMap
  }

}
