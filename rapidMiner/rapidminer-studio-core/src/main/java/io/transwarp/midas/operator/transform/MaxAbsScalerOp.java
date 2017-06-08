package io.transwarp.midas.operator.transform;


import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.preprocessing.normalization.Normalization;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttributes;
import io.transwarp.midas.constant.midas.params.preprocess.StandardScalerParams;

import java.util.LinkedList;
import java.util.List;

public class MaxAbsScalerOp extends Normalization {
    public MaxAbsScalerOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<>();
        ParameterType inputColumns = new ParameterTypeAttributes(StandardScalerParams.inputColumns(),
                "input column", getInputPort(), true, false);

        types.add(inputColumns);

        return types;
    }
}
