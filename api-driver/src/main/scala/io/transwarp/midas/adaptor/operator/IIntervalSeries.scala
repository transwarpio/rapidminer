package io.transwarp.midas.adaptor.operator

trait IIntervalSeries {
  def add(x: Double, y: Double, z: Double, t: Double): Unit

}
