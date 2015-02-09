package ru.biocad

import org.scalatest.{FlatSpec, Matchers}

/*
 * Created by roman on 28/09/14.
 */
class OCDataTest extends FlatSpec with Matchers {
  it should "load and export data properly" in {
    val ocData = OCData(getClass.getResourceAsStream("/E1.tsv"))
    ocData.toString should be ("Study Event Definitions\t1\nStudy Event Definition 1\tExam 1\tE1\nCRF2\t01_Frame number one\tC2\nCRF1\t01_Frame number one\tC1\nStudy Subject ID\tA1_E1_C1\tA2_E1_C1\tA1_E1_C2\tA3_E1_C2\nid1\tv1\tv2\t\t\nid2\t\t\tv1\tv3\nid3\tv1\tv2\t\t")
  }

  it should "concatenate data properly" in {
    val ocData1 = OCData(getClass.getResourceAsStream("/E1.tsv"))
    val ocData2 = OCData(getClass.getResourceAsStream("/E2.tsv"))
    val result = ocData1 + ocData2
    result.toString should be ("Study Event Definitions\t2\nStudy Event Definition 2\tExam 2\tE2\nCRF3\t01_Frame number two\tC3\nCRF1\t01_Frame number one\tC1\nStudy Event Definition 1\tExam 1\tE1\nCRF2\t01_Frame number one\tC2\nCRF1\t01_Frame number one\tC1\nStudy Subject ID\tA2_E2_C1\tA1_E1_C1\tA1_E2_C1\tA1_E2_C3\tA1_E1_C2\tA2_E1_C1\tA3_E1_C2\nid1\tv2\tv1\tv1\t\t\tv2\t\nid2\t\t\t\tv1\tv1\t\tv3\nid3\t\tv1\t\t\t\tv2\t")
  }

  it should "merge data properly" in {
    val ocData1 = OCData(getClass.getResourceAsStream("/E1.tsv"))
    val ocData2 = OCData(getClass.getResourceAsStream("/E2.tsv"))
    val result = ocData1 + ocData2
    val merged = result.merge
    merged.toString should be ("Study Event Definitions\t2\nStudy Event Definition 2\tE2\tExam 2\nCRF3\t01_Frame number two\tC3\nCRF1\t01_Frame number one\tC1\nStudy Event Definition 1\tE1\tExam 1\nCRF1\t01_Frame number one\tC1\nStudy Subject ID\tA2_E2_C1\tA1_E1_C1\tA1_E2_C1\tA1_E2_C3\tA2_E1_C1\tA3_E1_C1\nid1\tv2\tv1\tv1\t\tv2\t\nid2\t\tv1\t\tv1\t\tv3\nid3\t\tv1\t\t\tv2\t")
  }
}