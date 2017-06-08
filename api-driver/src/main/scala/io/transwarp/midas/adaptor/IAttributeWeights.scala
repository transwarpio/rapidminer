package io.transwarp.midas.adaptor

trait IAttributeWeights extends IIOObject{
  def setWeight(name: String, value: Double): Unit
}
