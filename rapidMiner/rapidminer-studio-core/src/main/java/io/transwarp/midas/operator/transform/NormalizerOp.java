package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import io.transwarp.midas.constant.midas.params.preprocess.NormalizerParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by endy on 16-8-4.
 */
public class NormalizerOp extends BaseOp {
    public NormalizerOp(OperatorDescription description) {
        super(description);
    }

    private InputPort trainSetInput = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeDouble(NormalizerParams.P(), "The p norm value",0,Double.MAX_VALUE,2,false);
        types.add(type);

        return types;
    }
}
