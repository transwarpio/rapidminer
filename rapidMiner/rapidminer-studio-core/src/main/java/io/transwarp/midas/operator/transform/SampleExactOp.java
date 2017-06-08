package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.data.SampleExactParams;

import java.util.ArrayList;
import java.util.List;


public class SampleExactOp extends Operator {
    public SampleExactOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private static final String PARAMETER_WITH_REPLACEMENT = SampleExactParams.WithReplacement();
    private static final String PARAMETER_SAMPLE_SIZE = SampleExactParams.SampleSize();

    protected final InputPort exampleSetInput = getInputPorts().createPort("input");
    protected final OutputPort exampleSetOutput = getOutputPorts().createPort("output");
    protected final OutputPort originalOutput = getOutputPorts().createPort("original");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType type = new ParameterTypeBoolean(PARAMETER_WITH_REPLACEMENT,
                "sampling with replacement or not", false, true);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_SAMPLE_SIZE, "The size of examples which should " +
                "be sampled", 0,
                Integer.MAX_VALUE, 100);
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
