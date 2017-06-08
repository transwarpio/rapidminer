package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.data.ReplaceDataParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 16-10-19.
 */
public class ReplaceDataOp extends BaseOp {
    protected static final String LEFT_EXAMPLE_SET_INPUT = "left";
    protected static final String RIGHT_EXAMPLE_SET_INPUT = "right";

    private InputPort leftInput = getInputPorts().createPort(LEFT_EXAMPLE_SET_INPUT);
    private InputPort rightInput = getInputPorts().createPort(RIGHT_EXAMPLE_SET_INPUT);
    private OutputPort joinOutput = getOutputPorts().createPort("example set output");

    private static final String PARAMETER_REPLACE_COLUMN = ReplaceDataParams.ReplaceCol();
    public ReplaceDataOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeString(PARAMETER_REPLACE_COLUMN, "replaceColumn",
                false);
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
