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
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeEnumeration;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.ApplyModelParams;

import java.util.ArrayList;
import java.util.List;

public class ApplyModelOp extends BaseOp {
    private final InputPort modelInput = getInputPorts().createPort("model");
	private final InputPort exampleSetInput = getInputPorts().createPort("unlabelled data");
	private final OutputPort exampleSetOutput = getOutputPorts().createPort("labelled data");
	private final OutputPort modelOutput = getOutputPorts().createPort("model");

    public ApplyModelOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeEnumeration(
                ApplyModelParams.LabelProps(),
                "output the probability of specific label. only use it when applying classification model with probability output",
                new ParameterTypeString("label", "label")));
        return types;
    }
}
