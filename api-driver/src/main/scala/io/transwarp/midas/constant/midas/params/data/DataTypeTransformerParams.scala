package io.transwarp.midas.constant.midas.params.data

object DataTypeTransformerParams {
  val attributeName = "attributeName"
  val additionalAttributes = "additionalAttributes"
  val targetDataType = "targetDataType"
  val defaultTargetType = "defaultTargetDataType"

  val boolean = "boolean"
  val bigint = "bigint"
  val double = "double"
  val date = "date"
  val float = "float"
  val long = "long"
  val int = "int"
  val varchar = "varchar"
  val string = "string"
  val smallint = "smallint"
  val timestamp = "timestamp"
  val decimal = "decimal"
  val array = "array"
  val struct = "struct"
  val map = "map"
  val tinyint = "tinyint"

  val dataTypes = Array(
    string,
    boolean,
    int,
    long,
    float,
    double,
    date,
    timestamp,
    decimal,
    array,
    struct,
    map)
}
