package io.transwarp.midas.error

/**
  * every module in midas should extend this trait and provide a prefix,
  * then all the exception made in the module should be created through it.
  * see example from [[MidasErrors]] in midas-core
  */
trait Errors {
  val prefix: String
  def entry(code: Int): ErrorCode = new ErrorCode(prefix, code)
}
