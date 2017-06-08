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
package io.transwarp.midas.operator;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.preprocessing.sampling.SamplingOperator;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.tools.RandomGenerator;
import io.transwarp.midas.constant.midas.params.data.SampleParams;

import java.util.ArrayList;
import java.util.List;

public class SampleOp extends BaseDataProcessOp {
	/** The parameter name for &quot;The fraction of examples which should be sampled&quot; */
	public static final String PARAMETER_SAMPLE_RATIO = SampleParams.SampleRatio();

	public static final String PARAMETER_BALANCE_DATA = SampleParams.BalanceData();
	public static final String PARAMETER_BALANCE_COL = SampleParams.BalanceColumn();
	public static final String PARAMETER_SAMPLE_RATIO_LIST = SampleParams.SampleRatioByClass();
    public static final String PARAMETER_WITH_REPLACEMENT = SampleParams.WithReplacement();

    public SampleOp(OperatorDescription description) {
        super(description);
		remote = true;
    }

    @Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new ArrayList<>();
        ParameterType type = new ParameterTypeBoolean(PARAMETER_WITH_REPLACEMENT,
                "sampling with replacement or not", false, true);
        types.add(type);

		type = new ParameterTypeBoolean(PARAMETER_BALANCE_DATA,
				"If you need to sample differently for examples of a certain class, you might check this.", false, true);
		types.add(type);

		type = new ParameterTypeDouble(PARAMETER_SAMPLE_RATIO, "The fraction of examples which should be sampled", 0.0d,
				1.0d, 0.1d);
		type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, false));
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeString(PARAMETER_BALANCE_COL, "the class column to balance");
		type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, true));
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeList(PARAMETER_SAMPLE_RATIO_LIST, "The fraction per class.", new ParameterTypeString(
				"class", "The class name this sample size applies to."), new ParameterTypeDouble("ratio",
				"The fractions of examples of this class.", 0, Integer.MAX_VALUE));
		type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, true));
		type.setExpert(false);
		types.add(type);

		return types;
	}
}
