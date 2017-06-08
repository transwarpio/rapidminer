package io.transwarp.midas;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeWeights;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.NumericalAttribute;
import com.rapidminer.example.table.StatisticsSet;
import com.rapidminer.gui.ExampleVisualizer;
import com.rapidminer.operator.SimpleResultObject;
import com.rapidminer.operator.clustering.Centroid;
import com.rapidminer.operator.clustering.CentroidClusterModel;
import com.rapidminer.operator.features.transformation.PCAModel;
import com.rapidminer.operator.features.transformation.SVDModel;
import com.rapidminer.operator.learner.bayes.SimpleDistributionModel;
import com.rapidminer.operator.learner.functions.neuralnet.*;
import com.rapidminer.operator.learner.tree.ConfigurableRandomForestModel;
import com.rapidminer.operator.learner.tree.Tree;
import com.rapidminer.operator.learner.tree.TreeModel;
import com.rapidminer.operator.meta.ParameterSet;
import com.rapidminer.operator.performance.*;
import com.rapidminer.studio.io.data.DataSetReader;
import com.rapidminer.tools.RandomGenerator;
import io.transwarp.midas.adaptor.*;
import io.transwarp.midas.adaptor.function.IActivationFunction;
import io.transwarp.midas.adaptor.meta.IParameterSet;
import io.transwarp.midas.adaptor.model.IPCAModel;
import io.transwarp.midas.adaptor.model.ISVDModel;
import io.transwarp.midas.adaptor.model.neuralnet.*;
import io.transwarp.midas.adaptor.model.tree.*;
import io.transwarp.midas.adaptor.operator.*;
import io.transwarp.midas.adaptor.tools.IRandomGenerator;
import io.transwarp.midas.impl.MemoryDataSet;
import io.transwarp.midas.impl.SplitCondition;
import io.transwarp.midas.model.*;
import io.transwarp.midas.model.BinaryClassificationPerformance;
import io.transwarp.midas.model.MultiClassificationPerformance;
import org.jfree.data.xy.YIntervalSeries;

import java.util.ArrayList;
import java.util.List;

public class MidasFrontendFactory implements FrontendFactory {

    @Override
    public IResultObject makeSimpleResultObject(String name, String content) {
        return new SimpleResultObject(name, content);
    }

    @Override
    public ICentroid makeCentroid(Integer k) {
        return new Centroid(k);
    }

    @Override
    public ICentroidClusterModel makeCentroidClusterModel(IExampleSet data, Integer k, List<String> features) {
        return new CentroidClusterModel(data, k, features, null, false, false);
    }

    @Override
    public IExampleVisualizer makeExampleVisualizer(IExampleSet exampleSet) {
        return new ExampleVisualizer((ExampleSet)exampleSet);
    }

    @Override
    public IAttributeWeights makeAttributeWeights() {
        return new AttributeWeights();
    }

    @Override
    public ILinearModel makeLinearModel(IExampleSet data, double[] weights, double incept) {
        return new LinearModel((ExampleSet)data, weights, incept);
    }

    @Override
    public ILogisticRegressionModel makeLogisticRegressionModel(IExampleSet data, double[] weights, double incept, double threhold) {
        return new LogisticRegressionModel((ExampleSet)data, weights, incept, threhold);
    }

    @Override
    public ISimpleDistributionModel makeSimpleDistributionModel(IExampleSet data) {
        return new SimpleDistributionModel((ExampleSet)data);
    }

    @Override
    public IInputNode makeInputNode(String name) {
        return new InputNode(name);
    }

    @Override
    public IRandomGenerator makeRandomGenerator(int seed) {
        return new RandomGenerator(seed);
    }

    @Override
    public IActivationFunction makeSigmoidFunction() {
        return new SigmoidFunction();
    }

    @Override
    public IInnerNode makeInnerNode(String name, int index, IRandomGenerator randomGenerator, IActivationFunction activationFunction) {
        return new InnerNode(name, index, (RandomGenerator)randomGenerator, (ActivationFunction) activationFunction);
    }

    @Override
    public IOutputNode makeOutputNode(String label, IAttribute attr) {
        return new OutputNode(label, (Attribute)attr, 0.0, 0.0);
    }

    @Override
    public IAttribute makeNumericalAttribute(String label) {
        return new NumericalAttribute(label);
    }

    @Override
    public int OUTPUT() {
        return -2;
    }

    @Override
    public boolean connect(INode from, INode to) {
        return Node.connect((Node)from, (Node)to);
    }

