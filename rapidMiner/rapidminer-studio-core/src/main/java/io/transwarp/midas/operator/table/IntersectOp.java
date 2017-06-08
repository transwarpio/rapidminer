package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 16-10-12.
 */
public class IntersectOp extends BaseOp {
    private final InputPort exampleSetInput = getInputPorts().createPort("example set input");
    private final InputPort secondInput = getInputPorts().createPort("second input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");
    private final OutputPort originalOutput = getOutputPorts().createPort("original");

    public IntersectOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        return types;
    }
}
