package io.transwarp.midas.adaptor.model.tree

trait ITree {
  def addIChild(child: ITree, condition: ISplitCondition): Unit
  def setLeaf(label: String): Unit
  def addCount(label: String, count: Int): Unit
}
