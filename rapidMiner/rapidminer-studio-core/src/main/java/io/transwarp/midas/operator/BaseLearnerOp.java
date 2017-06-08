package io.transwarp.midas.operator;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import io.transwarp.midas.constant.midas.PortNames;

public class BaseLearnerOp extends BaseOp {
	private final InputPort trainSet = getInputPorts().createPort(PortNames.TrainSet());
	private final OutputPort modelOutput = getOutputPorts().createPort(PortNames.Model());

	public BaseLearnerOp(OperatorDescription description) {
		super(description);
	}
}
