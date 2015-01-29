package ru.biocad

import java.text.ParseException

/**
 * Created by rdegtiarev on 29/01/15.
 */

case class OCPatient(id: String, protocol: String) {
  def ==(ocPatient: OCPatient): Boolean = toString == ocPatient.toString
  override def toString = id
}

object OCPatient {
  def apply(arr: Array[String]): OCPatient = {
    if(arr.length != 2) {
      throw new ParseException("Wrong fields count", -1)
    } else {
      OCPatient(arr(0), arr(1))
    }
  }
}