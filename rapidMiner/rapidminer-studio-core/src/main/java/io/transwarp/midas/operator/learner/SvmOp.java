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
import com.rapidminer.operator.learner.functions.kernel.LinearMySVMLearner;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.classification.SVMParams;

import java.util.LinkedList;
import java.util.List;

public class SvmOp extends LinearMySVMLearner {

    public SvmOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<>();

        types.add(new ParameterTypeInt(SVMParams.numIterations(), "the number of iterations for SGD",
                0, Integer.MAX_VALUE, 100, false));

        types.add(new ParameterTypeDouble(SVMParams.stepSize(), "initial step size of SGD for the first " +
                "step",
                0, Double.MAX_VALUE, 1));

        types.add(new ParameterTypeDouble(SVMParams.regParam(), "the regularization parameter",
                0, Double.MAX_VALUE, 0.01));
        types.add(new ParameterTypeDouble(SVMParams.miniBatchFraction(), "fraction of data to be used for each SGD " +
                "iteration",
                0, Double.MAX_VALUE, 1));
        return types;
    }
}
