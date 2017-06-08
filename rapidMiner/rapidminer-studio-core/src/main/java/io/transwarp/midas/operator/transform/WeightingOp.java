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
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.PortNames;
import io.transwarp.midas.constant.midas.params.data.SelectFeatureParams;
import io.transwarp.midas.constant.midas.params.table.SelectParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class WeightingOp extends BaseOp {
    public WeightingOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

	private InputPort input = getInputPorts().createPort(PortNames.Input());
	private OutputPort weights = getOutputPorts().createPort(PortNames.Weights());


	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> list = new ArrayList<>();

		String[] options = {
				SelectFeatureParams.Chisq(),
				SelectFeatureParams.Correlation(),
				SelectFeatureParams.Gini(),
				SelectFeatureParams.InfoGain(),
				SelectFeatureParams.InfoGainRatio(),
				SelectFeatureParams.PCA()
		};
		ParameterType type = new ParameterTypeStringCategory(SelectFeatureParams.Method(), "method to calculate feature", options);
		list.add(type);
		return list;
	}
}
