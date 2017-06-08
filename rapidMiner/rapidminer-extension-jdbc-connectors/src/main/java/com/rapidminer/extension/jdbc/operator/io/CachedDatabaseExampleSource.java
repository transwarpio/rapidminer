package com.rapidminer.extension.jdbc.operator.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.ExampleSet;
import com.rapidminer.extension.jdbc.example.table.IndexCachedDatabaseExampleTable;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.io.AbstractExampleSource;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;

import java.sql.SQLException;
import java.util.List;

public class CachedDatabaseExampleSource extends AbstractExampleSource implements ConnectionProvider {
    public static final String PARAMETER_RECREATE_INDEX = "recreate_index";
    private DatabaseHandler databaseHandler;

    public CachedDatabaseExampleSource(OperatorDescription description) {
        super(description);
    }

    public ExampleSet createExampleSet() throws OperatorException {
        try {
            this.databaseHandler = DatabaseHandler.getConnectedDatabaseHandler(this);
            String e = this.getParameterAsString("table_name");
            boolean recreateIndex = this.getParameterAsBoolean("recreate_index");
            IndexCachedDatabaseExampleTable table = new IndexCachedDatabaseExampleTable(this.databaseHandler, e, 0, recreateIndex, this);
            return ResultSetExampleSource.createExampleSet(table, this);
        } catch (SQLException var4) {
            throw new UserError(this, var4, 304, new Object[]{var4.getMessage()});
        }
    }

    public void processFinished() {
        this.disconnect();
    }

    private void disconnect() {
        if(this.databaseHandler != null) {
            try {
                this.databaseHandler.disconnect();
                this.databaseHandler = null;
            } catch (SQLException var2) {
                this.logWarning("Cannot disconnect from database: " + var2);
            }
        }

    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        types.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        types.addAll(DatabaseHandler.getQueryParameterTypes(this, true));
        types.add(new ParameterTypeBoolean("recreate_index", "Indicates if a recreation of the index or index mapping table should be forced.", false));
        ParameterTypeString type = new ParameterTypeString("label_attribute", "The (case sensitive) name of the label attribute");
        type.setExpert(false);
        types.add(type);
        types.add(new ParameterTypeString("id_attribute", "The (case sensitive) name of the id attribute"));
        types.add(new ParameterTypeString("weight_attribute", "The (case sensitive) name of the weight attribute"));
        return types;
    }

    public ConnectionEntry getConnectionEntry() {
        return DatabaseHandler.getConnectionEntry(this);
    }
}
