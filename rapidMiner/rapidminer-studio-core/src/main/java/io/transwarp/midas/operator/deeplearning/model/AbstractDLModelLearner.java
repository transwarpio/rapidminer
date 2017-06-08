package io.transwarp.midas.operator.deeplearning.model;

import com.rapidminer.operator.*;
import com.rapidminer.operator.learner.CapabilityProvider;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.SubprocessTransformRule;
import io.transwarp.midas.operator.deeplearning.layer.AbstractLayer;
import io.transwarp.midas.operator.deeplearning.layer.OutputLayer;

import java.util.LinkedList;
import java.util.List;

abstract class AbstractDLModelLearner extends OperatorChain implements CapabilityProvider{

	// Ports
	protected OutputPort modelPort = getOutputPorts().createPort("model");
	protected InputPort inputPort = getInputPorts().createPort("input");
	protected OutputPort subprocessPort = getSubprocess(0)
			.getInnerSources().createPort("input");

    // List
	protected List<AbstractLayer> structure = new LinkedList<AbstractLayer>();

    // Constructor
	public AbstractDLModelLearner(OperatorDescription description){
		super(description, "Layer Structure");
		remote = true;
		getTransformer().addRule(new SubprocessTransformRule(getSubprocess(0)));
	}

	@Override
	public boolean supportsCapability(OperatorCapability capability) {
		switch (capability) {
			case NUMERICAL_ATTRIBUTES:
			case POLYNOMINAL_LABEL:
			case BINOMINAL_LABEL:
			case NUMERICAL_LABEL:
			case WEIGHTED_EXAMPLES:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void doWork() throws OperatorException {
		super.doWork();

		List<Operator> list = getSubprocess(0).getEnabledOperators();
		structure = convertStructure(getStructure(list));
		if (structure == null || structure.size() == 0){
			throw new OperatorException("Please specify the structure of the neural network "
					+ this.getName() +  ", at least one layer is needed");
		}
	}

	private List<Operator> getStructure(List<Operator> list) throws OperatorException{
		List<Operator> result = new LinkedList<Operator>();

		for (Operator operator : list) {
			Class<? extends Operator> classType = operator.getClass();

			if(classType == SimpleOperatorChain.class) {
				SimpleOperatorChain simpleOperatorChain = (SimpleOperatorChain) operator;
				result.addAll(getStructure(simpleOperatorChain.getSubprocess(0).getEnabledOperators()));
			}
			else if (AbstractLayer.class.isAssignableFrom(classType)) {
				AbstractLayer abstractOperator = (AbstractLayer) operator;
				if(abstractOperator.isLinked() || classType == OutputLayer.class) {
					result.add(operator);
				}
			}
			else {
				throw new OperatorException("Invalid operator nested in " + getName() +"; only layers allowed");
			}
		}
		return result;
	}

	private List<AbstractLayer> convertStructure(List<Operator> list) throws OperatorException{
		List<AbstractLayer> result = new LinkedList<AbstractLayer>();

		for (Operator aList : list) {
			result.add((AbstractLayer) aList);
		}
		return result;
	}
}
