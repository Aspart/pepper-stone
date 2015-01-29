package my.com

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
 * Created by roman on 28/09/14.
 */
class DatasetTest extends FlatSpec with Matchers {
  val path = getClass.getResource("/testData.tsv")
  val source = Source.fromURL(path).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
  val meta = source.filter(_.length <= 3)
  val data = source.filter(_.length > 3)

//  val dataset = new Dataset(header, data)
//  it should "load data properly" in {
//    val rows = dataset.data("E2").data("C1").getRows
//    rows(1) should be ("c1")
//    rows(2) should be ("c2")
//    rows(3) should be ("c3")
//    rows(4) should be ("c4")
//    rows(5) should be ("c5")
//  }
//
//  it should "merge frames from one event by value name" in {
//    val merged = dataset.getMerged
//    val rows = merged.getRows
//
//    rows(0) should be ("ID\tSB\tA1_E1_C2\tA2_E1_C2\tA1_E2_C1")
//    rows(1) should be ("id1\tsb1\ta1\tb1\tc1")
//    rows(2) should be ("id2\tsb2\ta2\tb2\tc2")
//    rows(3) should be ("id3\tsb3\ta3\tb3\tc3")
//    rows(4) should be ("id4\tsb4\ta4\tb4\tc4")
//    rows(5) should be ("id5\tsb5\ta5\tb5\tc5")
//  }
}