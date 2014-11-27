package my.com

import my.com.meta.DatasetMetaBuilder
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
 * Created by roman on 28/09/14.
 */
class FrameTest extends FlatSpec with Matchers {
  val path = getClass.getResource("/testData.tsv")
  val source = Source.fromURL(path).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
  val meta = source.filter(_.length <= 3)
  val data = source.filter(_.length > 3)

  val headerBuilder = new DatasetMetaBuilder
  headerBuilder.parseHeader(meta, data(0))
  val header = headerBuilder.build
//  val dataset = new Dataset(header, data)

//  it should "split data by frames" in {
//    val frame1 = dataset.data("E1").data("C1").getRows
//    val frame2 = dataset.data("E1").data("C2").getRows
//
//    frame1(1) should be ("a1\tb1")
//    frame1(2) should be ("a2\tb2")
//    frame1(3) should be ("\t")
//    frame1(4) should be ("a4\tb4")
//    frame1(5) should be ("\t")
//
//    frame2(1) should be ("\t")
//    frame2(2) should be ("\t")
//    frame2(3) should be ("a3\tb3")
//    frame2(4) should be ("\t")
//    frame2(5) should be ("a5\tb5")
//  }
//
//  it should "merge data from two frames into one" in {
//    val merged = dataset.data("E1").data("C1") + dataset.data("E1").data("C2")
//    val rows = merged.getRows
//    rows(1) should be ("a1\tb1")
//    rows(2) should be ("a2\tb2")
//    rows(3) should be ("a3\tb3")
//    rows(4) should be ("a4\tb4")
//    rows(5) should be ("a5\tb5")
//  }
}
