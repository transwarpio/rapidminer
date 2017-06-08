package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeText;
import io.transwarp.midas.constant.midas.params.features.GenerateIDFeatureParams;

import java.util.ArrayList;
import java.util.List;


public class GenerateIDFeatureOp extends Operator {
    public GenerateIDFeatureOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private InputPort input = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeString(GenerateIDFeatureParams.GeneratedColumnName(),
                "The name of generated column","id",false);

        types.add(type);
        return types;
    }

}
