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
package io.transwarp.midas.operator;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;

/**
 * this is base operator for remote operators.
 * if the child operator is simple, it is recommended to extend this class to implement midas' own operator
 * but if the original operator is complicated, it might be easier to just extend rapidminer operator and just
 * set remote = true.
 * The decision is on you.
 */
abstract public class BaseOp extends Operator {
    public BaseOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public void clear(int clearFlags) {
        // do nothing for remote operator
    }

}
