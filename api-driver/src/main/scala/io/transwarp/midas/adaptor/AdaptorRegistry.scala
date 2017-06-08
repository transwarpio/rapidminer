package io.transwarp.midas.adaptor

object AdaptorRegistry {
  var parameterService : IParameterService = null
  def setParameterService( ops: IParameterService ) : Unit = {
    parameterService = ops
  }

  def getParameterService() : IParameterService = parameterService

  var frontEndFactory: FrontendFactory = null
  def setFrontEndFactory(off: FrontendFactory) : Unit = {
    frontEndFactory = off
  }

  def getFrontEndFactory(): FrontendFactory = frontEndFactory


  var visualizer: IVisualizerService = null
  def setVisualizer(ov: IVisualizerService): Unit = {
    visualizer = ov
  }

  def getVisualizer(): IVisualizerService = visualizer

}
