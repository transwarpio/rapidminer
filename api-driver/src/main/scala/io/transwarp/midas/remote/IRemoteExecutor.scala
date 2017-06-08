package io.transwarp.midas.remote

import io.transwarp.midas.adaptor.IExecutionUnit
import io.transwarp.midas.result.{JobStatus, ValidationResult}

trait IRemoteExecutor {
  def execute(unit: IExecutionUnit): JobStatus
  def validate(unit: IExecutionUnit, mode: String): ValidationResult
  def isRemote(unit: IExecutionUnit): Boolean
  def isLocal(unit: IExecutionUnit): Boolean
  def checkState(unit: IExecutionUnit, remote: Boolean): Boolean
  def login(username: String, password: String): Boolean
  def logout(): Boolean
}
