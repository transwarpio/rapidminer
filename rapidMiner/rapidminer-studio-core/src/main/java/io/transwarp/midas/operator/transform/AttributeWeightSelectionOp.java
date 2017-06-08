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
package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.features.selection.AttributeWeightSelection;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import io.transwarp.midas.constant.midas.params.data.SelectFeatureParams;

import java.util.ArrayList;
import java.util.List;

public class AttributeWeightSelectionOp extends AttributeWeightSelection {

	private static final String[] WEIGHT_RELATIONS = { SelectFeatureParams.Greater(),
			SelectFeatureParams.TopK(), SelectFeatureParams.TopP()};
	private static final int GREATER = 0;

	private static final int TOPK = 1;

	private static final int TOPPPERCENT = 2;

    public AttributeWeightSelectionOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new ArrayList<>();
		ParameterType type = new ParameterTypeCategory(SelectFeatureParams.WeightRelation(),
				"Selects only weights which fulfill this relation.", WEIGHT_RELATIONS, GREATER);
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeDouble(SelectFeatureParams.Weight(),
				"The selected relation will be evaluated against this value.",
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0d);
		type.registerDependencyCondition(new EqualTypeCondition(
				this, SelectFeatureParams.WeightRelation(), WEIGHT_RELATIONS, true, GREATER));
		type.setExpert(false);
		types.add(type);


		type = new ParameterTypeInt(
				SelectFeatureParams.K(),
				"Number k of attributes to be selected. For example 'top k' with k = 5 will return an exampleset containing only the 5 highest weighted attributes.",
				1, Integer.MAX_VALUE, 10);
		type.registerDependencyCondition(new EqualTypeCondition(this, SelectFeatureParams.WeightRelation(), WEIGHT_RELATIONS, true,
				TOPK));
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeDouble(
				SelectFeatureParams.P(),
				"Percentage of attributes to be selected. For example 'top p%' with p = 15 will return an exampleset containing only attributes which are part of the 15% of the highest weighted attributes.",
				0.0d, 1.0d, 0.5d);
		type.registerDependencyCondition(new EqualTypeCondition(this, SelectFeatureParams.WeightRelation(), WEIGHT_RELATIONS, true,
				TOPPPERCENT));
		type.setExpert(false);
		types.add(type);


		return types;
	}
}
