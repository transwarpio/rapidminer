package io.transwarp.midas.adaptor.model

import io.transwarp.midas.adaptor.IIOObject

trait IPCAModel extends IIOObject{
  def setNumberOfComponents(k: Int): Unit
}
