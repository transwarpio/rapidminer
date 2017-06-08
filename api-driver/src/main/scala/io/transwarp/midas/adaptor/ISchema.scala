package io.transwarp.midas.adaptor

trait ISchema {
  def getType: String
  def getName: String
  def getRole: String
  def getValues: java.util.List[String]

}
