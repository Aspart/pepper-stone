package ru.biocad

import org.scalatest.{Matchers, FlatSpec}

import scala.io.Source

/**
 * Created by rdegtiarev on 02/02/15.
 */
class OCMetaTest extends FlatSpec with Matchers {    val is = getClass.getResource("/E1.tsv").openStream()
  val lines = Source.fromInputStream(is).getLines().filter(!_.isEmpty).map(_.split("\t",-1)).toArray // -1 to parse even if no data in columns)
  val header = lines(11).drop(2).map(OCColumn(_))

  val ocMeta = OCMeta(lines.filter(_.length <= 3), header)

  it should "load meta from parsed lines" in {
    val vals1 = Array("A1_E1_C1", "A2_E1_C1").map(OCColumn(_))
    val vals2 = Array("A1_E1_C2", "A3_E1_C2").map(OCColumn(_))

    val frames1 = Array(
      OCFrame("CRF2",	"01_Frame number one", "C2", vals2),
      OCFrame("CRF1", "01_Frame number one", "C1", vals1)
    )

    ocMeta.events.map(_.frames).reduce(_ ++ _).distinct should be (frames1)
  }

  it should "merge meta" in {
    val vals = Array("A1_E1_C2", "A3_E1_C2", "A2_E1_C2").map(OCColumn(_))
    val frame = Array(
      OCFrame("CRF2",	"01_Frame number one", "C2", vals)
    )

    val mergedMeta = ocMeta.merge
    mergedMeta.events.map(_.frames).reduce(_ ++ _).distinct should be (frame)
    mergedMeta.events.map(_.frames).reduce(_ ++ _).distinct.head.columns should be (vals)
  }

  it should "concatenate two meta" in {
    // TODO
  }

  it should "get framesLib" in {
    val vals = Array("A1_E1_C2", "A3_E1_C2", "A2_E1_C2").map(OCColumn(_))
    val frame = Array(
      OCFrame("CRF2",	"01_Frame number one", "C2", vals)
    )
    ocMeta.getFramesLib should be (frame)
  }
}
