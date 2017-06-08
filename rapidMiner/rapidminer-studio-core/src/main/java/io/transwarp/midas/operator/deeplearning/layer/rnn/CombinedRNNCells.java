package io.transwarp.midas.operator.deeplearning.layer.rnn;

import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.SubprocessTransformRule;

public class CombinedRNNCells extends OperatorChain {

    // Ports
    protected OutputPort outPort = getOutputPorts().createPort("through");
    protected InputPort inPort = getInputPorts().createPort("through");
    protected final OutputPort start = getSubprocess(0).getInnerSources().createPort("start");
    protected final InputPort end = getSubprocess(0).getInnerSinks().createPort("end");

    public CombinedRNNCells(OperatorDescription description) {
        super(description, "Combined RNN Cells");
        remote = true;
        getTransformer().addRule(new SubprocessTransformRule(getSubprocess(0)));
    }
}
