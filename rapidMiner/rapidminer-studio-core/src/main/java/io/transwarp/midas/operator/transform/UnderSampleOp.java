package io.transwarp.midas.operator.transform;


import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.data.UnderSampleParams;

import java.util.ArrayList;
import java.util.List;

public class UnderSampleOp extends Operator {
    public UnderSampleOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private final InputPort exampleSetInput = getInputPorts().createPort("input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("output");
    private final OutputPort originalOutput = getOutputPorts().createPort("original");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeString(UnderSampleParams.DependentColName(), "The " +
                "column that you use to sample the data", false);
        types.add(type);

        type = new ParameterTypeDouble(UnderSampleParams.PrimaryClass(), "The class that you use " +
                "as base value", 0d,
                Double.MAX_VALUE, 0d);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(UnderSampleParams.Threshold(),
                "The threshold that need to sample", 1d, Double.MAX_VALUE, 2d);
        types.add(type);

        type = new ParameterTypeBoolean(UnderSampleParams.WithReplacement(),
                "with replacement", false);
        types.add(type);

        return types;
    }
}
