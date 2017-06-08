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

public class BivariateCateVsCateFeatureStatisticOp extends Operator {
    public BivariateCateVsCateFeatureStatisticOp(OperatorDescription description) {
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

        types.add(new ParameterTypeBoolean(BivariateParams.pearsonChiSq(), "Pearson's " +
                "chi square : A measure of how much the frequencies of value pairs of two " +
                "categorical features deviate from statistical independence.",
                true,
                false));

        types.add(new ParameterTypeBoolean(BivariateParams.degreesOfFreedom(), "An integer " +
                "parameter required for the interpretation of Pearson's chi square measure.",
                true,
                false));

        types.add(new ParameterTypeBoolean(BivariateParams.pValueOfPearsonChiSq(), "An measure of" +
                " how likely we would observe the current frequencies of  value pairs of two " +
                "categorical features assuming their statistical independence.",
                true,
                false));

        types.add(new ParameterTypeBoolean(BivariateParams.cramerV(), "An measure for the " +
                "strength of association ,i.e. of statistical dependence, between two " +
                "categorical features,conceptually similar to Pearson's correlation coefficient.",
                true,
                false));

        return types;
    }
}
