package io.transwarp.midas.operator.features;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.features.TFParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 17-6-6.
 */
public class TFOp extends BaseOp {
    public TFOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private InputPort input = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeString(TFParams.inputColumn(), "input column"));

        types.add(new ParameterTypeString(TFParams.outputColumn(), "output column"));

        types.add(new ParameterTypeInt(TFParams.numFeatures(),
                "num features", 1, Integer.MAX_VALUE, (int)Math.pow(2, 20)));

        types.add(new ParameterTypeBoolean(TFParams.binary(), "binary", false));

        return types;
    }
}
