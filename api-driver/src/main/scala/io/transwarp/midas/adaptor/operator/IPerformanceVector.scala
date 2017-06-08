package io.transwarp.midas.adaptor.operator

import io.transwarp.midas.adaptor.IIOObject

trait IPerformanceVector extends IIOObject{
  def addICriterion(criterion: IPerformanceCriterion): Unit
}
