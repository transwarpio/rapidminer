package io.transwarp.midas.operator.features;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.features.IDFParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 17-6-6.
 */
public class IDFOp extends BaseOp {
    public IDFOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private InputPort input = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");
    private OutputPort model = getOutputPorts().createPort("model");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeString(IDFParams.inputColumn(), "input column"));

        types.add(new ParameterTypeString(IDFParams.outputColumn(), "output column"));

        types.add(new ParameterTypeInt(IDFParams.minDocFreq(),
                "minDocFreq", 1, Integer.MAX_VALUE, 0));

        return types;
    }
}
