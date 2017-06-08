package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import io.transwarp.midas.constant.midas.params.DCTParams;

import java.util.ArrayList;
import java.util.List;


public class DCTOp extends Operator{
    public DCTOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private InputPort input = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeBoolean(DCTParams.Inverse(), "Indicates whether to " +
                "perform the inverse DCT (true) or forward DCT (false). ", false, false);
        types.add(type);

        return types;
    }
}
