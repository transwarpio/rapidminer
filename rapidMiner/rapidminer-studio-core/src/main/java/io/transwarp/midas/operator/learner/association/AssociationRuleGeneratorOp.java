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
import com.rapidminer.operator.learner.associations.FrequentItemSets;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import io.transwarp.midas.constant.midas.params.association.AssociationParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class AssociationRuleGeneratorOp extends BaseOp {
    public AssociationRuleGeneratorOp(OperatorDescription description) {
        super(description);
    }

	private InputPort itemSetsInput = getInputPorts().createPort("item sets", FrequentItemSets.class);
	private OutputPort rulesOutput = getOutputPorts().createPort("rules");
	private OutputPort itemSetsOutput = getOutputPorts().createPort("item sets");
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        types.add(new ParameterTypeDouble(AssociationParams.MinConfidence(),
                "The minimum confidence of the rules", 0.0d, 1.0d, 0.8d));
        return types;
    }
}
