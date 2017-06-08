package io.transwarp.midas.adaptor

trait IVisualizerService {
  def addIObjectVisualizer(iCentroidClusterModel: ICentroidClusterModel,
                          iExampleVisualizer: IExampleVisualizer): Unit
}
