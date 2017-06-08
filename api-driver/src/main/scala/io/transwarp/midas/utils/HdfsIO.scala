package io.transwarp.midas.utils

trait HdfsIO {
  def writeRows(url: String,
                rows: Iterator[Array[String]],
                delimiter: String, filename: String, useKerberos: Boolean): Unit
  def login(principal: String, keytab: String): Unit
  def doWrite(url: String,
              rows: Iterator[Array[String]],
              delimiter: String, filename: String): Unit

  protected def mkString(row: Array[String], delimiter: String): String = {
    val r = row.map(r => if (r == null) "" else r)
    r.mkString(delimiter)
  }
}
