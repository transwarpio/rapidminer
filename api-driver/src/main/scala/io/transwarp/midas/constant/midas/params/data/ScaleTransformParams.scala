package io.transwarp.midas.constant.midas.params.data

/**
  * Created by viki on 17-2-13.
  */
object ScaleTransformParams {
  val scaleMethod = "scaleMethod"
  val scaleCol = "scaleCol"

  val log2 = "log2"
  val log10 = "log10"
  val ln = "ln"
  val abs = "abs"
  val sqrt = "sqrt"

  val supportedMethod = Array(log2, log10, ln, abs, sqrt)
}
