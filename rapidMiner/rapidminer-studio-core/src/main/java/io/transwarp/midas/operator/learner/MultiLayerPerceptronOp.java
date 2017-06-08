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
import com.rapidminer.operator.learner.functions.neuralnet.ImprovedNeuralNetLearner;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualStringCondition;
import com.rapidminer.tools.RandomGenerator;
import io.transwarp.midas.constant.midas.params.SharedParams;
import io.transwarp.midas.constant.midas.params.classification.MultiLayerPerceptronParams;

import java.util.ArrayList;
import java.util.List;

public class MultiLayerPerceptronOp extends ImprovedNeuralNetLearner {

    private static final String[] SOLVERS = {"l-bfgs","gd"};

    public MultiLayerPerceptronOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType type = new ParameterTypeEnumeration(
                MultiLayerPerceptronParams.Layers(),
                "Describes the size of all layers.",
                new ParameterTypeInt(
                        "layer_sizes",
                        "The size of the layers. A size of < 0 leads to a layer size of (number_of_attributes + number of classes) / 2 + 1.",
                        -1, Integer.MAX_VALUE, -1));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(SharedParams.MaxIter(),
                "The number of training cycles used for the neural network training.", 1, Integer.MAX_VALUE, 500);
        type.setExpert(false);
        types.add(type);

        types.add(new ParameterTypeDouble(SharedParams.Tol(),
                "The optimization is stopped if the training error gets below this epsilon value.", 0.0d,
                Double.POSITIVE_INFINITY, 0.00001d));

        type = new ParameterTypeStringCategory(MultiLayerPerceptronParams.Solver(), " The solver that " +
                "Multilayer Perceptron Classifier use", SOLVERS, SOLVERS[0], false);
        type.setOptional(false);
        types.add(type);

        type = new ParameterTypeDouble(SharedParams.StepSize(), "Step size to be used for each iteration " +
                "of optimization", Double.MIN_NORMAL, Double.MAX_VALUE, 0.03d);
        type.registerDependencyCondition(new EqualStringCondition(this, MultiLayerPerceptronParams.Solver(), true, SOLVERS[1]));
        types.add(type);

        // types.addAll(RandomGenerator.getRandomGeneratorParameters(this));
        return types;
    }
}
