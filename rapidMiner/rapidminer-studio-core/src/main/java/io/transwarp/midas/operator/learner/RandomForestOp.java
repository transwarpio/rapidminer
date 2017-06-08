package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.learner.tree.ParallelRandomForestLearner;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.classification.DecisionTreeParams;
import io.transwarp.midas.constant.midas.params.classification.RandomForestParams;

import java.util.ArrayList;
import java.util.List;

public class RandomForestOp extends ParallelRandomForestLearner{
    private static String[] IMPURITY_NAMES ;

    public RandomForestOp(OperatorDescription description,String[] impurity){
        super(description);
        IMPURITY_NAMES = impurity;
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeStringCategory(RandomForestParams.Impurity(),
                "Specifies the used criterion for selecting attributes and numerical splits.", IMPURITY_NAMES,
                IMPURITY_NAMES[0], false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(RandomForestParams.NumTrees(), "The number of learned random trees.", 1,
                Integer.MAX_VALUE, 10);
        type.setExpert(false);
        types.add(type);


        type = new ParameterTypeInt(RandomForestParams.MaxDepth(), "The maximum tree depth (-1: no bound)",
                -1, Integer.MAX_VALUE,
                5);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(RandomForestParams.MinInfoGain(),
                "The minimal gain which must be achieved in order to produce a split.", 0.0d, Double.POSITIVE_INFINITY, 0.1d);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(RandomForestParams.SubsamplingRate(), "Ratio of randomly chosen " +
                "attributes" +
                " to test", 0.0d, 1.0d,
                0.2d);
        type.setExpert(false);
        types.add(type);


        type = new ParameterTypeInt(RandomForestParams.MaxBins(), "The maximum bins num ", 1, Integer
                .MAX_VALUE,
                32);
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeInt(RandomForestParams.MinInstancePerNode(),
                "The minimal size of a node in order to allow a split.", 1, Integer.MAX_VALUE, 4);
        types.add(type);

        types.add(new ParameterTypeInt(DecisionTreeParams.MaxCategories(), "Max categories",
                2, Integer.MAX_VALUE, 20));

        return types;
    }
}
