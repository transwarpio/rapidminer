package io.transwarp.midas.adaptor.model

import io.transwarp.midas.adaptor.IIOObject

trait ISVDModel extends IIOObject{
  def setNumberOfComponents(k: Int): Unit

}
