package io.transwarp.midas.operator.table.agg;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.table.AggParams;
import io.transwarp.midas.operator.BaseDataProcessOp;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class AggregationOp extends BaseDataProcessOp {
    private static final String[] METHODS = new String[] {
            AggParams.avg(), AggParams.count(), AggParams.max(), AggParams.min(), AggParams.sum()};

    public AggregationOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeList(AggParams.Aggregations(),
                "This parameter defines aggregation columns and corresponding functions",
                new ParameterTypeAttribute("columns",
                        "The name of the aggregation columns.",
                        exampleSetInput, false, false),
                new ParameterTypeStringCategory("aggregation method",
                        "The target aggregation functions",
                        METHODS, METHODS[0]), false));
        return types;
    }
}
