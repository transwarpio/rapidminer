package com.rapidminer.extension.jdbc.operator.visualization;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.Process;
import com.rapidminer.extension.jdbc.gui.DatabaseExampleVisualization;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.DummyPortPairExtender;
import com.rapidminer.operator.ports.PortPairExtender;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.tools.ObjectVisualizerService;
import java.util.List;

public class DatabaseExampleVisualizationOperator extends Operator implements ConnectionProvider {
    public static final String PARAMETER_ID_COLUMN = "id_column";
    private PortPairExtender dummyPorts = new DummyPortPairExtender("through", this.getInputPorts(), this.getOutputPorts());

    public DatabaseExampleVisualizationOperator(OperatorDescription description) {
        super(description);
        this.dummyPorts.start();
        this.getTransformer().addRule(this.dummyPorts.makePassThroughRule());
    }

    public void doWork() throws OperatorException {
        String databaseURL = null;
        String username = null;
        String password = null;
        Process process = this.getProcess();
        switch(this.getParameterAsInt("define_connection")) {
            case 0:
                String visualizer = null;
                if(process != null) {
                    RepositoryLocation entry = process.getRepositoryLocation();
                    if(entry != null) {
                        visualizer = entry.getRepositoryName();
                    }
                }

                ConnectionEntry entry1 = DatabaseConnectionService.getConnectionEntry(this.getParameterAsString("connection"), visualizer, process != null?process.getRepositoryAccessor():null);
                if(entry1 == null) {
                    throw new UserError(this, 318, new Object[]{this.getParameterAsString("connection")});
                }

                databaseURL = entry1.getURL();
                username = entry1.getUser();
                password = new String(entry1.getPassword());
                break;
            case 1:
                databaseURL = this.getParameterAsString("database_url");
                username = this.getParameterAsString("username");
                password = this.getParameterAsString("password");
        }

        DatabaseExampleVisualization visualizer1 = new DatabaseExampleVisualization(databaseURL, username, password, this.getParameterAsInt("database_system"), this.getParameterAsString("table_name"), this.getParameterAsString("id_column"), this.getLog());
        ObjectVisualizerService.addObjectVisualizer(this, visualizer1);
        this.dummyPorts.passDataThrough();
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        types.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        types.addAll(DatabaseHandler.getQueryParameterTypes(this, true));
        types.add(new ParameterTypeString("id_column", "The column of the table holding the object ids for detail data querying.", false));
        return types;
    }

    public ConnectionEntry getConnectionEntry() {
        return DatabaseHandler.getConnectionEntry(this);
    }
}
