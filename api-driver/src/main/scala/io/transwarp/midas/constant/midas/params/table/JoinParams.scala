package io.transwarp.midas.constant.midas.params.table

/**
  * Created by linchen on 16-10-13.
  */
object JoinParams {
  val JoinType = "join_type"

  val On = "on"

  val LeftKeyColumns = "left_key_columns"
  val RightKeyColumns = "right_key_columns"
  val LeftColumns = "left_columns"
  val RightColumns = "right_columns"

  val AddLeftPrefix = "add_left_prefix"
  val LeftPrefix = "left_prefix"
  val AddRightPrefix = "add_right_prefix"
  val RightPrefix = "right_prefix"

  val Inner = "inner"
  val Left = "left"
  val Right = "right"
  val Outer = "outer"

  val JoinTypes = Array(Inner, Left, Right, Outer)
}
