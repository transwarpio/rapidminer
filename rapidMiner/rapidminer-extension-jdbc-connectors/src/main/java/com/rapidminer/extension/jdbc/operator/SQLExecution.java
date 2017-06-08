package com.rapidminer.extension.jdbc.operator;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.license.annotation.LicenseConstraint;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.DummyPortPairExtender;
import com.rapidminer.operator.ports.PortPairExtender;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.parameter.TextType;
import com.rapidminer.tools.Tools;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@LicenseConstraint(
        productId = "rapidminer-[^\\-]+",
        constraintId = "productivity",
        value = "PRODUCTIVITY_OPERATORS"
)
public class SQLExecution extends Operator {
    public static final String PARAMETER_QUERY = "query";
    public static final String PARAMETER_QUERY_FILE = "query_file";
    private PortPairExtender dummyPorts = new DummyPortPairExtender("through", this.getInputPorts(), this.getOutputPorts());

    public SQLExecution(OperatorDescription description) {
        super(description);
        this.dummyPorts.start();
        this.getTransformer().addRule(this.dummyPorts.makePassThroughRule());
    }

    public void doWork() throws OperatorException {
        try {
            DatabaseHandler sqle = DatabaseHandler.getConnectedDatabaseHandler(this);
            String query = this.getQuery();
            sqle.executeStatement(query, false, this, this.getLogger());
            sqle.disconnect();
        } catch (SQLException var3) {
            throw new UserError(this, var3, 304, new Object[]{var3.getMessage()});
        }

        this.dummyPorts.passDataThrough();
    }

    private String getQuery() throws OperatorException {
        String query = this.getParameterAsString("query");
        if(query != null) {
            query = query.trim();
        }

        String parameterUsed = null;
        boolean warning = false;
        if(query != null && query.length() != 0) {
            parameterUsed = "query";
            if(this.isParameterSet("query_file")) {
                warning = true;
            }
        } else {
            File queryFile = this.getParameterAsFile("query_file");
            if(queryFile != null) {
                try {
                    query = Tools.readTextFile(queryFile);
                    parameterUsed = "query_file";
                } catch (IOException var6) {
                    throw new UserError(this, var6, 302, new Object[]{queryFile, var6.getMessage()});
                }

                if(query == null || query.trim().length() == 0) {
                    throw new UserError(this, 305, new Object[]{queryFile});
                }
            }
        }

        if(query == null) {
            throw new UserError(this, 202, new Object[]{"query", "query_file"});
        } else {
            if(warning) {
                this.logWarning("Only one of the parameters \'query\' and \'query_file\' has to be set. Using value of \'" + parameterUsed + "\'.");
            }

            return query;
        }
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        types.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        ParameterTypeText type = new ParameterTypeText("query", "SQL query. If not set, the query is read from the file specified by \'query_file\'.", TextType.SQL);
        type.setExpert(false);
        types.add(type);
        types.add(new ParameterTypeFile("query_file", "File containing the query. Only evaluated if \'query\' is not set.", (String)null, true));
        types.addAll(DatabaseHandler.getStatementPreparationParamterTypes(this));
        return types;
    }
}
