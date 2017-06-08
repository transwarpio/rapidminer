package io.transwarp.midas.constant.midas.params

/**
  * Created by endy on 16-8-24.
  */
object LDAParams {

  val MaxIter = "maxIter"
  val k = "k"
  val DocConcentration = "docConcentration"
  val TopicConcentration = "topicConcentration"
  val Optimizer = "optimizer"
  val LearningOffset = "learningOffset"
  val LearningDecay = "learningDecay"
  val SubsamplingRate = "subsamplingRate"
  val OptimizeDocConcentration = "optimizeDocConcentration"
  val Topics = "topics"

  val OptimizerOptions: Array[String] = Array("online", "em")
}
