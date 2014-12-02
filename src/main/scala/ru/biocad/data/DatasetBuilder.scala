package ru.biocad.data

import my.com.meta.DatasetMetaBuilder

/**
 * Created by roman on 17/11/14.
 */
class DatasetBuilder {
  var meta = new DatasetMetaBuilder
  var data = Array[Array[String]]()
  var patients = Array[Array[String]]()

  def this(ds: Dataset) {
    this()
    this.setData(ds.data)
    this.setMeta(new DatasetMetaBuilder(ds.meta))
    this.setPatients(ds.patients)
  }

  def setMeta(meta: DatasetMetaBuilder) = this.meta = meta
  def setData(data: Array[Array[String]]) = this.data = data
  def setPatients(patients: Array[Array[String]]) = this.patients = patients

  def build = new Dataset(meta.build, data, patients)
}
