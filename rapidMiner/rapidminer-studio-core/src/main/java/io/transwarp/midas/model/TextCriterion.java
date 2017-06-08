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

import com.rapidminer.example.Example;
import com.rapidminer.operator.performance.MeasuredPerformance;
import com.rapidminer.tools.math.Averagable;

public class TextCriterion extends MeasuredPerformance {
    public TextCriterion(String name, double value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private double value;

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public double getExampleCount() {
        return 0;
    }

    @Override
    public double getFitness() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getMikroAverage() {
        return value;
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    @Override
    protected void buildSingleAverage(Averagable averagable) {

    }

    @Override
    public void countExample(Example example) {

    }
}
