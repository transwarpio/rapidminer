package io.transwarp.midas.constant.midas.params.tuning

/**
  * Created by tianming on 5/13/16.
  */
object PerfParams {
  val MainCriterion = "main criterion"
  val ConfusionMatrix = "confusion matrix"
  val PositiveLabel = "positive label value"

  val Precision = "precision"
  val Recall = "recall"
  val PR = "precision recall curve"
  val FMeasure = "F-measure"
  val ROC = "ROC"
  val KS = "k-s"
  val Lift = "lift"
  val AUROC = "area under ROC"
  val AUPRC = "area under precision recall curve"

  val BinaryOptions = Array(
    ROC,
    KS,
    Lift,
    PR,
    Recall,
    Precision,
    FMeasure
  )
  // for multi class
  val Accuracy = "accuracy"
  val WeightedPrecision = "weighted precision"
  val WeightedRecall = "weighted recall"
  val WeightedFMeasure = "weighted f measure"

  val MultiOptions = Array(
    WeightedRecall,
    WeightedPrecision,
    WeightedFMeasure,
    Accuracy
  )
  // for regression

  val RMSE = "root mean squared error"
  val MSE = "mean squared error"
  val R2 = "r2"
  val MAE = "mean absolute error"

  val RegressionOptions = Array(
    MAE,
    MSE,
    R2,
    RMSE
  )
  // for cluster
  val DBI = "Davies–Bouldin index"
  val ClusterOptions = Array(
    DBI
  )
}
