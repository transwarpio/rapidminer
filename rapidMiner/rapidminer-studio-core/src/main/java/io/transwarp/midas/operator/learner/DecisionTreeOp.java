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
import com.rapidminer.operator.learner.tree.ParallelDecisionTreeLearner;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import io.transwarp.midas.constant.midas.params.classification.DecisionTreeParams;

import java.util.ArrayList;
import java.util.List;

public class DecisionTreeOp extends ParallelDecisionTreeLearner {
    private static String[] IMPURITY_NAMES;

    public DecisionTreeOp(OperatorDescription description,String [] imputity) {
        super(description);
		IMPURITY_NAMES=imputity;
		remote = true;
    }



	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new ArrayList<>();

		ParameterType type = new ParameterTypeStringCategory(DecisionTreeParams.Impurity(),
				"Specifies the used criterion for selecting attributes and numerical splits.", IMPURITY_NAMES,
				IMPURITY_NAMES[0], false);
		type.setExpert(false);
		types.add(type);


		type = new ParameterTypeInt(DecisionTreeParams.MaxDepth(), "The maximum tree depth (-1: no bound)",
				-1, Integer.MAX_VALUE,
				20);
		type.setExpert(false);
		types.add(type);


		type = new ParameterTypeDouble(DecisionTreeParams.MinInfoGain(),
				"The minimal gain which must be achieved in order to produce a split.", 0.0d, Double.POSITIVE_INFINITY, 0.1d);
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeInt(DecisionTreeParams.MinInstancePerNode(),
				"The minimal size of a node in order to allow a split.", 1, Integer.MAX_VALUE, 4);
		types.add(type);

		type = new ParameterTypeInt(DecisionTreeParams.MaxBins(), "The maximum bins num", 1, Integer
				.MAX_VALUE,
				32);
		type.setExpert(true);
		types.add(type);

		types.add(new ParameterTypeInt(DecisionTreeParams.MaxCategories(), "Max categories",
				2, Integer.MAX_VALUE, 20));

		return types;
	}

}
