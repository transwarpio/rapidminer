package io.transwarp.midas.operator.nlp;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.classification.NWIParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 17-4-24.
 */
public class NWIOpeartor extends BaseOp {

    private InputPort exampleSetInput = getInputPorts().createPort("example set");
    private OutputPort output = getOutputPorts().createPort("output");

    public NWIOpeartor(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {

        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeInt(NWIParams.MaxWordLength(),
                "word.length.max", 1, Integer.MAX_VALUE, 6, false));


        types.add(new ParameterTypeDouble(NWIParams.PMI(), "filter.pmi", 0, Double.MAX_VALUE, 1));

        types.add(new ParameterTypeDouble(NWIParams.ENTROPHY(), "filter.entrophy", 0, Double.MAX_VALUE, 0.5));

        types.add(new ParameterTypeInt(NWIParams.FREQUENCY(), "filter.frequency", 0, Integer.MAX_VALUE, 5));

        return types;
    }
}
