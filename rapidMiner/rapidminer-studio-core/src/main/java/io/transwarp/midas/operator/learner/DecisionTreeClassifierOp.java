package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;


public class DecisionTreeClassifierOp extends DecisionTreeOp {
    private static final String[] IMPURITY_NAMES = {"gini", "entropy"};

    public DecisionTreeClassifierOp(OperatorDescription description) {
        super(description,IMPURITY_NAMES);
    }
}
