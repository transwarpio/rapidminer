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

import com.rapidminer.operator.Operator;
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

public class AprioriOp extends BaseOp {

    public AprioriOp(OperatorDescription description) {
        super(description);
    }

    /**
     * The parameter name for &quot; min support.&quot;
     */
    public static final String PARAMETER_MIN_SUPPORT = "min_support";

    /**
     * The parameter name for &quot; max length.&quot;
     */
    public static final String PARAMETER_MAX_LENGTH = "max_length";

    /**
     * The parameter name for &quot; min length.&quot;
     */
    public static final String PARAMETER_MIN_LENGTH = "min_length";

    /**
     * The parameter name for &quot; max_support.&quot;
     */
    public static final String PARAMETER_MAX_SUPPORT = "max_support";


    private InputPort exampleSetInput = getInputPorts().createPort("example set");
    private OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
    private OutputPort frequentSetsOutput = getOutputPorts().createPort("frequent sets");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeDouble(AssociationParams.MinSupport(), "This specifies the minimal support of a pattern", 0, 1, false);
        type.setDefaultValue(0.5d);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(AssociationParams.MaxSupport(), "This specifies the maximum length of support", 0, Double.MAX_VALUE, false);
        type.setDefaultValue(1.0d);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(AssociationParams.MinPatternLength(), "This specifies the minimum length of a pattern", 0, Integer.MAX_VALUE, false);
        type.setDefaultValue(0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(AssociationParams.MaxPatternLength(), "This specifies the maximum length of a pattern", 0, Integer.MAX_VALUE, false);
        type.setDefaultValue(10000);
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
