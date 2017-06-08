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

import com.rapidminer.example.Attributes;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.preprocessing.filter.ChangeAttributeRole;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.data.SetRoleParams;
import io.transwarp.midas.operator.BaseDataProcessOp;

import java.util.List;

public class SetRoleOp extends BaseDataProcessOp {
    public SetRoleOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private static final String REGULAR_NAME = "regular";

    private static final String[] TARGET_ROLES = new String[] { REGULAR_NAME, Attributes.ID_NAME, Attributes.LABEL_NAME,
            Attributes.PREDICTION_NAME, Attributes.CLUSTER_NAME, Attributes.WEIGHT_NAME, Attributes.BATCH_NAME };

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeAttribute(SetRoleParams.attributeName(),
                "The name of the attribute whose role should be changed.",
                exampleSetInput,false, false));
        ParameterType type = new ParameterTypeStringCategory(SetRoleParams.targetRole(),
                "The target role of the attribute (only changed if parameter change_attribute_type is true).", TARGET_ROLES,
                TARGET_ROLES[0]);
        type.setExpert(false);
        types.add(type);

        types.add(new ParameterTypeList(SetRoleParams.setAdditionalRoles(),
                "This parameter defines additional attribute role combinations.", new
                ParameterTypeAttribute(SetRoleParams.attributeName(),
                "The name of the attribute whose role should be changed.",
                exampleSetInput, false, false),
                new ParameterTypeStringCategory(SetRoleParams.targetRole(),
                        "The target role of the attribute (only changed if parameter change_attribute_type is true).",
                        TARGET_ROLES, TARGET_ROLES[0]), false));
        return types;
    }
}
