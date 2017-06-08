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
import com.rapidminer.operator.SimpleOperatorChain;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.Parameters;
import io.transwarp.midas.constant.midas.params.macros.MacroParams;

import java.util.ArrayList;
import java.util.List;

public class SimpleOperatorChainOp extends SimpleOperatorChain {
    public SimpleOperatorChainOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    public SimpleOperatorChainOp(OperatorDescription description, String subProcessName) {
        super(description, subProcessName);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeList(
                MacroParams.Macros(), "macros to be added",
                new ParameterTypeString("macro name", "macro name", false),
                new ParameterTypeString("value", "value", false)
        ));
        return types;
    }
}
