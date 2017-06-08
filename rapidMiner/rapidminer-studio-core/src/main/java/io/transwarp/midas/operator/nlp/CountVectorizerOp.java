package io.transwarp.midas.operator.nlp;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.features.CountVectorizerParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 17-6-2.
 */
public class CountVectorizerOp extends BaseOp {
    public CountVectorizerOp(OperatorDescription description) {
        super(description);
    }

    private InputPort input = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");
    private OutputPort model = getOutputPorts().createPort("model");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeInt(CountVectorizerParams.vocabSize(),
                "Max size of the vocabulary.",
                1, Integer.MAX_VALUE, (int)Math.pow(2, 18),false));

        types.add(new ParameterTypeDouble(CountVectorizerParams.minDF(),
                "Specifies the minimum number of different documents a term must appear in" +
                        "to be included in the vocabulary.", 0, Double.MAX_VALUE, 1, false));

        types.add(new ParameterTypeDouble(CountVectorizerParams.minTF(), "Filter to ignore rare words in a document.",
                0, Double.MAX_VALUE, 1, false));

        types.add(new ParameterTypeBoolean(CountVectorizerParams.binary(), "Binary toggle to control the output vector values.",
                false, false));


        types.add(new ParameterTypeString(CountVectorizerParams.inputColumn(), "input column"));

        types.add(new ParameterTypeString(CountVectorizerParams.outputColumn(), "output column"));
        return types;
    }
}
