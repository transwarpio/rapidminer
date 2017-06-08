package io.transwarp.midas.adaptor.model.neuralnet

trait IInnerNode extends INode{
  def setWeights(weights: Array[Double]): Unit
}
