package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import io.transwarp.midas.constant.midas.params.regression.IsotonicRegressionParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.List;


public class IsotonicRegressionOp extends BaseLearnerOp {
    public IsotonicRegressionOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeBoolean(IsotonicRegressionParams.isotonic(), "Whether the " +
                "output sequence should be isotonic/increasing (true)" +
                " or antitonic/decreasing (false).", true, false);
        types.add(type);

        return types;
    }
}
