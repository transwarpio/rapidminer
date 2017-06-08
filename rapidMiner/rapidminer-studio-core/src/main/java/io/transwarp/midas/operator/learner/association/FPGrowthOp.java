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
package io.transwarp.midas.operator.learner.association;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.association.AssociationParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class FPGrowthOp extends BaseOp {
    public FPGrowthOp(OperatorDescription description) {
        super(description);
    }

	private final InputPort exampleSetInput = getInputPorts().createPort("example set");

	private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
	private final OutputPort frequentSetsOutput = getOutputPorts().createPort("frequent sets");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeDouble(AssociationParams.MinSupport(),
                "The minimal support necessary in order to be a frequent item (set).", 0.0d, 1.0d, 0.5d));
        types.add(new ParameterTypeInt(AssociationParams.NumPartitions(), "Number of partitions of rdd", 1, Integer.MAX_VALUE, 1));
        return types;
    }
}
