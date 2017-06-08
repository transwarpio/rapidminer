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
package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualStringCondition;
import io.transwarp.midas.constant.midas.PortNames;
import io.transwarp.midas.constant.midas.params.data.FilterDataParams;
import io.transwarp.midas.operator.BaseDataProcessOp;
import io.transwarp.midas.ui.property.ParameterTypeSqlExpr;

import java.util.ArrayList;
import java.util.List;

public class FilterOp extends BaseDataProcessOp {
	public static final String[] KNOWN_CONDITION_NAMES = {
			FilterDataParams.ConditionNotMissing(),
			FilterDataParams.ConditionNotMissingAttributes(),
			FilterDataParams.ConditionNotMissingLabels(),
			FilterDataParams.ConditionCustomFilter()
			};

	public static final int CONDITION_CUSTOM_FILTER = 3;

	/** The parameter name for &quot;Implementation of the condition.&quot; */
	public static final String PARAMETER_CONDITION_CLASS = FilterDataParams.ConditionClass();

	final OutputPort unmatched = getOutputPorts().createPort(PortNames.Unmatched());

    public FilterOp(OperatorDescription description) {
        super(description);
        remote = true;
    }
    	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new ArrayList<>();

		ParameterType type = new ParameterTypeSqlExpr(FilterDataParams.FilterExpr(),
				"Defines the filter expression, you can use sql-like expression",
				exampleSetInput, true);
		type.registerDependencyCondition(new EqualStringCondition(this, PARAMETER_CONDITION_CLASS, false,
				KNOWN_CONDITION_NAMES[CONDITION_CUSTOM_FILTER]));
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeStringCategory(PARAMETER_CONDITION_CLASS, "Implementation of the condition.",
				KNOWN_CONDITION_NAMES,
				KNOWN_CONDITION_NAMES[CONDITION_CUSTOM_FILTER], false);
		type.setExpert(false); // confusing, only show for experts, default custom filters are fine
		// for new users
		types.add(type);

		return types;
	}
}
