package my.com

import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by roman on 28/09/14.
 */
class DatasetTest extends FlatSpec with Matchers {
  val src = Array.ofDim[String](5,6)
  val pat = Array.ofDim[String](5,2)

  pat(0) = Array("ID", "SB")
  pat(1) = Array("id1", "sb1")
  pat(2) = Array("id2", "sb2")
  pat(3) = Array("id3", "sb3")
  pat(4) = Array("id4", "sb4")

  src(0) = Array("A1_E1_C1",  "A1_E1_C2", "A1_E2_C1",  "A1_E2_C2", "A3_E2_C1", "A1_E3_C1")
  src(1) = Array("A1",                 "",         "",         "B1",        "C1",        "D1")
  src(2) = Array("",                 "A2",         "B2",         "",        "C2",        "D2")
  src(3) = Array("A3",                 "",         "B31",         "B32",        "C3",        "D3")
  src(4) = Array("A4",                 "",         "",         "B4",        "C4",        "D4")

  "my.com.Dataset" should "split data to exams" in {
    val exams = Map("E1" -> Array(0,1), "E2" -> Array(2, 3,4), "E3" -> Array(5))
    val dataset = new Dataset(pat, src)
    val res = dataset.getExamsArray(src)
    res("E1") should be (exams("E1"))
    res("E2") should be (exams("E2"))
    res("E3") should be (exams("E3"))
  }

  it should "split exam data to frames" in {
    val frames = Map("E1" -> Map("C1" -> Array(0), "C2" -> Array(1)), "E2" -> Map("C1" -> Array(2,4), "C2" -> Array(3)), "E3" -> Map("C1" -> Array(5)))
    val dataset = new Dataset(pat, src)
    val exams = dataset.getExamsArray(src)
    val res = dataset.getFramesArrayFromExamsArray(src, exams)
    res("E1")("C1") should be (frames("E1")("C1"))
    res("E1")("C2") should be (frames("E1")("C2"))
    res("E2")("C1") should be (frames("E2")("C1"))
    res("E2")("C2") should be (frames("E2")("C2"))
    res("E3")("C1") should be (frames("E3")("C1"))
  }

  it should "merge frames from one exam by value name" in {
    val dataset = new Dataset(pat, src)
    dataset.processMerge
    val merged = dataset.getMerged
  }
}