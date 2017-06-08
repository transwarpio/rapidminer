package io.transwarp.midas.operator.nlp;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.features.ApplyWord2VecParams;
import io.transwarp.midas.constant.midas.params.features.WordSegmentationParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 17-5-9.
 */
public class ApplyWord2VecOperator extends BaseOp {
    private InputPort exampleSetInput = getInputPorts().createPort("example set");
    private InputPort secondInput = getInputPorts().createPort("second input");
    private OutputPort output = getOutputPorts().createPort("output");

    private static final String[] source = {ApplyWord2VecParams.useWordEmbeddingFromInputPort(), ApplyWord2VecParams.usePreTrainedWordEmbedding()};

    public ApplyWord2VecOperator(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType category = new ParameterTypeStringCategory(ApplyWord2VecParams.source(),
                "Specifies the source of word embedding.", source,
                source[0], false);
        category.setExpert(false);
        types.add(category);

        ParameterType input = new ParameterTypeString(
                ApplyWord2VecParams.inputColumn(), "input column");
        input.setExpert(false);
        types.add(input);

        ParameterType output = new ParameterTypeString(
                ApplyWord2VecParams.outputColumn(), "output column");
        output.setExpert(false);
        types.add(output);

        return types;
    }
}
