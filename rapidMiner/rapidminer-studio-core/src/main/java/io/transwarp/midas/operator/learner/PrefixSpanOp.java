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
package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import com.rapidminer.tools.Ontology;
import io.transwarp.midas.constant.midas.params.association.AssociationParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class PrefixSpanOp extends BaseOp {

    public PrefixSpanOp(OperatorDescription description) {
        super(description);
    }

    private InputPort exampleSetInput = getInputPorts().createPort("example set");
    private OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
    private OutputPort patternOutput = getOutputPorts().createPort("patterns");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType type = new ParameterTypeAttribute(AssociationParams.CustomerId(),
                "This attribute will be used to identify the customer of a transaction.", exampleSetInput, false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeAttribute(AssociationParams.TimeAttribute(),
                "This numerical attribute specifies the time of a transaction.", exampleSetInput, false, Ontology.NUMERICAL);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(AssociationParams.MinSupport(), "This specifies the minimal support of a pattern", 0, 1, false);
        type.setDefaultValue(0.5);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(AssociationParams.MaxPatternLength(), "This specifies the maximum length of a pattern", 0, Integer.MAX_VALUE, false);
        type.setDefaultValue(10);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeLong(AssociationParams.MaxLocalProjDBSize(), "This specifies the maximum local projected database size", 0, Long.MAX_VALUE, false);
        type.setDefaultValue(32000000L);
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
