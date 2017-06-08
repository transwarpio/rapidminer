package io.transwarp.midas.remote
import io.transwarp.midas.adaptor.IExecutionUnit
import io.transwarp.midas.result.{JobStatus, ValidationResult}


class RemoteExecutor extends IRemoteExecutor {
  override def execute(unit: IExecutionUnit): JobStatus = null
  override def validate(unit: IExecutionUnit, mode: String): ValidationResult = null
  override def isRemote(unit: IExecutionUnit): Boolean = false
  override def isLocal(unit: IExecutionUnit): Boolean = false
  override def checkState(unit: IExecutionUnit, remote: Boolean): Boolean = false
  override def login(username: String, password: String): Boolean = false
  override def logout(): Boolean = false
}
