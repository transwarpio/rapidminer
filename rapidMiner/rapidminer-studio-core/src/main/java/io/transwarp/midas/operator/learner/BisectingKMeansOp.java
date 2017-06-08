package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.clusting.BisectingKMeansParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.List;

public class BisectingKMeansOp extends BaseLearnerOp {
    public BisectingKMeansOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        // Parameter max iteration.
        ParameterType type = new ParameterTypeInt(
                BisectingKMeansParams.MaxIter(),
                "Maximum number of iterations.",
                1, Integer.MAX_VALUE, 20);
        type.setExpert(false);
        types.add(type);

        // Parameter k.
        type = new ParameterTypeInt(
                BisectingKMeansParams.k(),
                "The number of  clusters to infer.",
                1, Integer.MAX_VALUE, 2);
        type.setExpert(false);
        types.add(type);

        // Parameter min divisible cluster size.
        type = new ParameterTypeDouble(
                BisectingKMeansParams.MinDivisibleClusterSize(),
                "The minimum number of points (if >=1.0) or the minimum" +
                        "proportion of points (if < 1.0) of a divisible cluster",
                1, Double.MAX_VALUE, 1.0);
        type.setExpert(false);
        types.add(type);

        return types;
    }


}
