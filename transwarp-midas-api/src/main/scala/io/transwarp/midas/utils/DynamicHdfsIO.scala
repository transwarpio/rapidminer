package io.transwarp.midas.utils

import java.io.File

class DynamicHdfsIO(val path: Seq[File]) extends HdfsIO {
  override def writeRows(url: String,
                         rows: Iterator[Array[String]],
                         delimiter: String,
                         filename: String,
                         useKerberos: Boolean): Unit = {}

  override def login(principal: String, keytab: String): Unit = {}

  override def doWrite(url: String,
                       rows: Iterator[Array[String]],
                       delimiter: String, filename: String): Unit = {}
  def writeRows(url: String,
                rows: java.util.Iterator[Array[String]],
                delimiter: String,
                filename: String,
                userKerberos: Boolean): Unit = {}
}