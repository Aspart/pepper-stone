package ru.biocad.data

import my.com.meta.DatasetMeta

/**
 * Created by roman on 28/09/14.
 */
case class Dataset(meta: DatasetMeta, data: Array[Array[String]], patients: Array[Array[String]])