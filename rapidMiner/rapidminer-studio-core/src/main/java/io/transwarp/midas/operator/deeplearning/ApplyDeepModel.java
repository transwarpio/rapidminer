package io.transwarp.midas.operator.deeplearning;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

public class ApplyDeepModel extends BaseOp {
    private final InputPort modelInput = getInputPorts().createPort("model");
    private final InputPort exampleSetInput = getInputPorts().createPort("unlabelled data");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("labelled data");
    private final OutputPort modelOutput = getOutputPorts().createPort("model");

    public ApplyDeepModel(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}
