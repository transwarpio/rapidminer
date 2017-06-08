package io.transwarp.midas.adaptor

trait ICondition {
  def getFeature: String
  def getRelation: String
  def getValue: String
}
