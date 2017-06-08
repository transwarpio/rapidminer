package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.xgboost.GeneralParams;
import io.transwarp.midas.constant.midas.params.xgboost.TreeBoosterParams;
import io.transwarp.midas.operator.BaseLearnerOp;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class BoostMultiClassClassifierOp extends BaseLearnerOp {

    private String[] supportedTreeMethods = {"auto", "exact", "approx"};

    public BoostMultiClassClassifierOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeInt(GeneralParams.NumRound(), "The number of rounds" +
                " for boosting", 1, Integer.MAX_VALUE, 1);
        types.add(type);

        type = new ParameterTypeInt(GeneralParams.nWorkers(),
                "number of workers used to run xgboost", 1, Integer.MAX_VALUE, 1);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.Eta(),
                "Step size shrinkage used in update to prevents overfitting.",
                0, 1, 0.3);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.Gamma(),
                "Minimum loss reduction required to make a further partition on a" +
                        " leaf node of the tree.", 0, Double.POSITIVE_INFINITY, 0);
        types.add(type);

        type = new ParameterTypeInt(TreeBoosterParams.MaxDepth(),
                "maximum depth of a tree", 1, Integer.MAX_VALUE, 6);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.MinChildWeight(),
                "number of workers used to run xgboost", 0, Double.POSITIVE_INFINITY, 1);
        types.add(type);


        type = new ParameterTypeDouble(TreeBoosterParams.MaxDeltaStep(),
                "Maximum delta step we allow each tree’s weight estimation to be.", 0, Double
                .POSITIVE_INFINITY, 0);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.SubSample(),
                "Subsample ratio of the training instance.", Double.MIN_NORMAL, 1, 1);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.ColSampleByTree(),
                "Subsample ratio of columns when constructing each tree.", Double.MIN_NORMAL, 1, 1);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.ColSampleByLevel(),
                "Subsample ratio of columns for each split, in each level.", Double.MIN_NORMAL,
                1, 1);
        types.add(type);


        type = new ParameterTypeDouble(TreeBoosterParams.Lambda(),
                "L2 regularization term on weights, increase this value will make model" +
                        " more conservative.", 0, Double.POSITIVE_INFINITY, 1);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.Alpha(),
                "L1 regularization term on weights, increase this value will make model" +
                        " more conservative.", 0, Double.POSITIVE_INFINITY, 0);
        types.add(type);

        type = new ParameterTypeStringCategory(TreeBoosterParams.TreeMethod(),
                "The tree construction algorithm.", supportedTreeMethods,
                supportedTreeMethods[0]);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.SketchEps(),
                "The parameter used for approximate greedy algorithm.", Double.MIN_NORMAL,
                0.9999, 0.03);
        types.add(type);

        type = new ParameterTypeDouble(TreeBoosterParams.ScalePosWeight(),
                "Control the balance of positive and negative weights, useful for" +
                        " unbalanced classes.", 0, Double.POSITIVE_INFINITY, 0);
        types.add(type);

        return types;
    }
}
