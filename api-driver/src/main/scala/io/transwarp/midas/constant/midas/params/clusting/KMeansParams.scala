package io.transwarp.midas.constant.midas.params.clusting

/**
  * Created by tianming on 4/20/16.
  */
object KMeansParams {

  val K = "k"
  val Centers = "centers"
  val InitMode = "initMode"
  val InitSteps = "initSteps"
  val Tol = "tol"

  val InitModeOptions: Array[String] = Array("random", "k-means||")
}
