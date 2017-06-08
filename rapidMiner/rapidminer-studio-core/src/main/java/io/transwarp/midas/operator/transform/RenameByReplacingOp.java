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
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeRegexp;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.meta.RenameByReplaceParams;
import io.transwarp.midas.operator.BaseDataProcessOp;

import java.util.List;

public class RenameByReplacingOp extends BaseDataProcessOp {

    AttributeSelector selector = new AttributeSelector(this, this.exampleSetInput);
    public RenameByReplacingOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.addAll(selector.getParameterTypes());

        ParameterType type = new ParameterTypeRegexp(RenameByReplaceParams.ReplaceWhat(),
				"A regular expression defining what should be replaced in the attribute names.", "\\W");
		type.setShowRange(false);
		type.setExpert(false);
		types.add(type);

		types.add(new ParameterTypeString(RenameByReplaceParams.ReplaceBy(),
				"This string is used as replacement for all parts of the matching attributes where the parameter '"
						+ RenameByReplaceParams.ReplaceWhat() + "' matches.", true, false));
        return types;
    }
}
