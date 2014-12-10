package ru.biocad.data

import my.com.meta.DatasetMeta

/**
 * Created by roman on 28/09/14.
 */
case class Dataset(meta: DatasetMeta, data: Array[Array[String]], patients: Array[Array[String]]) {
  def +(that: Dataset): Dataset = {
    val td1 = data.transpose.drop(1)
    val td2 = that.data.transpose.drop(1)
    val p1dataCols = data.length
    val p2dataCols = that.data.length
    val p1 = patients.transpose.drop(1).zip(td1).map { case (x, d) => x(0) -> d}.toMap
    val p2 = that.patients.transpose.drop(1).zip(td2).map { case (x, d) => x(0) -> d}.toMap


    val dataMerge = (p1.keys ++ p2.keys).map { p =>
      p -> (
      p1.getOrElse(p, Array.fill(p1dataCols) {
        ""
      }) ++ p2.getOrElse(p, Array.fill(p2dataCols) {
        ""
      }))
    }.toMap

    val protocolResult = patients.transpose.drop(1).map(x => x(0) -> x(1)).toMap ++ that.patients.transpose.drop(1).map(x => x(0) -> x(1)).toMap
    val patientsResult = (Array(patients.transpose.head) ++ (p1.keys ++ p2.keys).map(x => Array(x, protocolResult.get(x).get))).transpose

    val dataHeader = data.transpose.head ++ that.data.transpose.head

    val dataResult = (Array(dataHeader) ++ patientsResult.head.drop(1).map(p => dataMerge.get(p).get)).transpose

    new Dataset(meta + that.meta, dataResult, patientsResult)
  }
}