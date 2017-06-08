package io.transwarp.midas.error

class MidasError(val code: ErrorCode, msg: String, val source: String) extends Exception {
  override def getMessage: String = {
    if (source != null) {
      s"Error-${code} ${source}: ${msg}"
    } else {
      s"Error-${code}: ${msg}"
    }
  }

  def this(code: ErrorCode, msg: String, source: String, cause: Throwable) = {
    this(code, msg, source)
    initCause(cause)
  }
}

object MidasError {
  def apply(code: ErrorCode, msg: String, source: String): MidasError = {
    new MidasError(code, msg, source)
  }

  def apply(code: ErrorCode, msg: String, source: String, cause: Throwable): MidasError = {
    new MidasError(code, msg, source, cause)
  }
}
