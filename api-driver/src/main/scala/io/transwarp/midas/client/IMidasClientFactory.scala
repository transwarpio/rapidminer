package io.transwarp.midas.client

trait IMidasClientFactory {
  // Public Methods.
  def addConf(key: String, value: String): Unit = {}
  def getClientInstance: MidasClient
}
