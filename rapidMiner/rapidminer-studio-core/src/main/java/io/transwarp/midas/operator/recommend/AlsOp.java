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
package io.transwarp.midas.operator.recommend;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.association.ALSParams;
import io.transwarp.midas.operator.BaseLearnerOp;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class AlsOp extends BaseLearnerOp {
    public AlsOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeInt(ALSParams.rank(),"rank of the factorization",0, Integer.MAX_VALUE, false);
        type.setDefaultValue(10);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(ALSParams.implicitPrefs(),"whether to use implicit preference",false, false);
        type.setDefaultValue(false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(ALSParams.alpha(),"alpha for implicit preference",0,1, false);
        type.setDefaultValue(1.0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(ALSParams.nonnegative(),"whether to use nonnegative constraint for least squares",false,false);
        type.setDefaultValue(false);
        type.setExpert(false);
        types.add(type);


        type = new ParameterTypeInt(ALSParams.maxIter(),"max iterate time",0, Integer.MAX_VALUE, false);
        type.setDefaultValue(10);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(ALSParams.regParam(),"regularization parameter ",0, 1, false);
        type.setDefaultValue(0.1);
        type.setExpert(false);
        types.add(type);

        return types;
    }

}
