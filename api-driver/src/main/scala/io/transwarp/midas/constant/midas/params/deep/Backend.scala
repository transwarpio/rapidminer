package io.transwarp.midas.constant.midas.params.deep

/**
  * Created by xiao on 16-11-17.
  */
sealed trait Backend { def name: String }
case object TF extends Backend { val name = "TensorFlow" }


