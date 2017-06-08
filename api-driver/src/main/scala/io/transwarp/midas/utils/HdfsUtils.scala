package io.transwarp.midas.utils

import java.sql.{Connection, PreparedStatement, SQLException, ResultSet}

import io.transwarp.midas.constant.InceptorDict

object HdfsUtils {
  def getTableProp(conn: Connection,
                   tableName: String,
                   databaseName: String): Map[String, String] = {
    val rs: ResultSet = null
    val ps: PreparedStatement = null
    try {
      val ps = conn.prepareStatement(
        s"SELECT * FROM system.tables_v WHERE database_name=? AND table_name=?")

      ps.setString(1, databaseName)
      ps.setString(2, tableName.toLowerCase)
      val rs = ps.executeQuery()

      if (rs.next()) {
        val meta = rs.getMetaData
        (1 to meta.getColumnCount)
          .map(index =>
            (meta.getColumnName(index), rs.getString(index)))
            .filter(kv => kv._2 != null && !kv._2.isEmpty).toMap
      } else {
        throw new SQLException(s"get meta failed for $databaseName $tableName")
      }
    } finally {
      if (ps != null) {
        ps.close()
      }
      if (rs != null) {
        rs.close()
      }
    }
  }

  def urlToDir(url: String): String = {
    val startIndex = url.indexOf("/", "hdfs://".length)
    url.substring(startIndex)
  }

  def getHDFSUrl(conn: Connection, tableName: String, databaseName: String): String = {
    getTableProp(conn, tableName, databaseName).getOrElse("table_location",
      throw new SQLException(s"get meta failed  table_location for $databaseName $tableName"))
  }

  def getFieldDelimiter(conn: Connection, tableName: String, databaseName: String): String = {
    getDelimiter(conn, tableName, databaseName, "field_delim", InceptorDict.Delimiter)
  }

  private def getDelimiter(conn: Connection, tableName: String, databaseName: String,
                           field: String, default: String): String = {
    val delimiter = getTableProp(conn, tableName, databaseName)
    if (delimiter == null || delimiter.isEmpty) {
      // default delimiter in Inceptor
      default
    } else {
      delimiter.getOrElse(field,
        throw new SQLException(s"get meta failed for $databaseName $tableName"))
    }
  }
}

