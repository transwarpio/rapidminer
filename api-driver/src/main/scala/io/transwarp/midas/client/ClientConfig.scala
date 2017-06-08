package io.transwarp.midas.client

import java.io.File

object ClientConfig {

  var midasClintHome: String = null

  def setMidasClientHome( home: String ): Unit = {
    midasClintHome = home
  }

  def getMidasClientHome: String = midasClintHome

  def getMidasConfDir : String = Option(midasClintHome).map( _ + "/lib/conf/")
    // scalastyle:off
    .getOrElse( throw new Exception(s"Midas home not setup"))

  def wrapDriverClassPath(jar: String): Seq[File] = Seq(jar, getMidasConfDir).map(new File(_))
}
