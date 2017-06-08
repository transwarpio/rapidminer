package io.transwarp.midas.result

import io.transwarp.midas.adaptor.ISchema

case class PortMetaResult(port: String, error: String, schema: Array[ISchema])
case class OpMetaResult(op: String, error: String, ports: Map[String, PortMetaResult])
class ValidationResult(val metas: Array[OpMetaResult]) {

}
