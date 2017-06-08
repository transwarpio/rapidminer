package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.preprocessing.PreprocessingOperator;
import com.rapidminer.operator.preprocessing.filter.NominalToBinominal;
import com.rapidminer.operator.preprocessing.normalization.Normalization;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttributes;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import io.transwarp.midas.constant.midas.params.data.StringIndexerParams;
import io.transwarp.midas.constant.midas.params.preprocess.StandardScalerParams;

import java.util.LinkedList;
import java.util.List;

public class StandardScalerOp extends Normalization {

    public StandardScalerOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<>();
        ParameterType inputColumns = new ParameterTypeAttributes(StandardScalerParams.inputColumns(),
                "input column", getInputPort(), true, false);

        types.add(inputColumns);

        ParameterType type = new ParameterTypeBoolean(StandardScalerParams.WithStd(), "Scale to unit standard deviation",false,false);
        types.add(type);

        type = new ParameterTypeBoolean(StandardScalerParams.WithMean(), "Center data with mean",true,false);
        types.add(type);

        return types;
    }

}
