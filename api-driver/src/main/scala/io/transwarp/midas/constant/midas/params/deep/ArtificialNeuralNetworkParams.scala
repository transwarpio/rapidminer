package io.transwarp.midas.constant.midas.params.deep

/**
  * Created by xiao on 16-12-2.
  */
object ArtificialNeuralNetworkParams {
  // Parameters.
  val Framework = "framework"
  val Mode = "mode"
  val Single = "single node"
  val WorkAndPs = "worker and parameter server"
  val SummaryPath = "summaryPath"
  val OptimizationAlgorithm = "optimizationAlgorithm"
  val LearningRate = "learningRate"
  val Iteration = "iteration"
  val BatchSize = "batchSize"
  val DisplayStride = "displayStride"

  // Description.
  val FrameworkDesc = "framework"
  val SummaryPathDesc = "summary_path"
  val OptimizationAlgorithmDesc = "optimization_algorithm"
  val LearningRateDesc = "learning_rate"
  val IterationDesc = "iteration"
  val BatchSizeDesc = "batch_size"
  val DisplayStrideDesc = "display_stride"
  val FilenamesDesc = "filenames"

  // Supported parameter choice.
  val FrameworkOptions: Array[String] = Array("TensorFlow")
  val OptimizationAlgorithmOptions: Array[String] = Array(
    "Gradient Descent", "Adam")
}
