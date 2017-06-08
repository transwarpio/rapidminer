package io.transwarp.midas.operator.recommend;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.FactorizationMachinesParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 16-11-24.
 */
public class FactorizationMachinesOp extends BaseOp {
    public static final String PARAMETER_TASK = FactorizationMachinesParams.task();
    public static final String PARAMETER_OPTIMIZATION = FactorizationMachinesParams.solver();
    public static final String PARAMETER_INITIAL_STD = FactorizationMachinesParams.initialStd();
    public static final String PARAMETER_NUM_FACTORS = FactorizationMachinesParams.numFactors();
    public static final String PARAMETER_MAX_ITER = FactorizationMachinesParams.maxIter();
    public static final String PARAMETER_TOL = FactorizationMachinesParams.tol();
    public static final String PARAMETER_REG_0 = FactorizationMachinesParams.regParam0();
    public static final String PARAMETER_REG_1 = FactorizationMachinesParams.regParam1();
    public static final String PARAMETER_REG_2 = FactorizationMachinesParams.regParam2();
    public static final String PARAMTER_MINI_BATCH_FRACTION = FactorizationMachinesParams.miniBatchFraction();
    public static final String PARAMTER_STEP_SIZE = FactorizationMachinesParams.stepSize();
    public static final String PARAMTER_THRESHOLD = FactorizationMachinesParams.threshold();

    public static final String[] Types = new String[] {
            FactorizationMachinesParams.classification(), FactorizationMachinesParams.regression()};
    public static final String[] Solvers = new String[] {
            FactorizationMachinesParams.miniBatchGradientDescent(),
            FactorizationMachinesParams.parallelStochasticGradientDescent(),
            FactorizationMachinesParams.lbfgs()};

    private InputPort trainSetInput = getInputPorts().createPort("train set");
    private OutputPort modelOutput = getOutputPorts().createPort("model");
    private OutputPort Output = getOutputPorts().createPort("output");

    public FactorizationMachinesOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeCategory(PARAMETER_TASK, "regression or classification", FactorizationMachinesOp.Types, 0, false));
        types.add(new ParameterTypeCategory(PARAMETER_OPTIMIZATION, "gd", FactorizationMachinesOp.Solvers, 0, false));
        types.add(new ParameterTypeDouble(PARAMETER_INITIAL_STD,
                "The standard deviation for initializing weights", 0.0d, 1.0d, 0.01d, true));
        types.add(new ParameterTypeInt(PARAMETER_NUM_FACTORS, "Number of factors", 1, Integer.MAX_VALUE, 8, false));
        types.add(new ParameterTypeInt(PARAMETER_MAX_ITER, "maximum number of iterations", 1, Integer.MAX_VALUE, 5, true));
        types.add(new ParameterTypeDouble(PARAMETER_TOL, "tol", 0, Double.MAX_VALUE, 1e-4, true));
        types.add(new ParameterTypeDouble(PARAMETER_REG_0, "regParam0", 0, Double.MAX_VALUE, 0, true));
        types.add(new ParameterTypeDouble(PARAMETER_REG_1, "regParam1", 0, Double.MAX_VALUE, 1e-3, true));
        types.add(new ParameterTypeDouble(PARAMETER_REG_2, "regParam2", 0, Double.MAX_VALUE, 1e-4, true));
        types.add(new ParameterTypeDouble(PARAMTER_MINI_BATCH_FRACTION, "miniBatchFraction", 0, Double.MAX_VALUE, 1, true));
        types.add(new ParameterTypeDouble(PARAMTER_STEP_SIZE, "stepSize", 0, Double.MAX_VALUE, 0.01, false));
        types.add(new ParameterTypeDouble(PARAMTER_THRESHOLD, "threshold", 0, Double.MAX_VALUE, 0.5, false));
        return types;
    }
}
