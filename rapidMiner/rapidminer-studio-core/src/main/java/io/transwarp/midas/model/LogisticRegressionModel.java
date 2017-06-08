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
package io.transwarp.midas.model;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import io.transwarp.midas.adaptor.ILogisticRegressionModel;

public class LogisticRegressionModel extends LinearModel implements ILogisticRegressionModel{
    public double threshold;
    public LogisticRegressionModel(ExampleSet trainingExampleSet, double[] weights, double intercept, double threshold) {
        super(trainingExampleSet, weights, intercept);
        this.threshold = threshold;
    }

    @Override
    public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
        return null;
    }

    @Override
    public String toString() {
        String s = super.toString();
        String string = s + "\nthreshold: " + threshold + "\n";
        return string;
    }
}
