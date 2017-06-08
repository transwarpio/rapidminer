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
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.math.Averagable;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

public class CurveCollection extends MeasuredPerformance {
    private String name;
    private YIntervalSeriesCollection curves = new YIntervalSeriesCollection();
    private double auc;
    public CurveCollection(String name, double auc) {
        this.name = name;
        this.auc = auc;
    }

    public void setAUC(double auc) {
        this.auc = auc;
    }

    public void addSeries(YIntervalSeries series) {
        curves.addSeries(series);
    }

    public YIntervalSeriesCollection getCollections() {
        return curves;
    }

    @Override
    public String toString() {
        String result = name + ": " + Tools.formatNumber(auc);
        return result;
    }

    @Override
    public void countExample(Example example) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public double getExampleCount() {
        return 0;
    }

    @Override
    public double getFitness() {
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getMikroAverage() {
        return auc;
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    @Override
    protected void buildSingleAverage(Averagable averagable) {

    }
}
