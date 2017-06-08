package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualStringCondition;
import io.transwarp.midas.constant.midas.params.LDAParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.List;


public class LDAOp extends BaseLearnerOp {
    public LDAOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        // Parameter optimizer.
        ParameterType type = new ParameterTypeStringCategory(
                LDAParams.Optimizer(),
                "Optimizer or inference algorithm used" +
                " to estimate the LDA model.",
                LDAParams.OptimizerOptions(),
                LDAParams.OptimizerOptions()[0],
                false);
        type.setExpert(false);
        types.add(type);

        // Parameter max iteration.
        type = new ParameterTypeInt(
                LDAParams.MaxIter(),
                "Maximum number of iterations.",
                1, Integer.MAX_VALUE, 20);
        type.setExpert(false);
        types.add(type);

        // Parameter K.
        type = new ParameterTypeInt(
                LDAParams.k(),
                "The number of topics (clusters) to infer.",
                1, Integer.MAX_VALUE, 10);
        type.setExpert(false);
        types.add(type);

        // Parameter doc concentration.
        type = new ParameterTypeDouble(
                LDAParams.DocConcentration(),
                "Concentration parameter (commonly named 'alpha') " +
                        "for the prior placed on documents distributions" +
                        "over topics ('theta').",
                0.0, Integer.MAX_VALUE, 2.0);
        type.setExpert(true);
        types.add(type);

        // Parameter topic concentration.
        type = new ParameterTypeDouble(
                LDAParams.TopicConcentration(),
                "Concentration parameter (commonly named 'beta') " +
                        "for the prior placed on documents distributions" +
                        "over terms.",
                0.0, Integer.MAX_VALUE, 2.0);
        type.setExpert(true);
        types.add(type);

        // Parameter learning offset.
        type = new ParameterTypeDouble(
                LDAParams.LearningOffset(),
                "(For online optimizer) A positive learning parameter" +
                        "that down-weights early iterations. Large value" +
                        "make early iterations count less.",
                0.0, Integer.MAX_VALUE, 1024d, true);
        type.registerDependencyCondition(
                new EqualStringCondition(
                        this, LDAParams.Optimizer(),
                        true, LDAParams.OptimizerOptions()[1]));
        types.add(type);

        // Parameter learning decay.
        type = new ParameterTypeDouble(
                LDAParams.LearningDecay(),
                "(For online optimizer) Learning rate, set as an exponential" +
                        "decay rate. this should between (0.5,1.0].",
                0.51, 1.0, 0.51, true);
        type.registerDependencyCondition(
                new EqualStringCondition(
                        this, LDAParams.Optimizer(),
                        true, LDAParams.OptimizerOptions()[1]));
        types.add(type);

        // Parameter sub-sampling rate.
        type = new ParameterTypeDouble(
                LDAParams.SubsamplingRate(),
                "(For online optimizer) Fraction of the corpus to be sampled" +
                        "and  used in each iteration of min-batch gradient" +
                        "descent , in range (0,1].",
                0.001, 1.0, 0.05, true);
        type.registerDependencyCondition(
                new EqualStringCondition(
                        this, LDAParams.Optimizer(),
                        true, LDAParams.OptimizerOptions()[1]));
        types.add(type);

        // Parameter optimize doc concentration.
        type = new ParameterTypeBoolean(
                LDAParams.OptimizeDocConcentration(),
                "(For online optimizer) Indicates whether the docConcentration" +
                " will be optimized during training.",
                false, true);
        type.registerDependencyCondition(
                new EqualStringCondition(
                        this, LDAParams.Optimizer(),
                        true, LDAParams.OptimizerOptions()[1]));
        types.add(type);

        return types;
    }


}
