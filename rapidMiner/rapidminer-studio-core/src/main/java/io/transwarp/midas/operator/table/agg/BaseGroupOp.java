package io.transwarp.midas.operator.table.agg;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.table.AggParams;
import io.transwarp.midas.operator.BaseDataProcessOp;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseGroupOp extends BaseDataProcessOp {
    private static final String[] METHODS = new String[] {
            AggParams.avg(), AggParams.count(), AggParams.max(), AggParams.min(), AggParams.sum(), AggParams.collectSet(), AggParams.collectList()};

    public BaseGroupOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeEnumeration(AggParams.GroupByColumns(),
                "Attribute that groups the examples which form one example after pivoting.",
                new ParameterTypeAttribute("columns",
                        "Attribute which differentiates examples inside a group.", exampleSetInput, false, false), false));

        types.add(new ParameterTypeList(AggParams.Aggregations(),
                "This parameter defines aggregation columns and corresponding functions",
                new ParameterTypeAttribute("column",
                        "The name of the aggregation columns.",
                        exampleSetInput,false, false),
                new ParameterTypeStringCategory("aggregation method",
                        "The target aggregation functions",
                        METHODS, METHODS[0]), false));
        return types;
    }
}
