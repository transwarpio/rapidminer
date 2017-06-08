package io.transwarp.midas.constant.midas.params.table

/**
  * Created by linchen on 16-12-14.
  * it is used for all aggregation ops like agg, groupby, rollup, cube, groupby
  */
object AggParams {
  val GroupByColumns = "groupby columns"
  val Aggregations = "aggregations"
  val onlyDistinct = "onlyDistinct"
  val PivotColumn = "pivot column"

  val avg = "avg"
  val count = "count"
  val sum = "sum"
  val max = "max"
  val min = "min"
  val variance = "variance"
  val standardDeviation = "standardDeviation"

  val collectSet = "collect_set"
  val collectList = "collect_list"
}
