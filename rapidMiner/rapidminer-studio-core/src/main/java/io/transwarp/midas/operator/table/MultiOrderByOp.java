package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.data.OrderByParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

/**
 * Created by viki on 17-1-12.
 */
public class MultiOrderByOp extends BaseOp {
    private final InputPort exampleSetInput = getInputPorts().createPort("example set input");
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");

    public MultiOrderByOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = new ParameterTypeEnumeration(
                OrderByParams.orderByCol(),
                "Columns to sort. The former column will has the priority when sorting",
                new ParameterTypeAttribute("Columns", "Columns to sort", exampleSetInput));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(OrderByParams.descending(), "Whether" +
                "order by descending", false, false);
        types.add(type);

        return types;
    }
}
