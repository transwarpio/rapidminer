package io.transwarp.midas.result

import io.transwarp.midas.result.SessionState._

class SessionState(val id: Int, val timestamp: Long, val state: String) {
  def isStarting(): Boolean = {
    state.equals(STARTING)
  }

  def isActive(): Boolean = {
    state.equals(IDLE) || state.equals(Busy) || state.equals(Running)
  }
}

object SessionState {
  val STARTING = "starting"
  val IDLE = "idle"
  val Running = "running"
  val Busy = "busy"
  val DEAD = "dead"
  val ERROR = "error"
}
