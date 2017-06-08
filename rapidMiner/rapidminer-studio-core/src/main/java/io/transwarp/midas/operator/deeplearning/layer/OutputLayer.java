package io.transwarp.midas.operator.deeplearning.layer;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.deep.OutputLayerParams;

import java.util.List;

public class OutputLayer extends Operator {

	// Ports
	private final InputPort inputPort = getInputPorts().createPort("through");

	// Constructor
	public OutputLayer(OperatorDescription description) {
		super(description);
		remote = true;
	}

	@Override
	public List<ParameterType> getParameterTypes() {

		List<ParameterType> types = super.getParameterTypes();

		// Parameter number of classes
		types.add(new ParameterTypeInt(
				OutputLayerParams.NumNodes(),
				"The number of nodes in this layer",
				1, Integer.MAX_VALUE, 2, false));

        // Parameter activation function
		types.add(new ParameterTypeCategory(
				OutputLayerParams.Activation(),
				"The activation function of this layer",
				OutputLayerParams.ActivationOptions(),
				0, false));

        // Parameter loss function
		types.add(new ParameterTypeCategory(
				OutputLayerParams.Loss(),
				"The loss function of this layer",
				OutputLayerParams.LossOptions(),
				0, false));

		return types;
	}
}
