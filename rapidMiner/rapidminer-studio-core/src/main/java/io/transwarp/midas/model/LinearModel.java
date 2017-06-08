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

import com.rapidminer.datatable.DataTable;
import com.rapidminer.datatable.SimpleDataTable;
import com.rapidminer.datatable.SimpleDataTableRow;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Tools;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.learner.PredictionModel;
import io.transwarp.midas.adaptor.ILinearModel;

public class LinearModel extends PredictionModel implements ILinearModel {

    private double[] weights;
    private double intercept;
    private ExampleSet examples;

    public LinearModel(ExampleSet trainingExampleSet, double[] weightsOther, double interceptOther) {
        super(trainingExampleSet);
        weights = weightsOther;
        intercept = interceptOther;
        examples = trainingExampleSet;
    }

    @Override
    public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
        return null;
    }

    public DataTable createWeightsTable() {
        SimpleDataTable weightTable = new SimpleDataTable("Linear Model Weights", new String[] { "Attribute", "Weight" });
        String[] attributeNames = Tools.getRegularAttributeNames(getTrainingHeader());
        for (int j = 0; j < weights.length; j++) {
            int nameIndex = weightTable.mapString(0, attributeNames[j]);
            weightTable.add(new SimpleDataTableRow(new double[] { nameIndex, weights[j] }));
        }
        int nameIndex = weightTable.mapString(0, "intercept");
        weightTable.add(new SimpleDataTableRow(new double[] { nameIndex, intercept}));
        return weightTable;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("weights:\n");
        String[] attributeNames = Tools.getRegularAttributeNames(getTrainingHeader());
        for (int j = 0; j < weights.length; j++) {
            buffer.append(attributeNames[j] + ": " + weights[j] + "\n");
        }
        buffer.append("intercept" + ": " + intercept + "\n");
        String string = super.toString() + "\n" + buffer.toString();
        return string;
    }
}
