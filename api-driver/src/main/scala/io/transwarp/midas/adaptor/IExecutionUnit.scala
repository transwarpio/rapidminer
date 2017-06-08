package io.transwarp.midas.adaptor

trait IExecutionUnit {
  def getIInnerSinks : IInputPorts
  def getIEnclosingOperator: IOperator
  def getIOperatorEnumeration: java.util.Enumeration[IOperator]

}
