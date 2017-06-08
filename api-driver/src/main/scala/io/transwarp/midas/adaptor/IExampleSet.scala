package io.transwarp.midas.adaptor

trait IExampleSet {
  def getAttributes(): IAttributes
  def size(): Int
  def getExample(idx: Int): IExample
}
