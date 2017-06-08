package io.transwarp.midas.operator.macros;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import io.transwarp.midas.operator.BaseOp;

public class BaseMacroOp extends BaseOp {
    protected final InputPort exampleSetInput = getInputPorts().createPort("through");
    protected final OutputPort exampleSetOutput = getOutputPorts().createPort("through");
    public BaseMacroOp(OperatorDescription description) {
        super(description);
    }
}
