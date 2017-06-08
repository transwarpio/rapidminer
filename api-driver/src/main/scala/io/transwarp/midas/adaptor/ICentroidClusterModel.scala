package io.transwarp.midas.adaptor

trait ICentroidClusterModel extends IIOObject{
  def setICentroids(centroids: java.util.List[ICentroid]): Unit
  def getICluster(idx: Int): ICluster
}
