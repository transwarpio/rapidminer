package io.transwarp.midas.operator.random;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualStringCondition;

import java.util.ArrayList;
import java.util.List;


public class RandomGeneratorOp extends Operator {
    public RandomGeneratorOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private final OutputPort output = getOutputPorts().createPort("output");

    private static final String[] DISTRIBUTIONS =
            {"normal", "exponential", "gamma", "logNormal", "poisson", "uniform"};

    private static final String DISTRIBUTION = "distribution";
    private static final String NUM_ROWS = "numRows";
    private static final String NUM_COLUMNS = "numCols";
    private static final String EXPONENTIAL_MEAN = "exponentialMean";
    private static final String SHAPE = "shape";
    private static final String SCALE = "scale";
    private static final String LOG_NORMAL_MEAN = "logNormalMean";
    private static final String LOG_NORMAL_STD = "logNormalStd";
    private static final String POISSON_MEAN = "poissonMean";

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        // condition class
        ParameterType type = new ParameterTypeStringCategory(DISTRIBUTION, "The name of " +
                "distribution which to generate data.",DISTRIBUTIONS,DISTRIBUTIONS[0],false);
        type.setExpert(false);
        types.add(type);

        // exponential
        type = new ParameterTypeDouble(EXPONENTIAL_MEAN,"The mean of exponential distribution",
                Double.MIN_NORMAL, Double.MAX_VALUE, 0.5d, false);

        type.registerDependencyCondition(new EqualStringCondition(this, DISTRIBUTION, true,
                DISTRIBUTIONS[1]));
        types.add(type);


        // gamma
        type = new ParameterTypeDouble(SHAPE,"shape parameter (> 0) for the gamma distribution",
                Double.MIN_NORMAL, Double.MAX_VALUE, 0.1d, false);

        type.registerDependencyCondition(new EqualStringCondition(this, DISTRIBUTION, true,
                DISTRIBUTIONS[2]));
        types.add(type);

        type = new ParameterTypeDouble(SCALE,"scale parameter (> 0) for the gamma distribution",
                Double.MIN_NORMAL, Double.MAX_VALUE, 0.1d, false);

        type.registerDependencyCondition(new EqualStringCondition(this, DISTRIBUTION, true,
                DISTRIBUTIONS[2]));
        types.add(type);

        // log normal
        type = new ParameterTypeDouble(LOG_NORMAL_MEAN,"Mean of the log normal distribution",
                0.0d, Double.MAX_VALUE, 0.0d, false);

        type.registerDependencyCondition(new EqualStringCondition(this, DISTRIBUTION, true,
                DISTRIBUTIONS[3]));
        types.add(type);

        type = new ParameterTypeDouble(LOG_NORMAL_STD,"Standard deviation of the log normal distribution",
                Double.MIN_NORMAL, Double.MAX_VALUE, 0.1d, false);

        type.registerDependencyCondition(new EqualStringCondition(this, DISTRIBUTION, true,
                DISTRIBUTIONS[3]));
        types.add(type);

        // poisson
        type = new ParameterTypeDouble(POISSON_MEAN,"Mean for the Poisson distribution",
                Double.MIN_NORMAL, Double.MAX_VALUE, 0.1d, false);

        type.registerDependencyCondition(new EqualStringCondition(this, DISTRIBUTION, true,
                DISTRIBUTIONS[4]));
        types.add(type);


        type = new ParameterTypeLong(NUM_ROWS, "The number of rows",
                1, Long.MAX_VALUE, 100, false);
        types.add(type);

        type = new ParameterTypeInt(NUM_COLUMNS, "The number of columns",
                1, Integer.MAX_VALUE, 10, false);
        types.add(type);

        return types;
    }
}
