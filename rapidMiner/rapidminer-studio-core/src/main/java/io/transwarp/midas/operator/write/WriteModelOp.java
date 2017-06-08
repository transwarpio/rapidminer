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
package io.transwarp.midas.operator.write;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.ReadWriteParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

public class WriteModelOp extends BaseOp {

    public InputPort fileInputPort = getInputPorts().createPort("model");
    public OutputPort fileOutputPort = getOutputPorts().createPort("model");

    public WriteModelOp(OperatorDescription description) {
        super(description);
        remote = true;
        getTransformer().addPassThroughRule(fileInputPort, fileOutputPort);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterTypeString filename = new ParameterTypeString(
                ReadWriteParams.Filename(),
                "File to read.",
                false,
                true);
        types.add(filename);

        ParameterType overwrite = new ParameterTypeBoolean(
                ReadWriteParams.Overwrite(),
                "overwrite existing file",
                true,
                true);

        types.add(overwrite);
        return types;
    }
}