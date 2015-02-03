package ru.biocad

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
 * Created by roman on 28/09/14.
 */
class OCEventTest extends FlatSpec with Matchers {
<<<<<<< HEAD
  it should "concatenate two events" in {

  }

  it should "get frames to merge" in {

=======
  val path = getClass.getResource("/testData.tsv")
  val source = Source.fromURL(path).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
  val meta = source.filter(_.length <= 3)
  val data = source.filter(_.length > 3)

  it should "split data to frames" in {
>>>>>>> 9469e764633f746c2783a5a2ec5024cb597a9a07
  }
}