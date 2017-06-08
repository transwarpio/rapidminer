package io.transwarp.midas.utils

object MidasJson {
  def toJson(value: Any, pretty: Boolean = false): String = ""
  def fromJson[T](json: String, clazz: Class[T]): T = null.asInstanceOf[T]
}
