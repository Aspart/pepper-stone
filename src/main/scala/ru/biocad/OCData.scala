package ru.biocad

/**
 * Created by roman on 28/09/14.
 */
/**
 * Container for meta table and data table
 * @param ocMeta container for events and frames
 * @param ocTable container for data
 */
case class OCData(ocMeta: OCMeta, ocTable: OCTable) {
  def +(that: OCData): OCData = OCData(ocMeta+that.ocMeta, ocTable+that.ocTable)

  def merge: OCData = {
    val newMeta = ocMeta.merge
    val newData = ocTable.merge(ocMeta)
    OCData(newMeta, newData)
  }
}