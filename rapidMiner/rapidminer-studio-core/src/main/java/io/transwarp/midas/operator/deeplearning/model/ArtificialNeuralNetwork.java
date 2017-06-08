package io.transwarp.midas.operator.deeplearning.model;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.deep.ArtificialNeuralNetworkParams;

import java.util.List;

public class ArtificialNeuralNetwork extends AbstractDLModelLearner {

    // Constructor
	public ArtificialNeuralNetwork(OperatorDescription description) {
		super(description);
	}

	@Override
	public List<ParameterType> getParameterTypes() {

		List<ParameterType> types = super.getParameterTypes();

		// Parameter framework
		types.add(new ParameterTypeCategory(
				ArtificialNeuralNetworkParams.Framework(),
				"The framework used to generate deep learning code",
				ArtificialNeuralNetworkParams.FrameworkOptions(),
				0, false));

		// Parameter summary path
		types.add(new ParameterTypeString(
				ArtificialNeuralNetworkParams.SummaryPath(),
				"The full path to store summary after the process is finished",
				"", false));

		// Parameter optimization algorithm
		types.add(new ParameterTypeCategory(
				ArtificialNeuralNetworkParams.OptimizationAlgorithm(),
				"The optimization function",
				ArtificialNeuralNetworkParams.OptimizationAlgorithmOptions(),
				1, false));

		// Parameter learning rate
		types.add(new ParameterTypeDouble(
				ArtificialNeuralNetworkParams.LearningRate(),
				"The learning rate determines by how much" +
						"we change the weights at each step." +
						"May not be 0.",
				Double.MIN_VALUE, 1.0d, 0.9, true));

		// Parameter iteration
		types.add(new ParameterTypeInt(
				ArtificialNeuralNetworkParams.Iteration(),
				"The number of iterations used for the neural network training.",
				1, Integer.MAX_VALUE, 500));

		// Parameter batch size
		types.add(new ParameterTypeInt(
				ArtificialNeuralNetworkParams.BatchSize(),
				"The size of each batch of pre-fetched input.",
				1, Integer.MAX_VALUE, 100));

		// Parameter display stride
		types.add(new ParameterTypeInt(
				ArtificialNeuralNetworkParams.DisplayStride(),
				"The result will be printed once a display stride",
				1, Integer.MAX_VALUE, 100));

		types.add(new ParameterTypeCategory(
				ArtificialNeuralNetworkParams.Mode(),
				"run on single node or on workers and parameter servers",
				new String[]{ArtificialNeuralNetworkParams.Single(),
						ArtificialNeuralNetworkParams.WorkAndPs()},
				0, true));

		return types;
	}

}
