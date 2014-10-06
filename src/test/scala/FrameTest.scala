import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by roman on 28/09/14.
 */
class FrameTest extends FlatSpec with Matchers {
  val data1 = Array.ofDim[String](5,5)
  val data2 = Array.ofDim[String](5,4)

  data1(0) = Array("A1_E1_C1", "A2_E1_C1",  "A3_E1_C1", "A4_E1_C1", "A6_E1_C1")
  data1(1) = Array(      "A1",         "",        "C1",       "D1",       "D1")
  data1(2) = Array(        "",       "B2",          "",       "D2",       "D2")
  data1(3) = Array(      "A3",      "B31",       "C32",       "D3",       "D3")
  data1(4) = Array(      "A4",         "",        "C4",       "D4",       "D4")

  data2(0) = Array("A2_E1_C2",  "A1_E1_C2", "A3_E1_C2",  "A5_E1_C2")
  data2(1) = Array(        "B1",        "",         "",        "E1")
  data2(2) = Array(      "",          "A2",       "C2",          "")
  data2(3) = Array(        "",          "",         "",          "")
  data2(4) = Array(        "B4",        "",         "",        "E4")

  val frame1 = new Frame("C1", data1)
  val frame2 = new Frame("C2", data2)

  "Frame" should "merge data from two frames into one" in {
    val merged = frame1 + frame2
    merged.data(0)(0) should be ("A1_E1_C2")
    merged.data(0)(5) should be ("A5_E1_C2")
  }
}
