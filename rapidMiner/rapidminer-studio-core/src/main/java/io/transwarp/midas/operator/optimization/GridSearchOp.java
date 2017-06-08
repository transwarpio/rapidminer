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
package io.transwarp.midas.operator.optimization;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.meta.GridSearchParameterOptimizationOperator;

public class GridSearchOp extends GridSearchParameterOptimizationOperator {
    public GridSearchOp(OperatorDescription description) {
        super(description);
        remote = true;
    }
}
