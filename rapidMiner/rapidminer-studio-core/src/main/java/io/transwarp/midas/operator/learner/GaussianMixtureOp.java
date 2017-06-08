package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.classification.GaussianMixtureParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.List;


public class GaussianMixtureOp extends BaseLearnerOp {
    public GaussianMixtureOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        // Parameter max iteration.
        ParameterType type = new ParameterTypeInt(
                GaussianMixtureParams.MaxIter(),
                "Maximum number of iterations.",
                1, Integer.MAX_VALUE, 20);
        type.setExpert(false);
        types.add(type);

        // Parameter K.
        type = new ParameterTypeInt(
                GaussianMixtureParams.k(),
                "The number of  clusters to infer.",
                1, Integer.MAX_VALUE, 2);
        type.setExpert(false);
        types.add(type);

        // Parameter tolerance.
        type = new ParameterTypeDouble(
                GaussianMixtureParams.Tol(),
                "The convergence tolerance for iterative algorithm",
                0, Double.MAX_VALUE, 0.01);
        type.setExpert(true);
        types.add(type);

        return types;
    }
}
