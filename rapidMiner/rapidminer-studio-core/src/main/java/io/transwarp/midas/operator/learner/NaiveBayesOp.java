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
import com.rapidminer.operator.learner.bayes.NaiveBayes;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.classification.NaiveBayesParams;

import java.util.ArrayList;
import java.util.List;

public class NaiveBayesOp extends NaiveBayes{
    private String[] MODE_TYPES = {"multinomial","bernoulli"};

    public NaiveBayesOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeDouble(NaiveBayesParams.Smoothing(),"smoothing parameter",
                0d, Double.MAX_VALUE, 1d);
        types.add(type);

        type = new ParameterTypeStringCategory(NaiveBayesParams.ModelType(),"model type " +
                "using a string", MODE_TYPES , MODE_TYPES[0], false);
        type.setOptional(false);
        types.add(type);

        return types;
    }


}
