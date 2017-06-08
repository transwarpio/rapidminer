package io.transwarp.midas.adaptor

import io.transwarp.midas.adaptor.function.IActivationFunction
import io.transwarp.midas.adaptor.meta.IParameterSet
import io.transwarp.midas.adaptor.model.neuralnet._
import io.transwarp.midas.adaptor.model.tree.{IRandomForestModel, ISplitCondition, ITree, ITreeModel}
import io.transwarp.midas.adaptor.model.{IPCAModel, ISVDModel}
import io.transwarp.midas.adaptor.operator._
import io.transwarp.midas.adaptor.tools.IRandomGenerator

trait FrontendFactory {
  def makeSimpleResultObject(name: String, content: String): IResultObject

  def makeCentroid(k: Integer): ICentroid

  def makeCentroidClusterModel(data: IExampleSet,
                               k: Integer, features: java.util.List[String]) : ICentroidClusterModel

  def makeExampleVisualizer(exampleSet: IExampleSet): IExampleVisualizer

  def makeAttributeWeights: IAttributeWeights

  def makeLinearModel(data: IExampleSet, weights: Array[Double], incept: Double): ILinearModel

  def makeLogisticRegressionModel(data: IExampleSet,
                                  weights: Array[Double],
                                  incept: Double,
                                  threhold: Double): ILogisticRegressionModel
  def makeSimpleDistributionModel(data: IExampleSet): ISimpleDistributionModel


  def makeInputNode(name: String): IInputNode
  def makeRandomGenerator(seed: Int): IRandomGenerator
  def makeSigmoidFunction: IActivationFunction
  def makeInnerNode(name: String,
                    index: Int,
                    randomGenerator: IRandomGenerator,
                    activationFunction: IActivationFunction): IInnerNode

  def makeOutputNode(label: String,
                     attr: IAttribute): IOutputNode

  def makeNumericalAttribute(label: String): IAttribute

  def OUTPUT: Int
  def connect(from: INode, to: INode): Boolean
  def makeImprovedNeuralNetwork(data: IExampleSet,
                                inputs: Array[IInputNode],
                                inners: Array[IInnerNode],
                                outputs: Array[IOutputNode]): IImprovedNeuralNetModel
  def makePCAModel(data: IExampleSet,
                   names: Array[String],
                   vector: Array[Double],
                   matrics: Array[Array[Double]]): IPCAModel

  @throws(classOf[Exception])
  def makeDummySchemaDataSet(schemas: java.util.List[ISchema]): IExampleSet
  @throws(classOf[Exception])
  def makeSchemaDataSet(dataSet: IDataSet): IExampleSet

  @throws(classOf[Exception])
  def makeStatisticsSet(dataSet: IDataSet): IExampleSet

  def makeSVDModel(data: IExampleSet,
                   names: Array[String],
                   vector: Array[Double],
                   matrics: Array[Array[Double]]): ISVDModel
  def makeLinearSVMModel(data: IExampleSet,
                         weights: Array[Double],
                         incept: Double): ILinearModel
  def makeTreeModel(data: IExampleSet,
                    tree: ITree): ITreeModel

  def makeRandomForestModel(data: IExampleSet,
                            models: java.util.List[ITree],
                            strategy: String): IRandomForestModel

  def makeTreeNode(data: IExampleSet): ITree
  def makeSplitCondition(condition: ICondition, context: IVisitorContext): ISplitCondition
  def makeParameterSet(operators: Array[String],
                       parameters: Array[String],
                       values: Array[String]): IParameterSet
  def makeThresholds(values: Array[Double]): IThresholds

  // performance
  def makePerformanceVector: IPerformanceVector
  def makeIntervalSeries(name: String): IIntervalSeries
  def makeCurveCollection(name: String,
                          value: Double,
                          series: Array[IIntervalSeries]): IPerformanceCriterion
  def makeBinaryClassificationPerformance(name: String,
                                          value: Double,
                                          std: Double,
                                          counts: Array[Array[Double]],
                                          labels: Array[String]): IPerformanceCriterion
  def makeMultiClassificationPerformance(name: String,
                                          value: Double,
                                          std: Double,
                                          counts: Array[Array[Double]],
                                          labels: Array[String]): IPerformanceCriterion
  def makeTextCriterion(name: String,
                        value: Double): IPerformanceCriterion
}
