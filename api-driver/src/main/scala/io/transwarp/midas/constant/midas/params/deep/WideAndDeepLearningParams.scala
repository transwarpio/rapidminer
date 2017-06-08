package io.transwarp.midas.constant.midas.params.deep

object WideAndDeepLearningParams {
  val Framework = "framework"
  val ModelDir = "model_dir"
  val ModelType = "model_type"
  val TrainingSteps = "training_steps"
  val TrainingData = "training_data"
  val TestData = "test_data"

  // Description.
  val FrameworkDesc = "framework"

  // Options
  val FrameworkOptions: Array[String] = Array("TensorFlow")
  val ModelTypeOptions = Array("wide", "deep", "wide_n_deep")
}
