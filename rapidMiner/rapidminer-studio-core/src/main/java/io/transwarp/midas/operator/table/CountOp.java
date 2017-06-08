package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

/**
 * Created by linchen on 16-9-7.
 */
public class CountOp extends BaseOp {
    private final InputPort exampleSetInput = getInputPorts().createPort("example set input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");
    private final OutputPort originalOutput = getOutputPorts().createPort("original");

    public CountOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        return super.getParameterTypes();

    }
}
