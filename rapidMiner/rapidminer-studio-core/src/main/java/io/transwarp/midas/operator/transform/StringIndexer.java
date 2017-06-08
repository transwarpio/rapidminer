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
import com.rapidminer.operator.preprocessing.filter.NominalToBinominal;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttributes;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import io.transwarp.midas.constant.midas.params.data.StringIndexerParams;

import java.util.LinkedList;
import java.util.List;

public class StringIndexer extends NominalToBinominal {

    public StringIndexer(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<>();
        types.add(new ParameterTypeBoolean(StringIndexerParams.autoTypeDetection(),
                "automatic type detection", false, false));
        ParameterType inputColumns = new ParameterTypeAttributes(StringIndexerParams.inputColumns(),
                "input column", getInputPort(), true, false);
        inputColumns.registerDependencyCondition(
                new BooleanParameterCondition(this, StringIndexerParams.autoTypeDetection(), false, false));
        types.add(inputColumns);
        return types;
    }
}
