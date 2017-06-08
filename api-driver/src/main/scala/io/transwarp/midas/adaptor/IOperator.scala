package io.transwarp.midas.adaptor

trait IOperator {
  def getMidasXML(hideDefault: Boolean): String
  def getMidasJson(): String
  def isRemote: Boolean
}
