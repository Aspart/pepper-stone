package ru.biocad

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by rdegtiarev on 30/01/15.
 */
class OCDataLoaderTest extends FlatSpec with Matchers {
  "OCDataLoader" should "load OpenClinica data" in {
    val is = getClass.getResource("/testData.tsv").openStream()
    val ocData = OCDataLoader.XLSLoad(is)
    // TODO: add test here
  }
}
