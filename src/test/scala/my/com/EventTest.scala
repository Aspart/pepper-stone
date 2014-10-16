package my.com

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
 * Created by roman on 28/09/14.
 */
class EventTest extends FlatSpec with Matchers {
  val path = getClass.getResource("/testData.tsv")
  val source = Source.fromURL(path).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
  val meta = source.filter(_.length <= 3)
  val data = source.filter(_.length > 3)

  it should "split data to frames" in {
  }
}