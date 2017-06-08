package io.transwarp.midas.constant.midas.params.deep

/**
  * Created by xiao on 16-12-2.
  */
object DenseLayerParams {
  // Parameters.
  val Activation = "activation"
  val NumNodes = "numNodes"

  // Supported parameter choice.
  val ActivationOptions: Array[String] = Array(
    "relu", "tanh", "sigmoid")
}
