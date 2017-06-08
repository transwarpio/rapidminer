package io.transwarp.midas.constant.midas.params

/**
  * Created by tianming on 4/20/16.
  */
object SharedParams {
  // shared params
  val RegParam = "regParam" // "regularization parameter (>= 0)"
  val MaxIter = "maxIter" // "maximum number of iterations (>= 0)"
  val FeaturesCol = "featuresCol" // "features column name"
  val LabelCol = "labelCol" // "label column name"
  val PredictionCol = "predictionCol" // "prediction column name"
  val RawPredictionCol = "rawPredictionCol" // "raw prediction (a.k.a. confidence) column name")
  val ProbCol = "probabilityCol" // "Column name for predicted class conditional probabilities.
  val Threshold = "threshold" // "threshold in binary classification prediction, in range [0, 1]")
  val Thresholds = "thresholds" // "Thresholds in multi-class classification"
  val InputCol = "inputCol" // "input column name")
  val InputCols = "inputCols" // "input column names")
  val OutputCol = "outputCol" // "output column name")
  val OutputCols = "outputCols" // "output column names"
  val CheckpointInterval = "checkpointInterval" // "checkpoint interval (>= 1)")
  val FitIntercept = "fitIntercept" // "whether to fit an intercept term")
  // "whether to standardize the training features before fitting the model.")
  val Standardization = "standardization"
  val Seed = "seed" // "random seed")
  val ElasticNetParam = "elasticNetParam" // "the ElasticNet mixing parameter, in range [0, 1]."
  val Tol = "tol" // "the convergence tolerance for iterative algorithms")
  val StepSize = "stepSize" // "Step size to be used for each iteration of optimization.")
  val WeightCol = "weightCol" // "weight column name
  val MinDocFreq = "minDocFreq"  // tfidf doc freq
  val QuantilesCol = "quantilesCol"
}
