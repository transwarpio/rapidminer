package io.transwarp.midas.operator.outlier;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.outlier.LOFParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class LOFOp extends BaseOp {
    public LOFOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private final InputPort exampleSetInput = getInputPorts().createPort("example set");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeInt(LOFParams.minPts(),
                "The minimum number of points", 1, Integer.MAX_VALUE, 5));
        return types;
    }
}
