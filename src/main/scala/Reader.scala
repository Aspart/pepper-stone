/**
 * Created by roman on 25/09/14.
 */

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Reader {
  def mapValuesToTitle(headers: Array[String], values: Array[String]): scala.collection.mutable.HashMap[String, String] = {
    val mappedValues = new scala.collection.mutable.HashMap[String, String]()
    (headers zip values).map{ case (title, value) => mappedValues(title) = value}
    mappedValues
  }

  def load(inFileName: String, outFileName: String) : Array[Array[String]] = {
    val table = Source.fromFile(inFileName).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
    val meta = table.filter(_.length<=3)
    val data = table.filter(_.length>3)
    val dataset = new Dataset(data, meta)
    dataset.getMergedTable
    dataset.makeFile(outFileName)
    null
  }

  def getUniqueVizitArray(table: Array[Array[String]]): scala.collection.immutable.Map[String,Int] = {
    val exams = table(0).drop(2).map(x => new HeaderParser(x.trim).exam)
    exams.zipWithIndex.reverseIterator.map { case (element, index) => element -> index }.toMap
  }

  def getUniqueFrameArray(table: Array[Array[String]]): scala.collection.immutable.Map[String,Int] = {
    val exams = table(0).drop(2).map(x => new HeaderParser(x.trim).frame) // drop 2 head elements
    exams.zipWithIndex.reverseIterator.map { case (element, index) => element -> (index+2) }.toMap // add '2' to index
  }

  def getFrameArray(table: Array[Array[String]]): scala.collection.immutable.Map[String,Array[Int]] = {
    val exams = table(0).drop(2).map(x => new HeaderParser(x.trim).frame)
    exams.zipWithIndex.groupBy(_._1).map(tpl => tpl._1 -> tpl._2.map(_._2)).toMap
  }

  def getSameVizitIndexArray(exam: String, fields: Array[Array[String]]): Array[Int] = {
    var result = scala.collection.mutable.ArrayBuffer.empty[Int]
    for((x,i) <- fields(0).view.zipWithIndex) {
      if(new HeaderParser(x).exam==exam)
        result+=i
    }
    result.toArray
  }

  def getSameFrameIndexArray(frame: String, fields: Array[Array[String]]): Array[Int] = {
    var result = scala.collection.mutable.ArrayBuffer.empty[Int]
    for((x,i) <- fields(0).view.zipWithIndex) {
      if(new HeaderParser(x).frame==frame)
        result+=i
    }
    result.toArray
  }

  def getSameValuesInDifferentFrames(titles: Array[HeaderParser]) = {
    titles.map(x => titles.filter(_.isSameTitle(x)).map(_.toString()))
  }

}
