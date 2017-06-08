package io.transwarp.midas.constant.midas.params.deep

/**
  * Created by xiao on 16-12-2.
  */
object OutputLayerParams {
  // Parameters.
  val NumNodes = "numNodes"
  val Activation = "activation"
  val Loss = "loss"

  // Supported parameter choice.
  val ActivationOptions: Array[String] = Array("softmax")
  val LossOptions: Array[String] = Array("cross entropy")
}
