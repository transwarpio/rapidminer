package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;

public class GradientBoostedTreeRegressionOp extends GradientBoostedTreeOp {
    private static String[] IMPURITY_NAMES = {"variance"};

    public GradientBoostedTreeRegressionOp(OperatorDescription description) {
        super(description,IMPURITY_NAMES);
    }
}
