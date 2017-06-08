package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeLong;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.data.BalancedSampleParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viki on 17-3-31.
 */
public class BalancedSampleOp extends Operator {
    public BalancedSampleOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private final InputPort exampleSetInput = getInputPorts().createPort("input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("output");
    private final OutputPort originalOutput = getOutputPorts().createPort("original");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeString(BalancedSampleParams.labelColumn(),
                BalancedSampleParams.labelColDoc(), false);
        types.add(type);
        type = new ParameterTypeDouble(BalancedSampleParams.labelA(),
                BalancedSampleParams.aDoc(), 0.0, 1.0, 1.0);
        types.add(type);
        type = new ParameterTypeDouble(BalancedSampleParams.labelB(),
                BalancedSampleParams.bDoc(), 0.0, 1.0, 0.0);
        types.add(type);
        type = new ParameterTypeDouble(BalancedSampleParams.sampleRatio(),
                BalancedSampleParams.sampleRatioDoc(), 0.0, Double.POSITIVE_INFINITY, 1);
        types.add(type);
        type = new ParameterTypeLong(BalancedSampleParams.seed(),
                BalancedSampleParams.seedDoc(), -1L, Long.MAX_VALUE, -1L);
        types.add(type);

        return types;
    }
}
