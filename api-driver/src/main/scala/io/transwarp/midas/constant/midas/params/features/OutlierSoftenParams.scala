package io.transwarp.midas.constant.midas.params.features

/**
  * Created by viki on 17-2-21.
  */
object OutlierSoftenParams {
  // main parameter
  val method = "Method"

  // sub parameter
  val upperPercentage = "Upper Percentage"
  val lowerPercentage = "Lower Percentage"
  val upperThreshold = "Upper Threshold"
  val lowerThreshold = "Lower Threshold"

  // parameter doc
  val methodDoc = "Method to soften the outlier of the selected attributes." +
    "The selected attributes must be double."
  val upperPerDoc = "The upper bound of percentage method. " +
    "Must be in the range [0, 1] and bigger than lowerPercentage."
  val lowerPerDoc = "The lower bound of percentage method. " +
    "Must be in the range [0, 1] and smaller than upperPercentage."
  val upperThresholdDoc = "The upper bound of threshold method. " +
    "Must be numeric and bigger than lowerThreshold."
  val lowerThresholdDoc = "The lower bound of threshold method. " +
    "Must be numeric and smaller than upperThreshold."


  // supported method
  val zScore = "zScore"
  val percentage = "percentage"
  val threshold = "threshold"
  val supportedMethod = Array(zScore, percentage, threshold)

  // default value
  val defaultMethod = zScore
}
