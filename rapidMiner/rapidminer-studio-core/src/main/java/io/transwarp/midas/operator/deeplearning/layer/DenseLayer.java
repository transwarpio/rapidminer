package io.transwarp.midas.operator.deeplearning.layer;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.deep.DenseLayerParams;

import java.util.List;

public class DenseLayer extends AbstractLayer {

	// Constructor
	public DenseLayer(OperatorDescription description) {
		super(description);
	}

	public List<ParameterType> getParameterTypes() {

		List<ParameterType> types = super.getParameterTypes();

        // Parameter number of node
		types.add(new ParameterTypeInt(
				DenseLayerParams.NumNodes(),
				"The number of nodes in this layer",
				1, Integer.MAX_VALUE, 10, false));

        // Parameter activation function
		types.add(new ParameterTypeCategory(
				DenseLayerParams.Activation(),
				"The activation function of this layer",
				DenseLayerParams.ActivationOptions(),
				2, false));

		return types;
	}
}
