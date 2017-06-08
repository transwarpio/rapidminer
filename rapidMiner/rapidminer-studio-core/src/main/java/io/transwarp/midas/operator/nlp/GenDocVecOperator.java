package io.transwarp.midas.operator.nlp;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.features.ApplyWord2VecParams;
import io.transwarp.midas.constant.midas.params.features.GenDocVecParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 17-5-12.
 */
public class GenDocVecOperator extends BaseOp {
    private InputPort exampleSetInput = getInputPorts().createPort("example set");
    private OutputPort output = getOutputPorts().createPort("output");

    public GenDocVecOperator(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType category = new ParameterTypeStringCategory(GenDocVecParams.method(),
                "method", GenDocVecParams.supportedMethods(),
                GenDocVecParams.supportedMethods()[0], false);
        category.setExpert(false);
        types.add(category);

        ParameterType input = new ParameterTypeString(
                GenDocVecParams.inputColumn(), "input column");
        input.setExpert(false);
        types.add(input);

        ParameterType output = new ParameterTypeString(
                GenDocVecParams.outputColumn(), "output column");
        output.setExpert(false);
        types.add(output);

        return types;
    }
}
