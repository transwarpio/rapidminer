package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.preprocessing.normalization.Normalization;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttributes;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.preprocess.QuantileDiscretizerParams;
import io.transwarp.midas.constant.midas.params.preprocess.StandardScalerParams;

import java.util.LinkedList;
import java.util.List;


public class QuantileDiscretizerOp extends Normalization {
    public QuantileDiscretizerOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {

        List<ParameterType> types = new LinkedList<>();
        ParameterType inputColumns = new ParameterTypeAttributes(StandardScalerParams.inputColumns(),
                "input column", getInputPort(), true, false);

        types.add(inputColumns);

        ParameterType type = new ParameterTypeInt(QuantileDiscretizerParams.NumBuckets(), "Maximum number of " +
                "buckets (quantiles, or categories) into which data points are grouped. Must be " +
                ">= 2. ", 2, Integer.MAX_VALUE, 2);
        type.setExpert(false);

        types.add(type);

        type = new ParameterTypeDouble(QuantileDiscretizerParams.RelativeError(), "The relative " +
                "target precision for the approximate quantile algorithm used to generate buckets" +
                ". Must be in the range [0, 1].", 0.0, 1.0, 0.001);
        type.setExpert(true);

        types.add(type);


        return types;
    }
}
