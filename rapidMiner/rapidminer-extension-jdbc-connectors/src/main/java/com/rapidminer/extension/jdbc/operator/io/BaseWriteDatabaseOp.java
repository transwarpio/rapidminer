package com.rapidminer.extension.jdbc.operator.io;

import com.rapidminer.Process;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseConnection;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.repository.RepositoryLocation;
import io.transwarp.midas.constant.midas.params.JdbcParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseWriteDatabaseOp extends BaseOp implements ConnectionProvider {
    public InputPort fileInputPort = getInputPorts().createPort("input");
    public OutputPort fileOutputPort = getOutputPorts().createPort("output");
    public BaseWriteDatabaseOp(OperatorDescription description) {
        super(description);
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

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterTypeDatabaseConnection connection =
                new ParameterTypeDatabaseConnection(JdbcParams.connection(),
                        "write data to connection");
        connection.setExpert(false);
        connection.setOptional(false);
        types.add(connection);

        ParameterTypeString filename = new ParameterTypeString(
                JdbcParams.Table(),
                "table name",
                false,
                true);
        types.add(filename);

        ParameterType overwrite = new ParameterTypeBoolean(
                JdbcParams.Overwrite(),
                "overwrite existing table",
                true,
                true);

        types.add(overwrite);
        return types;
    }
}
