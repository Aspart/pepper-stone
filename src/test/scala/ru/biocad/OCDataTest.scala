package ru.biocad

import org.scalatest.{FlatSpec, Matchers}

/*
 * Created by roman on 28/09/14.
 */
class OCDataTest extends FlatSpec with Matchers {
  it should "concatenate data properly" in {
    val is = getClass.getResource("/testData.tsv").openStream()
    val ocData = OCData(is)
  }

  it should "merge data properly" in {
    val is = getClass.getResource("/testData.tsv").openStream()
    val ocData = OCData(is)
  }
}