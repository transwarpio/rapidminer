package io.transwarp.midas.operator;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

public abstract class BaseDataProcessOp extends BaseOp {
	protected final InputPort exampleSetInput = getInputPorts().createPort("example set input");
	protected final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");

	public BaseDataProcessOp(OperatorDescription description) {
		super(description);
	}
}
