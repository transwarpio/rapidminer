/**
 * Copyright (C) 2016 Transwarp Technology(Shanghai ) Co., Ltd.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.classification.GLMParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.List;

public class LinearRegressionOp extends BaseLearnerOp {

    public LinearRegressionOp(OperatorDescription description) {
        super(description);
		remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

		types.add(new ParameterTypeInt(GLMParams.MaxIter(),
				"the maximum number of iterations", 1, Integer.MAX_VALUE, 100, false));

		types.add(new ParameterTypeDouble(GLMParams.Tol(),
				"The convergence tolerance of iterations. Smaller value will lead to higher accuracy with the cost of more iterations.",
				Double.MIN_VALUE,
				Double.MAX_VALUE,
				1E-6));

		types.add(new ParameterTypeDouble(GLMParams.RegParam(), "regularization parameter", 0, Double
				.MAX_VALUE, 0.0));

		types.add(new ParameterTypeBoolean(GLMParams.FitIntercept(), "Indicates if an intercept value should " +
				"be calculated.", true));

		types.add(new ParameterTypeBoolean(GLMParams.Standardization(),
				"Whether to standardize the training features before fitting the model.",
				true));

		types.add(new ParameterTypeDouble(GLMParams.ElasticNetParam(),
				"elastic net parameter",
				0.0d, 1.0d, 0.0d, true));

		types.add(new ParameterTypeInt(GLMParams.AggregationDepth(),
				"Suggested depth for treeAggregate",
				1, Integer.MAX_VALUE, 2, true));

		return types;
    }
}
