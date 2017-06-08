package io.transwarp.midas.error

class ErrorCode(val prefix: String, val code: Int) {
  override def toString: String = {
    s"${prefix}${code}"
  }
}


