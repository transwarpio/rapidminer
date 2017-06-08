package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.regression.AFTSuvivalRegressionParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class AFTSurvivalRegressionOp extends BaseLearnerOp {
    public AFTSurvivalRegressionOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private static final String[] keys={"1 quantile","2 quantile","3 quantile","4 quantile",
            "5 quantile","6 quantile","7 quantile","8 quantile","9 quantile"};
    private static final String[] probabilities = {"0.01", "0.05", "0.1", "0.25", "0.5", "0.75", "0.9", "0.95", "0.99"};


    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeInt(AFTSuvivalRegressionParams.maxIter(), "Maximum number " +
                "of iterations.", 1, Integer.MAX_VALUE, 20);
        type.setExpert(false);
        types.add(type);


        type = new ParameterTypeDouble(AFTSuvivalRegressionParams.tol(), "The convergence tolerance " +
                "for iterative algorithm", 0, Double.MAX_VALUE, 0.01);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(AFTSuvivalRegressionParams.fitIntercept(), "Whether to fit an " +
                "intercept term.", false, false);
        type.setExpert(false);
        types.add(type);

        List<String[]> lists = new LinkedList<>();
        for(int i =0;i<keys.length;i++) {
            lists.add(new String[]{keys[i],probabilities[i]});
        }

        type = new ParameterTypeList(AFTSuvivalRegressionParams.quantileProbabilities(),
                "Quantile Probabilities",
                new ParameterTypeString("key","key"),
                new ParameterTypeString("probability","Quantile Probability"),
                lists, true);
        types.add(type);

        type = new ParameterTypeString(AFTSuvivalRegressionParams.quantilesCol(), "quantiles column name",
                "quantile", true);
        types.add(type);

        return types;
    }
}
