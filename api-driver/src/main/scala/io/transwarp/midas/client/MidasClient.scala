package io.transwarp.midas.client

import io.transwarp.midas.adaptor.IOperator
import io.transwarp.midas.result._
import io.transwarp.midas.thrift.message.{FunctionMsg, Repository}

trait MidasClient {
  def shutdown(): Unit
  def submit(root: IOperator): JobStatus

  def getSession: Session
  def getSessionState(session: Session): SessionState
  def startSession(): Unit
  def stopSession(): Unit
  def requireRestart(): Boolean
  // whether session is ready
  def verifySession(): Option[SessionState]
  def waitForSession(): Unit
  def ensureSession(): Unit = {
    synchronized {
      val state = verifySession()
      state match {
        case Some(s) =>
          if (s.isStarting()) {
            waitForSession()
          } else if (!s.isActive()) {
            // not an active session
            // start a new one
            startSession()
          }
        case None =>
          startSession()
      }

      if (requireRestart()) {
        // though session is ready, the user context has changed
        // so we have to restart the session
        stopSession()
        startSession()
      }
    }
  }

  def addFile(path: String): Unit
  def addJar(path: String): Unit

  def getJobs: Array[JobStatus]
  def getJobResult(status: JobStatus): ResultHolder
  def validateJob(root: IOperator, mode: String): ValidationResult
  def stopJob(status: JobStatus): Unit
  def stopJobs(): Unit
  def scheduleJob(job: String): Unit
  def submitJob(job: String): JobStatus
  def updateJobStatus(status: JobStatus): JobStatus
  def login(username: String, password: String): Boolean
  def logout(): Boolean

  def addCompleteCallback(fn: (JobStatus) => Unit): Unit
  def fireCallbacks(job: JobStatus): Unit

  def getRepository(): Repository
  def addSharing(content: Array[Byte], filename: String): Unit
  def mkdir(path: String): Unit
  def getSharing(path: String): Array[Byte]
  def deleteSharing(path: String): Unit
  def move(oldPath: String, newPath: String): Unit

  def getFunctions(): Map[String, Array[FunctionMsg]]
}
