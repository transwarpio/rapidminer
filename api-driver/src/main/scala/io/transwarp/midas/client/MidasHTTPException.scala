package io.transwarp.midas.client

/**
  * Created by xiao on 16-11-25.
  */
class MidasHTTPException(val statusCode: Int, statusMessage: String)
  extends Exception(statusMessage) {
  override def getMessage: String = {
    s"${statusCode}: ${statusMessage}"
  }
}
