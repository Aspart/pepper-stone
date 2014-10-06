package my.com

import java.text.ParseException

import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by roman on 28/09/14.
 */

class HeaderParserTest extends FlatSpec with Matchers {
  "Parser" should "parse headers in right manner" in {
    val header1 = new HeaderParser("BDATE_STRANGE_E8_C22_3")
    val header2 = new HeaderParser("BDATE_STRANGE_E8_C22")

    header1.variableName should be ("BDATE_STRANGE_3")
    header1.exam should be ("E8")
    header1.frame should be ("C22")

    header2.variableName should be ("BDATE_STRANGE")
    header2.exam should be ("E8")
    header2.frame should be ("C22")
  }

  it should "throw ParseException if wrong header present" in {
    a [ParseException] should be thrownBy {
      val parser = new HeaderParser("BDATE_STRAN!_GE__ffa8_C22_3")
    }
  }
}