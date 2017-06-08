package io.transwarp.midas.operator.transform;


import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.data.OverSampleParams;

import java.util.ArrayList;
import java.util.List;

public class OverSampleOp extends Operator {
    public OverSampleOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private final InputPort exampleSetInput = getInputPorts().createPort("input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("output");
    private final OutputPort originalOutput = getOutputPorts().createPort("original");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeString(OverSampleParams.DependentColName(), "The " +
                "column that you use to sample the data", false);
        types.add(type);

        type = new ParameterTypeDouble(OverSampleParams.PrimaryClass(), "The class that you use " +
                "as base value", 0d,
                Double.MAX_VALUE, 0d);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(OverSampleParams.Threshold(),
                "The threshold that need to sample", 1d, Double.MAX_VALUE, 2d);
        types.add(type);

        return types;
    }
}
