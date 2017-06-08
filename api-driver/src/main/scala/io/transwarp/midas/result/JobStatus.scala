package io.transwarp.midas.result

import io.transwarp.midas.client.Session
import io.transwarp.midas.result.JobStatus._

// scalastyle:off
class JobStatus(val id: Int, val session: Session, val state: String,
                val error: String, val startTime: Long, val endTime: Long) {
  private var done = false

  def isDone(): Boolean = done

  def setDone(): Unit = {
    done = true
  }

  def isSent(): Boolean = {
    state.equals(SENT)
  }

  def isFailed(): Boolean = {
    state.equals(FAILED)
  }

  def isSucceeded(): Boolean = {
    state.equals(SUCCEEDED)
  }

  def isFinished(): Boolean = {
    val v1 = state.equals(FAILED)
    val v2 = state.equals(SUCCEEDED)
    val v3 = state.equals(CANCELLED)
    v1 || v2 || v3
  }

  override def equals(obj: scala.Any): Boolean = {
    if (!obj.isInstanceOf[JobStatus]) return false
    val status = obj.asInstanceOf[JobStatus]
    status.id == id &&
      status.session.id == session.id &&
      status.session.timestamp == session.timestamp
  }
}

object JobStatus {
  val SUCCEEDED = "SUCCEEDED"
  val SENT = "SENT"
  val FAILED = "FAILED"
  val CANCELLED = "CANCELLED"
}
