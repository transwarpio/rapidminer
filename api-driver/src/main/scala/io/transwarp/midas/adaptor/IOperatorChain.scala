package io.transwarp.midas.adaptor

trait IOperatorChain {
  def getISubprocesses: java.util.List[IExecutionUnit]
}
