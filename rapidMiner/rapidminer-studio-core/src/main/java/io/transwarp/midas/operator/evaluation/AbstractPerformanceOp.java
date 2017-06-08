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
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.tuning.PerfParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPerformanceOp extends BaseOp {

    protected String[] choices = getChoices();
   	private InputPort exampleSetInput = getInputPorts().createPort("labelled data");
	private InputPort performanceInput = getInputPorts().createPort("performance");
	private OutputPort performanceOutput = getOutputPorts().createPort("performance");

    public AbstractPerformanceOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    abstract public String[] getChoices();

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeStringCategory(PerfParams.MainCriterion(),
                "The criterion used for comparing performance vectors.", choices, choices[0]);
        type.setExpert(false);
        types.add(type);

        boolean isDefault = true;
        for (String c : choices) {
            ParameterType ptype = new ParameterTypeBoolean(c, c, isDefault, false);
            types.add(ptype);
            isDefault = false;
        }

        return types;
    }
}
