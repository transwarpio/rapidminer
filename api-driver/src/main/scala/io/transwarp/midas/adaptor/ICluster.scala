package io.transwarp.midas.adaptor

trait ICluster {
  def assignExample(example: Object): Unit
}
