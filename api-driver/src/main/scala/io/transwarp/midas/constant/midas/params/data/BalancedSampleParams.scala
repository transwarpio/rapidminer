package io.transwarp.midas.constant.midas.params.data

/**
  * Created by viki on 17-3-31.
  */
object BalancedSampleParams {
  val labelColumn = "Label column"
  val labelA = "Label A"
  val labelB = "Label B"
  val sampleRatio = "Sample Ratio"
  val seed = "seed"

  val labelColDoc = "Label column."
  val aDoc = "One of the value in 'Label'."
  val bDoc = "The other value in 'Label'."
  val sampleRatioDoc = "Sample number ratio (A:B). Must greater than 0."
  val seedDoc = "Random seed. Must be positive long integer or -1. " +
    "If the value = -1, it'll generate seed automatically/"
}
