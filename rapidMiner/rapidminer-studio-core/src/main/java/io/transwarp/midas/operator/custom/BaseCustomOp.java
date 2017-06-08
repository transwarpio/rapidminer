package io.transwarp.midas.operator.custom;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPortExtender;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.CustomParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCustomOp extends BaseOp {
    private final InputPortExtender inputExtender = new InputPortExtender("input", getInputPorts());
    private final OutputPortExtender outputExtender = new OutputPortExtender("output", getOutputPorts());

    public BaseCustomOp(OperatorDescription description) {
        super(description);
        inputExtender.start();
        outputExtender.start();
    }
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeList(CustomParams.Params(), "parameters",
                new ParameterTypeString("key", "key"),
                new ParameterTypeString("value", "value"));
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
