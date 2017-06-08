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
package io.transwarp.midas.operator.evaluation;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.validation.XValidation;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;

import java.util.ArrayList;
import java.util.List;

public class CrossValidationOp extends XValidation {
    public CrossValidationOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeBoolean(PARAMETER_AVERAGE_PERFORMANCES_ONLY,
                "Indicates if only performance vectors should be averaged or all types of averagable result vectors", true));

        ParameterType type = new ParameterTypeInt(PARAMETER_NUMBER_OF_VALIDATIONS,
                "Number of subsets for the crossvalidation.", 2, Integer.MAX_VALUE, 10);
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
