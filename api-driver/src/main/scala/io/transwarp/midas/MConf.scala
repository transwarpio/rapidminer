package io.transwarp.midas

import java.io.{InputStreamReader, File}
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Properties

import scala.collection.JavaConverters._
import scala.collection.mutable

// if defaultValue is null, it means no default value
case class ConfEntry(name: String, defaultValue: String)

trait MConf {
  val conf = new mutable.HashMap[String, String]()
  def getConfFile(): String
  def getHome(): String
  def getConfDir(): String

  def loadConf(): Unit = {
    val path = getConfFile()
    val file = new File(path)
    if (file.exists()) {
      loadProperties(file.toURI.toURL).map{ case (k, v) => conf.put(k, v)}
    } else {
      // scalastyle:off
      throw new IllegalArgumentException(s"conf file ${path} not found")
      // scalastyle:on
    }
  }

  /**
    * Update configuration map.
    */
  def updateConf(key: String, value: String): Unit = {
    conf.put(key, value)
  }

  def isDefined(entry: ConfEntry): Boolean = {
    conf.contains(entry.name) || entry.defaultValue != null
  }

  def get(entry: ConfEntry): String = {
    conf.getOrElse(entry.name, entry.defaultValue)
  }

  private def loadProperties(url: URL): Map[String, String] = {
    val inReader = new InputStreamReader(url.openStream(), UTF_8)
    try {
      val properties = new Properties()
      properties.load(inReader)
      properties.stringPropertyNames().asScala.map { k =>
        (k, properties.getProperty(k).trim())
      }.toMap
    } finally {
      inReader.close()
    }
  }
}
