package io.transwarp.midas.client

import java.io.File

import io.transwarp.midas.constant.midas.params.CustomParams

import scala.collection.mutable
import scala.xml._


class CustomOpProperty() {
  private var _name: String = _
  private var _type: String = _
  private var _file: String = _
  private var _class: String = _

  override def toString: String = {
    _name
  }

  def getName: String = _name
  def getFile: String = _file
  def getClazz: String = _class
  def getType: String = _type

  def setName(name: String): Unit = {
    _name = name
  }

  def setFile(jar: String): Unit = {
    _file = jar
  }

  def setClazz(clazz: String): Unit = {
    _class = clazz
  }

  def setType(tpe: String): Unit = {
    _type = tpe
  }
}

object CustomOpManager {
  val ops: mutable.ListBuffer[CustomOpProperty] = mutable.ListBuffer()
  def load(file: File): Unit = {
    if (file.exists()) {
      val doc = XML.loadFile(file)
      val nodes = doc \ "Op"
      nodes.foreach(node => {
        val name = (node \ "@name").text
        val tpe = (node \ "@type").text
        val file = (node \ "@file").text
        val clazz = (node \ "@clazz").text
        val op = new CustomOpProperty()
        op.setName(name)
        op.setFile(file)
        op.setClazz(clazz)
        op.setType(tpe)
        addOp(op)
      })
    }
  }

  def addOp(op: CustomOpProperty): Unit = {
    ops.append(op)
  }

  def deleteOp(op: CustomOpProperty): Unit = {
    ops -= op
  }

  def save(file: File): Unit = {
    val nodes = ops.map(op => {
      <Op name={op.getName} type={op.getType} file={op.getFile} clazz={op.getClazz}>
      </Op>

    })

    val doc = {
      <Ops>
        {nodes}
      </Ops>
    }
    XML.save(file.getAbsolutePath, doc)
  }

  def getOps: Seq[CustomOpProperty] = ops.toSeq
  def getJavaOps: Seq[CustomOpProperty] = {
    ops.filter(_.getType.equals(CustomParams.Java))
  }

  def getPythonOps: Seq[CustomOpProperty] = {
    ops.filter(_.getType.equals(CustomParams.Python))
  }
}
