package io.transwarp.midas.utils

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  @transient private lazy val log_ : Logger = LoggerFactory.getLogger(logName)
  val prefix: String
  private def log: Logger = log_
  private def f(msg: => String) = s"<${prefix}> $msg"
  protected def logName = {
    // Ignore trailing $'s in the class names for Scala objects
    this.getClass.getName.stripSuffix("$")
  }

  def logInfo(msg: => String): Unit = {
    if (log.isInfoEnabled) log.info(f(msg))
  }

  def logDebug(msg: => String): Unit = {
    if (log.isDebugEnabled) log.debug(f(msg))
  }

  def logTrace(msg: => String): Unit = {
    if (log.isTraceEnabled) log.trace(f(msg))
  }

  def logWarning(msg: => String): Unit = {
    if (log.isWarnEnabled) log.warn(f(msg))
  }

  def logError(msg: => String): Unit = {
    if (log.isErrorEnabled) log.error(f(msg))
  }

  def logInfo(msg: => String, throwable: Throwable): Unit = {
    if (log.isInfoEnabled) log.info(f(msg), throwable)
  }

  def logDebug(msg: => String, throwable: Throwable): Unit = {
    if (log.isDebugEnabled) log.debug(f(msg), throwable)
  }

  def logTrace(msg: => String, throwable: Throwable): Unit = {
    if (log.isTraceEnabled) log.trace(f(msg), throwable)
  }

  def logWarning(msg: => String, throwable: Throwable): Unit = {
    if (log.isWarnEnabled) log.warn(f(msg), throwable)
  }

  def logError(msg: => String, throwable: Throwable): Unit = {
    if (log.isErrorEnabled) log.error(f(msg), throwable)
  }
}
