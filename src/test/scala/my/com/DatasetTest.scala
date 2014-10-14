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

  val dataset = new Dataset(meta, data)

  it should "merge frames from one event by value name" in {
    val merged = dataset.getMerged
    val table = merged.getRows

    table(0) should be ("ID\tSB\tA1_E1_C2\tA2_E1_C2\tA1_E2_C1")
    table(1) should be ("id1\tsb1\ta1\tb1\tc1")
  }
}