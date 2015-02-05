package ru.biocad

import java.text.ParseException

import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by roman on 28/09/14.
 */

class OCColumnTest extends FlatSpec with Matchers {
  "OCColumn" should "parse headers in right manner" in {
    val col1 = OCColumn("BDATE_STRANGE_E8_1_C22_3")
    col1.value should be ("BDATE_STRANGE")
    col1.event should be ("E8_1")
    col1.frame should be ("C22")
    col1.version should be (3)
    col1.withVer should be("BDATE_STRANGE_3")
    col1.withVerAndFrame should be ("BDATE_STRANGE_3_C22")
    col1.toString should be("BDATE_STRANGE_E8_1_C22_3")
    
    val col2 = OCColumn("BDATE_STRANGE_E8_C22_3")
    col2.value should be ("BDATE_STRANGE")
    col2.event should be ("E8")
    col2.frame should be ("C22")
    col2.version should be (3)
    col2.withVer should be("BDATE_STRANGE_3")
    col2.withVerAndFrame should be ("BDATE_STRANGE_3_C22")
    col2.toString should be("BDATE_STRANGE_E8_C22_3")
    
    val col3 = OCColumn("BDATE_STRANGE_E8_C22")
    col3.value should be ("BDATE_STRANGE")
    col3.event should be ("E8")
    col3.frame should be ("C22")
    col3.version should be (0)
    col3.withVer should be("BDATE_STRANGE")
    col3.withVerAndFrame should be ("BDATE_STRANGE_C22")
    col3.toString should be("BDATE_STRANGE_E8_C22")
  }

  it should "throw ParseException if wrong header present" in {
    a [ParseException] should be thrownBy {
      OCColumn("BDATE_STRAN!_GE__ffa8_C22_3")
    }
  }

  it should "compare columns properly" in {
    OCColumn("A1_E1_C1") should be (OCColumn("A1_E2_C1"))
  }

  it should "distinct columns" in {
    Array(OCColumn("A1_E1_C1"), OCColumn("A1_E2_C1"), OCColumn("A2_E1_C1")).distinct should be(Array(OCColumn("A1_E1_C1"), OCColumn("A2_E1_C1")))
  }
}