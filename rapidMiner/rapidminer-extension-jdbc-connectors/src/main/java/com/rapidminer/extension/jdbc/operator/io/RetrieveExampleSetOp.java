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

import com.rapidminer.Process;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseConnection;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeSQLQuery;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import com.rapidminer.repository.RepositoryLocation;
import io.transwarp.midas.constant.midas.params.data.RetrieveJDBCParams;
import io.transwarp.midas.constant.midas.params.data.RetrieveOperatorParams;
import io.transwarp.midas.operator.BaseOp;
import io.transwarp.midas.operator.retrieve.ParameterTypeFileUpload;

import java.util.ArrayList;
import java.util.List;

public class RetrieveExampleSetOp extends BaseOp implements ConnectionProvider {

    private final OutputPort outputPort = getOutputPorts().createPort("output");

    public RetrieveExampleSetOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        String[] sources  = new String[]{
                RetrieveOperatorParams.table(),
                RetrieveOperatorParams.query(),
                RetrieveOperatorParams.queryFile()
        };

        ParameterTypeCategory sourceCategory =
                new ParameterTypeCategory(RetrieveOperatorParams.source(),
                        "source type",
                        sources,
                        0);
        sourceCategory.setExpert(false);
        sourceCategory.setOptional(false);
        types.add(sourceCategory);

        ParameterTypeDatabaseConnection connection =
                new ParameterTypeDatabaseConnection(RetrieveOperatorParams.connection(),
                        "retrieve data from database");
        connection.setExpert(false);
        connection.setOptional(false);
        types.add(connection);

        ParameterTypeString table = new ParameterTypeString(RetrieveOperatorParams.table(), "table",
                true, false);
        table.registerDependencyCondition(new EqualTypeCondition(this, RetrieveOperatorParams.source(),
                sources, false, new int[]{0}));
        table.setOptional(true);
        types.add(table);

        ParameterTypeSQLQuery query = new ParameterTypeSQLQuery(RetrieveOperatorParams.query(), "An SQL query.");
        query.setExpert(false);
        query.setOptional(true);
        query.registerDependencyCondition(new EqualTypeCondition(this, RetrieveOperatorParams.source(),
                sources, false, new int[]{1}));
        types.add(query);

        ParameterTypeFile queryFile = new ParameterTypeFileUpload(RetrieveOperatorParams.queryFile(), "A file containing an SQL query.", "sql", "");
        queryFile.registerDependencyCondition(new EqualTypeCondition(this, RetrieveOperatorParams.source(),
                sources, false, new int[]{2}));
        queryFile.setExpert(false);
        queryFile.setOptional(true);
        types.add(queryFile);

        return types;
    }

    @Override
    public ConnectionEntry getConnectionEntry() {
        Process process = this.getProcess();
        String repositoryName = null;
        if(process != null) {
            RepositoryLocation repositoryLocation = process.getRepositoryLocation();
            if(repositoryLocation != null) {
                repositoryName = repositoryLocation.getRepositoryName();
            }
        }
        try {
            return DatabaseConnectionService.getConnectionEntry(this.getParameterAsString("connection"),
                    repositoryName);
        } catch (UndefinedParameterError undefinedParameterError) {
            throw new RuntimeException(undefinedParameterError);
        }
    }
}
