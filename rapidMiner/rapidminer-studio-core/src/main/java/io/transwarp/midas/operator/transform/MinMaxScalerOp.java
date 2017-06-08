package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.preprocessing.normalization.Normalization;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttributes;
import com.rapidminer.parameter.ParameterTypeDouble;
import io.transwarp.midas.constant.midas.params.preprocess.MinMaxScalerParams;
import io.transwarp.midas.constant.midas.params.preprocess.StandardScalerParams;

import java.util.LinkedList;
import java.util.List;


public class MinMaxScalerOp extends Normalization {

    public MinMaxScalerOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<>();
        ParameterType inputColumns = new ParameterTypeAttributes(StandardScalerParams.inputColumns(),
                "input column", getInputPort(), true, false);

        types.add(inputColumns);

        ParameterType type = new ParameterTypeDouble(MinMaxScalerParams.Min(),
                "lower bound of the output feature range",
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0);
        types.add(type);

        type = new ParameterTypeDouble(MinMaxScalerParams.Max(),
                "upper bound of the output feature range",
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0);
        types.add(type);
        return types;
    }
}
