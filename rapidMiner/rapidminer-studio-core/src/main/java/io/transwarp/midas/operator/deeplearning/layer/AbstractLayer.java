package io.transwarp.midas.operator.deeplearning.layer;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.PassThroughRule;
import io.transwarp.midas.operator.deeplearning.connection.LayerSemaphore;

public abstract class AbstractLayer extends Operator {

	private final InputPort inPort = getInputPorts().createPort("through");
	private final OutputPort outPort = getOutputPorts().createPort("through");

	public AbstractLayer(OperatorDescription description) {
		super(description);
        remote = true;

		getTransformer().addRule(new PassThroughRule(inPort, outPort, false));
		getTransformer().addGenerationRule(outPort, LayerSemaphore.class);
	}

	public boolean isLinked() throws UserError {
		LayerSemaphore semaphore = inPort.getDataOrNull(LayerSemaphore.class);
		return (semaphore != null) && (semaphore.getClass() == LayerSemaphore.class);
	}

	@Override
	public void doWork() throws OperatorException {
		super.doWork();
		outPort.deliver(inPort.getDataOrNull(LayerSemaphore.class));
	}
}