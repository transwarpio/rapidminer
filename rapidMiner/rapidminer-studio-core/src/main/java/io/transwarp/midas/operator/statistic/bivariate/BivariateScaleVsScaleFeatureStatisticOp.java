package io.transwarp.midas.operator.statistic.bivariate;


import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.stats.BivariateParams;

import java.util.ArrayList;
import java.util.List;

public class BivariateScaleVsScaleFeatureStatisticOp extends Operator {

    public BivariateScaleVsScaleFeatureStatisticOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private InputPort trainSetInput = getInputPorts().createPort("input");
    private OutputPort output = getOutputPorts().createPort("output");

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeString(BivariateParams.firstColumnName(),
                "The first column choice to compute bivariate scale vs scale feature  statistic",
                false,
                false));
        types.add(new ParameterTypeString(BivariateParams.secondColumnName(),
                "The second column choice to compute bivariate scale vs scale feature  statistic",
                false,
                false));

        types.add(new ParameterTypeBoolean(BivariateParams.pearsonCorCoe(), "Pearson's " +
                "correlation coefficient: A measure of linear dependence between two numerical " +
                "features",
                true,
                false));

        return types;
    }
}
