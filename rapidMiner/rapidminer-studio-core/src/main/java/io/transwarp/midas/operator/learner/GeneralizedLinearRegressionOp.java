package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualStringCondition;
import io.transwarp.midas.constant.midas.params.regression.GeneralizedLinearRegressionParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.List;


public class GeneralizedLinearRegressionOp extends BaseLearnerOp {
    public GeneralizedLinearRegressionOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private static final String[] FAMILIES = {"gaussian", "binomial", "poisson", "gamma"};
    private static final String[] GAUSSIAN_LINKS = {"identity", "log", "inverse"};
    private static final String[] BINOMIAL_LINKS = {"logit", "probit", "cloglog"};
    private static final String[] POISSON_LINKS = {"log", "identity", "sqrt"};
    private static final String[] GAMMA_LINKS = {"inverse", "identity", "log"};
    private static final String[] SOLVERS = {"irls"};

    private static final String GAUSSIAN_LINK = "gaussianLink";
    private static final String BINOMIAL_LINK = "binomialLink";
    private static final String POISSON_LINK = "poissonLink";
    private static final String GAMMA_LINK = "gammaLink";

    private static final String FAMILY = GeneralizedLinearRegressionParams.family();


    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        // condition class
        ParameterType type = new ParameterTypeStringCategory(FAMILY,
                "The name of family which is description of the" +
                " error distribution to be used in the model.",FAMILIES,FAMILIES[0],false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeStringCategory(GAUSSIAN_LINK,"The name of link function which provides the relationship between" +
                " the linear predictor and the mean of the distribution function.",
                GAUSSIAN_LINKS,GAUSSIAN_LINKS[0],false);
        type.registerDependencyCondition(new EqualStringCondition(this, FAMILY, true, FAMILIES[0]));
        types.add(type);

        type = new ParameterTypeStringCategory(BINOMIAL_LINK,"The name of link function which provides the relationship between" +
                " the linear predictor and the mean of the distribution function.",
                BINOMIAL_LINKS,BINOMIAL_LINKS[0],false);
        type.registerDependencyCondition(new EqualStringCondition(this, FAMILY, true, FAMILIES[1]));
        types.add(type);

        type = new ParameterTypeStringCategory(POISSON_LINK,"The name of link function which provides the relationship between" +
                " the linear predictor and the mean of the distribution function.",
                POISSON_LINKS,POISSON_LINKS[0],false);
        type.registerDependencyCondition(new EqualStringCondition(this, FAMILY, true, FAMILIES[2]));
        types.add(type);

        type = new ParameterTypeStringCategory(GAMMA_LINK,"The name of link function which provides the relationship between" +
                " the linear predictor and the mean of the distribution function.",
                GAMMA_LINKS,GAMMA_LINKS[0],false);
        type.registerDependencyCondition(new EqualStringCondition(this, FAMILY, true, FAMILIES[3]));
        types.add(type);


        type = new ParameterTypeStringCategory(GeneralizedLinearRegressionParams.solver(),"The solver " +
                "algorithm for optimization.",
                SOLVERS,SOLVERS[0],false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(GeneralizedLinearRegressionParams.maxIter(), "Maximum number of " +
                "iterations.", 1, Integer.MAX_VALUE, 20);
        type.setExpert(false);
        types.add(type);


        type = new ParameterTypeDouble(GeneralizedLinearRegressionParams.tol(), "The convergence " +
                "tolerance for iterative algorithm", 0, Double.MAX_VALUE, 0.01);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(GeneralizedLinearRegressionParams.fitIntercept(), "Whether to fit an " +
                "intercept term.", false, false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(GeneralizedLinearRegressionParams.regParam(), "Regularization " +
                "Parameter (>=0).", 0.0, 1.0,0.0);
        type.setExpert(false);
        types.add(type);


        return types;
    }
}
