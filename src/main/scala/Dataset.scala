import scala.collection.mutable.ArrayBuffer

/**
 * Created by roman on 28/09/14.
 */
class Dataset(val table: Array[Array[String]], val metaData: Array[Array[String]]) {
  val patients = table.map(x => Array(x(0), x(1))) // create patient arrray from two first columns
  val data = table.map(_.drop(2).dropRight(1))     // drop last column as it is always empty (-1 option in .split)
  var merged : Option[Array[Array[String]]] = None

  def getFramesToMerge(metaData: Array[Array[String]]): Map[String, Array[Array[String]]] = {
    val examFrames = metaData.zipWithIndex.map{ case (field, index) =>
      if(field(0).startsWith("Study Event Definition ")) { // begin of exam section
        var a = index+1
        val buf = new ArrayBuffer[Array[String]]
        while(a != metaData.length && !metaData(a)(0).startsWith("Study Event Definition")) { // parse all frames
          buf += metaData(a)
          a += 1
        }
        field(2) -> buf.toArray
      } else {
        null
      }
    }.filter(_ != null)
    val result = examFrames.map { case (exam, frames) =>
      exam -> frames.map { frame =>
        val valName = frame(1).split(" - ")(0)
        val frameNames = frames.map(x => x(1).split(" - ")(0) -> x(2))
        frameNames.filter(_._1 == valName).map(_._2).toList
      }.filter(_.size > 1).distinct.map(_.toArray)
    }.filter(_._2.nonEmpty).toMap
    result
  }

  def getExamsArray(data: Array[Array[String]]): scala.collection.immutable.Map[String,Array[Int]] = {
    val exams = data(0).map(x => new HeaderParser(x.trim).exam)
    exams.zipWithIndex.groupBy(_._1).map(tpl => tpl._1 -> tpl._2.map(_._2)).toMap
  }

  def getFramesArrayFromExamsArray(data: Array[Array[String]], exams: Map[String, Array[Int]]):
  scala.collection.immutable.Map[String, scala.collection.immutable.Map[String, Array[Int]]] = {
    exams.map(x => x._1 -> x._2.map{index => data(0)(index) -> index}.map{x => new HeaderParser(x._1.trim).frame -> x._2}.groupBy(_._1).map(tpl => tpl._1 -> tpl._2.map(_._2)).toMap)
  }

  def processMerge = {
    val framesToMerge = getFramesToMerge(metaData)
    val exams = getExamsArray(data)
    val examsWithFrames = getFramesArrayFromExamsArray(data, exams)
    val arrayOfExams = examsWithFrames.map(x => new Exam(data, x._1, x._2))
    val tst = arrayOfExams.map(exam =>
      exam.getMergedData(framesToMerge.getOrElse(exam.exam, null))
    )
    val buf = Array.fill(tst.head.size){new ArrayBuffer[String]}
    tst.foreach(x => x.zipWithIndex.foreach { case (str, idx) =>
      buf(idx) ++= str
    })
    merged = Option(buf.map(_.toArray))
  }

  def getMerged = merged

  override def toString = {
    val outstr = merged.get.zipWithIndex.map{ case(x, idx) => patients(idx).mkString("\t") + x.mkString("\t")}.mkString("\n")
    outstr
  }
}
