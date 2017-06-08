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
import com.rapidminer.operator.preprocessing.filter.ChangeAttributeName;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttribute;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.data.RenameParams;
import io.transwarp.midas.operator.BaseDataProcessOp;

import java.util.List;

public class RenameOp extends BaseDataProcessOp {
    public RenameOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeAttribute(RenameParams.oldName(),
                "The old name of the attribute.", exampleSetInput, false));
        types.add(new ParameterTypeString(RenameParams.newName(), "The new name of the attribute.", false));

        types.add(new ParameterTypeList(RenameParams.renameAdditionalAttributes(),
                "A list that can be used to define additional attributes that should be renamed.",
                new ParameterTypeAttribute(RenameParams.oldName(), "The old name of the attribute.", exampleSetInput,false),
                new ParameterTypeString(RenameParams.newName(), "The new name of the attribute.", false), false));
        return types;
    }
}
