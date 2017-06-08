package io.transwarp.midas.constant.rapidminer

/**
 * Created by liusheng on 5/23/16.
 */

import java.util

import io.transwarp.midas.constant.midas.ColumnTypes

object TypeMap {
  val map = new util.HashMap[String, String]()
  map.put("nominal", ColumnTypes.Nominal)
  map.put("text", ColumnTypes.Nominal)
  map.put("polynominal", ColumnTypes.Nominal)
  map.put("file_path", ColumnTypes.Nominal)

  map.put("numeric", ColumnTypes.Numeric)
  map.put("integer", ColumnTypes.Integer)
  map.put("real", ColumnTypes.Numeric)

  map.put("binominal", ColumnTypes.Binary)

  val reverseMap = new util.HashMap[String, String]()
  reverseMap.put(ColumnTypes.Binary, "binominal")
  reverseMap.put(ColumnTypes.Numeric, "numeric")
  reverseMap.put(ColumnTypes.Nominal, "polynominal")
  reverseMap.put(ColumnTypes.Integer, "integer")
}
