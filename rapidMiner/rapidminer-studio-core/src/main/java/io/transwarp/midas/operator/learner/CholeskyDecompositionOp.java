package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

/**
 * Created by linchen on 16-9-27.
 */
public class CholeskyDecompositionOp extends BaseOp {

    private final InputPort exampleSetInput = getInputPorts().createPort("training set");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
    private final OutputPort original = getOutputPorts().createPort("original");

    public static final String PARAMETER_UPORLO = "UpOrLo";
    public static final String[] Types = new String[] {"lower", "upper"};


    public CholeskyDecompositionOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeCategory(PARAMETER_UPORLO, "Upper or lower triangle matrix", CholeskyDecompositionOp.Types, 0));
        return types;
    }
}
