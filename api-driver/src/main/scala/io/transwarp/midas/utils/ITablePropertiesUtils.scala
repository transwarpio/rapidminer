package io.transwarp.midas.utils

import java.sql.Connection
import java.util.{Map => JMap}

import scala.collection.mutable
import scala.collection.JavaConverters._

case class Column(name: String, dataType: Int, typeName: String)
case class TableMeta(map: Map[String, Map[String, String]])
case class Table(db: String, name: String, columns: Array[Column], meta: TableMeta)

trait ITablePropertiesUtils {
  val PropertyRole = "role"
  val PropertyType = "type"

  val properties = Array(PropertyRole, PropertyType)
  def getTableDesc(connection: Connection, database: String, table: String): Array[Table]
  def getMetaValue(values: String): Map[String, String]
  def getMeta(connection: Connection,
              database: String,
              table: String): Map[String, Map[String, Map[String, String]]]

  def getMetaJava(connection: Connection,
                  database: String,
                  table: String): JMap[String, JMap[String, JMap[String, String]]]
}
