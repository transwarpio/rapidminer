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

public class BinaryClassificationPerformance
        extends com.rapidminer.operator.performance.BinaryClassificationPerformance {
    private String name;
    private double value;
    private double std;
    public BinaryClassificationPerformance(String name, double value, double std, double [][] confusion, String[] labels) {
        this.name = name;
        this.value = value;
        this.std = std;
        this.counter = confusion;
        if (labels.length >= 2) {
            this.negativeClassName = labels[0];
            this.positiveClassName = labels[1];
        } else {
            this.negativeClassName = "NEG";
            this.positiveClassName = "POS";
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double[][] getCounter() {
        return counter;
    }

    @Override
    public double getExampleCount() {
        double sum = 0;
        for (int i = 0; i < counter.length; i++) {
            double[] row = counter[i];
            for (int j = 0; j < row.length; j++) {
                sum += row[j];
            }
        }
        return sum;
    }

    @Override
    public double getMikroAverage() {
        return value;
    }

    @Override
    public String getTitle() {
        return name + ": " + value + " +/- " + std;
    }
}
