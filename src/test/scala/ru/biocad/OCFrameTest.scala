package ru.biocad

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
 * Created by roman on 28/09/14.
 */
class OCFrameTest extends FlatSpec with Matchers {
  it should "concatenate two frames" in {
    val cols1 = Array("A1_E1_C1", "A2_E1_C1").map(OCColumn(_))
    val cols2 = Array("A1_E1_C2", "A3_E1_C2").map(OCColumn(_))
    val fr1 = OCFrame("CRF1", "My super frame", "C1", cols1)
    val fr2 = OCFrame("CRF2", "My super frame", "C2", cols2)

    val fr = fr1 + fr2

    fr.name should be ("CRF1")
    fr.description should be ("My super frame")
    fr.key should be ("C1")

    val cols = Array("A1_E1_C1", "A2_E1_C1", "A3_E1_C1").map(OCColumn(_))
    fr.columns should be (cols)
  }
}
