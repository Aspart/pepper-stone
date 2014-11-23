package my.com

import java.text.ParseException

import org.scalatest.{FlatSpec, Matchers}
import ru.biocad.meta.ColumnParser

/**
 * Created by roman on 28/09/14.
 */

class ColumnParserTest extends FlatSpec with Matchers {
  "Parser" should "parse headers in right manner" in {
    val header1 = ColumnParser.parse("BDATE_STRANGE_E8_C22_3")
    val header2 = ColumnParser.parse("BDATE_STRANGE_E8_C22")

    header1.value should be ("BDATE_STRANGE_3")
    header1.event should be ("E8")
    header1.frame should be ("C22")

    header2.value should be ("BDATE_STRANGE")
    header2.event should be ("E8")
    header2.frame should be ("C22")
  }

  it should "throw ParseException if wrong header present" in {
    a [ParseException] should be thrownBy {
      val parser = ColumnParser.parse("BDATE_STRAN!_GE__ffa8_C22_3")
    }
  }
}