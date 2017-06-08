package io.transwarp.midas.constant.midas.params

object Compression extends Enumeration {
  type Compression = Value
  val none, snappy, gzip, lzo, zlib, bzip2, lz4, deflate = Value
}
