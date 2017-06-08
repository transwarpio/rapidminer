package io.transwarp.midas.constant.midas.params.features

/**
  * Created by tianming on 5/6/16.
  */
object PCAParams {
  val K = "k"
  val VariancePercentage = "percent"
  val Mode = "mode"
  val EigenVectors = "eigen vectors"
  val EigenValues = "eigen values"
  val DimensionalityReduction = "mode"

  val None = "none"
  val FixedNumber = "fixedNumber"
  val KeepVariance = "keepVariance"
  val NoneId = 0
  val KeepVarianceId = 1
  val FixedNumberId = 2
  val ReductionMethods = Array(None, KeepVariance, FixedNumber)
  val VarianceThreshold = "percent"
  val NumberOfComponents = K
}
