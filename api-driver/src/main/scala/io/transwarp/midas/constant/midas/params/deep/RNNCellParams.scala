package io.transwarp.midas.constant.midas.params.deep

/**
  * Created by xiao on 16-12-15.
  */
object RNNCellParams {

  val Cell = "cell"
  val NumCells = "numCells"
  val NumUnits = "numUnits"
  val Activation = "activation"
  val forgetBias = "forgetBias"

  val CellOptions: Array[String] = Array("BasicRNNCell", "GRUCell", "BasicLSTMCell")
  val ActivationOptions: Array[String] = Array("relu", "tanh", "sigmoid", "softmax")
}
