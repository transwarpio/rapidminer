package io.transwarp.midas.adaptor

trait IInputPort {
  def getIName: String
  def receiveI(obj: IIOObject): Unit
}
