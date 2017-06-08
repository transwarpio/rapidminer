package io.transwarp.midas.result

import java.util.Collection
import java.util.HashMap

import io.transwarp.midas.adaptor.IIOObject

class ResultHolder {
  private val result = new HashMap[String, IIOObject]()

  def getResults(): Collection[IIOObject] = {
    result.values()
  }

  def get(portName: String): IIOObject = {
    result.get(portName)
  }

  def set(portName: String, resultItem: IIOObject) {
    result.put(portName, resultItem)
  }
}
