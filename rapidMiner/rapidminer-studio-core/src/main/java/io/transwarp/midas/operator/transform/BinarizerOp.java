package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import io.transwarp.midas.constant.midas.params.preprocess.BinarizerParams;

import java.util.List;

public class BinarizerOp extends SelectAttributeOp {

    public BinarizerOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeDouble(BinarizerParams.threshold(),
                "The features greater than the threshold, will be binarized to 1.0." +
                "The features equal to or less than the threshold, will be binarized to 0.0. ",
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0);
        types.add(type);
        return types;
    }
}
