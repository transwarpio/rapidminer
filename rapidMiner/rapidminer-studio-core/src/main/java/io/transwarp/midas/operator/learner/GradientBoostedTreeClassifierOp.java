package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;


public class GradientBoostedTreeClassifierOp extends GradientBoostedTreeOp {
    private static final String[] IMPURITY_NAMES = {"gini", "entropy"};

    public GradientBoostedTreeClassifierOp(OperatorDescription description) {
        super(description,IMPURITY_NAMES);
        remote = true;
    }
}
