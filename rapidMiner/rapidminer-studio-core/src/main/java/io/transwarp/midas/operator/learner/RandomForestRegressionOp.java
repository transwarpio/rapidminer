package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;

public class RandomForestRegressionOp extends RandomForestOp{
    private static final String[] IMPURITY_NAMES = {"variance"};

    public RandomForestRegressionOp(OperatorDescription description){
        super(description,IMPURITY_NAMES);
    }
}
