package io.transwarp.midas.adaptor

trait IExample {
  def getLabel(): Double
  def getPredictionLabel(): Double
  def getIValue(id: IAttribute): Int
}
