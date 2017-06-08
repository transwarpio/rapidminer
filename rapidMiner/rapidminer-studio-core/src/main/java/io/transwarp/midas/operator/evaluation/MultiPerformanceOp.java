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
package io.transwarp.midas.operator.evaluation;

import com.rapidminer.operator.OperatorDescription;
import io.transwarp.midas.constant.midas.params.tuning.PerfParams;

public class MultiPerformanceOp extends AbstractPerformanceOp {
    public MultiPerformanceOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public String[] getChoices() {
        return PerfParams.MultiOptions();
    }
}
