package ru.biocad

import org.scalatest.{FlatSpec, Matchers}

/*
 * Created by roman on 28/09/14.
 */
class OCDataTest extends FlatSpec with Matchers {
  it should "load and export data properly" in {
    val ocData = OCData(getClass.getResourceAsStream("/E1.tsv"))
    val res = ocData.toString
  }

  it should "concatenate data properly" in {
    val ocData1 = OCData(getClass.getResourceAsStream("/E1.tsv"))
    val ocData2 = OCData(getClass.getResourceAsStream("/E2.tsv"))
    val result = ocData1 + ocData2
    result.toString
  }

  it should "merge data properly" in {
    val ocData1 = OCData(getClass.getResourceAsStream("/E1.tsv"))
    val ocData2 = OCData(getClass.getResourceAsStream("/E2.tsv"))
    val result = ocData1 + ocData2
    val merged = result.merge
  }
}