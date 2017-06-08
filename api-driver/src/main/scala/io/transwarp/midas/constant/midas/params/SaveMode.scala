package io.transwarp.midas.constant.midas.params


object SaveMode extends Enumeration {
  type SaveMode = Value
  val Append, Overwrite, Error, Ignore = Value
}
