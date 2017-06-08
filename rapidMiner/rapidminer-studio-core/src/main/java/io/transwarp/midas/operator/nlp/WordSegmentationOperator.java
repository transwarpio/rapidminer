package io.transwarp.midas.operator.nlp;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.features.WordSegmentationParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class WordSegmentationOperator extends BaseOp {
    private InputPort input = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");

    public WordSegmentationOperator(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType category = new ParameterTypeDouble(WordSegmentationParams.ratio(),
                "Specifies the source of word embedding.", 0, 1, 1);
        category.setExpert(false);
        types.add(category);

        ParameterType input = new ParameterTypeString(
                WordSegmentationParams.inputColumn(), "input column");
        input.setExpert(false);
        types.add(input);

        ParameterType output = new ParameterTypeString(
                WordSegmentationParams.outputColumn(), "output column");
        output.setExpert(false);
        types.add(output);
        return types;
    }
}
