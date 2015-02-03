package ru.biocad

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
 * Created by roman on 28/09/14.
 */
class OCFrameTest extends FlatSpec with Matchers {
<<<<<<< HEAD
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
=======
  val path = getClass.getResource("/testData.tsv")
  val source = Source.fromURL(path).getLines().filter(!_.isEmpty).map(_.split("\t", -1)).toArray
  val meta = source.filter(_.length <= 3)
  val data = source.filter(_.length > 3)

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
>>>>>>> 9469e764633f746c2783a5a2ec5024cb597a9a07
}
