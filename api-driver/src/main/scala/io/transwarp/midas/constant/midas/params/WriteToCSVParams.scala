package io.transwarp.midas.constant.midas.params

object WriteToCSVParams {
  val file = "file"
  val mode = "mode"

  val sep = "sep"
  val quote = "quote"
  val escape = "escape"
  val escapeQuotes = "escapeQuotes"
  val quoteAll = "quoteAll"
  val header = "header"
  val nullValue = "nullValue"
  val compression = "compression"
  val dateFormat = "dateFormat"
  val timestampFormat = "timestampFormat"

  val defaultMode = SaveMode.Overwrite.toString
  val defaultSep = ","
  val defaultQuote = "\""
  val defaultEscape = "\\"
  val defaultEscapeQuote = true
  val defaultQuoteAll = false
  val defaultHeader = false
  val defaultNullValue = ""
  val defaultCompression = Compression.none.toString
  val defaultDateFormat = "yyyy-MM-dd"
  val defaultTimestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
}
