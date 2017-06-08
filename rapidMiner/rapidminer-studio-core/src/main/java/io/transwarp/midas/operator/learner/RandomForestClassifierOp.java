package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;

public class RandomForestClassifierOp extends RandomForestOp {
    private static String[] IMPURITY_NAMES = {"gini", "entropy"};

    public RandomForestClassifierOp(OperatorDescription description){
        super(description,IMPURITY_NAMES);
    }
}
