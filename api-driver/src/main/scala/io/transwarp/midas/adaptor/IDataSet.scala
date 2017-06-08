package io.transwarp.midas.adaptor

trait IDataSet {
  def getSchema: java.util.List[ISchema]
  def getRows: java.util.List[IRow]

}
