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
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.io.AbstractReader;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeEnumeration;
import io.transwarp.midas.model.Thresholds;

import java.util.List;

public class CreateThresholdOp extends AbstractReader<Thresholds> {
	public static final String PARAMETER_THRESHOLDS = "thresholds";

	public CreateThresholdOp(OperatorDescription description) {
		super(description, Thresholds.class);
		remote = true;
	}

    @Override
    public Thresholds read() throws OperatorException {
		String[] ratioList = ParameterTypeEnumeration
				.transformString2Enumeration(getParameterAsString(PARAMETER_THRESHOLDS));
        double[] thresholds = new double[ratioList.length];
		for (int i = 0; i < ratioList.length; i++) {
			thresholds[i] = Double.valueOf(ratioList[i]);
		}
		return new Thresholds(thresholds);
    }

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> list = super.getParameterTypes();
		ParameterType type = new ParameterTypeEnumeration(PARAMETER_THRESHOLDS,
				"The confidence threshold to determine if the prediction should be positive.",
				new ParameterTypeDouble("threshold", "threshold", 0, 1));
		type.setExpert(false);
		list.add(type);
		return list;
	}
}
