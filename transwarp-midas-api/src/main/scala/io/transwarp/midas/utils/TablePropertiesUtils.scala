package io.transwarp.midas.utils
import java.sql.Connection
import java.util

object TablePropertiesUtils extends ITablePropertiesUtils {
  override def getMetaValue(values: String): Map[String, String] = null

  override def getMeta(connection: Connection,
                       database: String,
                       table: String): Map[String, Map[String, Map[String, String]]] = null

  override def getMetaJava(connection: Connection,
                           database: String,
                           table: String):
  util.Map[String, util.Map[String, util.Map[String, String]]] = null

  override def getTableDesc(connection: Connection,
                            database: String,
                            table: String): Array[Table] = {
    null
  }
}
