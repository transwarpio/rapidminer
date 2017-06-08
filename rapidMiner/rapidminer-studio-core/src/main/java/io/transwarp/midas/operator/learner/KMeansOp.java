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
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.SharedParams;
import io.transwarp.midas.constant.midas.params.clusting.KMeansParams;
import io.transwarp.midas.operator.BaseLearnerOp;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class KMeansOp extends BaseLearnerOp {

    public KMeansOp(OperatorDescription description) {
        super(description);
		remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        // Parameter K.
 		types.add(new ParameterTypeInt(
 				KMeansParams.K(),
				"The number of clusters which should be detected.",
				2, Integer.MAX_VALUE, 2, false));

		// Parameter max runs.
		types.add(new ParameterTypeInt(
				SharedParams.MaxIter(),
				"The maximum number of runs of k-Means with random" +
						"initialization that are performed.",
				1, Integer.MAX_VALUE, 20, false));

		// Parameter init mode.
		types.add(new ParameterTypeCategory(
				KMeansParams.InitMode(),
				"The initialization algorithm.",
				KMeansParams.InitModeOptions(),
				0, false));

		// Parameter max iteration.
		types.add(new ParameterTypeInt(
				KMeansParams.InitSteps(),
				"The number of steps for k-means|| initialization mode. Must be > 0.",
				1, Integer.MAX_VALUE, 5, true));

		// Parameter tol.
		types.add(new ParameterTypeDouble(
				KMeansParams.Tol(),
				"Distance threshold within which we've consider centers to have converged",
				0.0 , 1.0 , 0.0001 , true));

		//types.addAll(RandomGenerator.getRandomGeneratorParameters(this));

		return types;
    }
}
