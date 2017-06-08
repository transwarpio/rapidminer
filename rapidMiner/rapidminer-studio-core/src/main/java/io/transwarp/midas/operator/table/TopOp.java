package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.data.TopParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 16-10-10.
 */
public class TopOp extends BaseOp {
    private final InputPort exampleSetInput = getInputPorts().createPort("example set input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");

    public static final String PARAMETER_NUM_OF_ROWS = TopParams.numOfRows();

    public TopOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeInt(PARAMETER_NUM_OF_ROWS, "numOfRows", 0, Integer.MAX_VALUE, false);
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
