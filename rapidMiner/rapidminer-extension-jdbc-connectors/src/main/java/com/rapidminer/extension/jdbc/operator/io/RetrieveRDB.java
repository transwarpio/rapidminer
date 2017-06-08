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
package com.rapidminer.extension.jdbc.operator.io;

import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.data.RetrieveJDBCParams;

import java.util.List;

public class RetrieveRDB extends RetrieveExampleSetOp implements ConnectionProvider {

    public RetrieveRDB(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        // partitionColumn
        ParameterType partitionColumn =
                new ParameterTypeString(RetrieveJDBCParams.partitionColumn(),
                        "partition column, it must be a numeric column from the table",
                        true);
        partitionColumn.setExpert(true);
        types.add(partitionColumn);

        // lowerBound
        ParameterType lowerBound =
                new ParameterTypeDouble(RetrieveJDBCParams.lowerBound(),
                        "lower bound",
                        Double.MIN_VALUE, Double.MAX_VALUE,
                        true);
        lowerBound.setExpert(true);
        types.add(lowerBound);

        // upperBound
        ParameterType upperBound =
                new ParameterTypeDouble(RetrieveJDBCParams.upperBound(),
                        "upper bound",
                        Double.MIN_VALUE, Double.MAX_VALUE,
                        true);
        upperBound.setExpert(true);
        types.add(upperBound);

        // numPartitions
        ParameterType numPartitions =
                new ParameterTypeInt(RetrieveJDBCParams.numPartitions(),
                        "num partitions",
                        0, Integer.MAX_VALUE,
                        true);
        numPartitions.setExpert(true);
        types.add(numPartitions);

        // fetchsize
        ParameterType fetchsize =
                new ParameterTypeInt(RetrieveJDBCParams.fetchsize(),
                        "fetch size",
                        0, Integer.MAX_VALUE,
                        true);
        fetchsize.setExpert(true);
        types.add(fetchsize);
        return types;
    }
}