    @Override
    public IImprovedNeuralNetModel makeImprovedNeuralNetwork(IExampleSet data, IInputNode[] inputs, IInnerNode[] inners, IOutputNode[] outputs) {
        InputNode[] in = new InputNode[inputs.length];
        for( int i = 0; i < inputs.length; i++){
            in[i] = (InputNode)inputs[i];
        }

        InnerNode[] inner = new InnerNode[inners.length];
        for(int i = 0; i < inners.length; i++){
           inner[i] = (InnerNode) inners[i];
        }

        OutputNode[] out  = new OutputNode[outputs.length];
        for(int i = 0; i < outputs.length; i++){
            out[i] = (OutputNode) outputs[i];
        }
        return new ImprovedNeuralNetModel((ExampleSet)data, in, inner, out);
    }

    @Override
    public IPCAModel makePCAModel(IExampleSet data, String[] names, double[] vector, double[][] matrics) {
        return new PCAModel((ExampleSet)data, names, vector, matrics);
    }

    @Override
    public IExampleSet makeDummySchemaDataSet(List<ISchema> schemas) throws Exception {
        MemoryDataSet memoryDataSet = new MemoryDataSet(schemas);
        DataSetReader reader = new DataSetReader(null, memoryDataSet.getColumnMetaData(), true);
        ExampleSet exampleSet = reader.read(memoryDataSet, null);
        return exampleSet;
    }

    @Override
    public IExampleSet makeSchemaDataSet(IDataSet dataSet) throws Exception {
        MemoryDataSet memoryDataSet = new MemoryDataSet(dataSet);
        DataSetReader reader = new DataSetReader(null, memoryDataSet.getColumnMetaData(), true);
        ExampleSet exampleSet = reader.read(memoryDataSet, null);
        return exampleSet;
    }

    @Override
    public IExampleSet makeStatisticsSet(IDataSet dataSet) throws Exception {
        MemoryDataSet memoryDataSet = new MemoryDataSet(dataSet);
        DataSetReader reader = new DataSetReader(null, memoryDataSet.getColumnMetaData(), true);
        StatisticsSet statSet = new StatisticsSet((SimpleExampleSet) reader.read(memoryDataSet, null));
        return statSet;
    }

    @Override
    public ISVDModel makeSVDModel(IExampleSet data, String[] names, double[] vector, double[][] matrics) {
        return new SVDModel((ExampleSet)data, names, vector, matrics);
    }

    @Override
    public ILinearModel makeLinearSVMModel(IExampleSet data, double[] weights, double incept) {
        return new LinearSVMModel((ExampleSet)data, weights, incept);
    }

    @Override
    public ITreeModel makeTreeModel(IExampleSet data, ITree tree) {
        return new TreeModel((ExampleSet)data, (Tree)tree);
    }

    @Override
    public IRandomForestModel makeRandomForestModel(IExampleSet data, List<ITree> trees, String strategy) {
        List<TreeModel> models = new ArrayList<>();
        for(ITree tree : trees){
            models.add(new TreeModel((ExampleSet)data, (Tree)tree));
        }
        return new ConfigurableRandomForestModel((ExampleSet)data, models, ConfigurableRandomForestModel.VotingStrategy.MAJORITY_VOTE);
    }


    @Override
    public ITree makeTreeNode(IExampleSet data) {
        return new Tree((ExampleSet)data);
    }

    @Override
    public ISplitCondition makeSplitCondition(ICondition condition, IVisitorContext context) {
        return new SplitCondition(condition, context.getSchemas());
    }

    @Override
    public IParameterSet makeParameterSet(String[] operators, String[] parameters, String[] values) {
        return new ParameterSet(operators, parameters, values);
    }

    @Override
    public IThresholds makeThresholds(double[] values) {
        return new Thresholds(values);
    }

    @Override
    public IPerformanceVector makePerformanceVector() {
        return new PerformanceVector();
    }

    @Override
    public IIntervalSeries makeIntervalSeries(String name) {
        return new IntervalSeries(name);
    }

    @Override
    public IPerformanceCriterion makeCurveCollection(String name, double value, IIntervalSeries[] series) {
        CurveCollection col = new CurveCollection(name, value);
        for (IIntervalSeries ser : series) {
            col.addSeries((YIntervalSeries)ser);
        }
        return col;
    }

    @Override
    public IPerformanceCriterion makeBinaryClassificationPerformance(String name, double value, double std, double[][] counts, String[] labels) {
        return new BinaryClassificationPerformance(name, value, std, counts, labels);
    }

    @Override
    public IPerformanceCriterion makeMultiClassificationPerformance(String name, double value, double std, double[][] counts, String[] labels) {
        return new MultiClassificationPerformance(name, value, std, counts, labels);
    }

    @Override
    public IPerformanceCriterion makeTextCriterion(String name, double value) {
        return new TextCriterion(name, value);
    }
}
