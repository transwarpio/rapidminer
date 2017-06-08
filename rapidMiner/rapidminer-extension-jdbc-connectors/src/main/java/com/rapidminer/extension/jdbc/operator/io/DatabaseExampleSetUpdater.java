package com.rapidminer.extension.jdbc.operator.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ProcessStoppedException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.io.AbstractExampleSetWriter;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.tools.AttributeSubsetSelector;
import com.rapidminer.parameter.ParameterType;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DatabaseExampleSetUpdater extends AbstractExampleSetWriter implements ConnectionProvider {
    private final AttributeSubsetSelector attributeSelector = new AttributeSubsetSelector(this, (InputPort)this.getInputPorts().getPortByIndex(0));
    private DatabaseHandler databaseHandler;

    public DatabaseExampleSetUpdater(OperatorDescription description) {
        super(description);
    }

    public ExampleSet write(ExampleSet exampleSet) throws OperatorException {
        try {
            DatabaseHandler e = DatabaseHandler.getConnectedDatabaseHandler(this);
            Throwable var3 = null;

            try {
                Set idAttributeSet = this.attributeSelector.getAttributeSubset(exampleSet, true);
                TableName selectedTableName = DatabaseHandler.getSelectedTableName(this);
                Iterator var6 = idAttributeSet.iterator();

                while(var6.hasNext()) {
                    Attribute idAtt = (Attribute)var6.next();
                    if(idAtt == null) {
                        throw new UserError(this, 129);
                    }
                }

                this.getProgress().setTotal(exampleSet.size());
                e.updateTable(this, exampleSet, selectedTableName, idAttributeSet, this.getLogger());
                this.getProgress().complete();
            } catch (Throwable var16) {
                var3 = var16;
                throw var16;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var15) {
                            var3.addSuppressed(var15);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return exampleSet;
        } catch (SQLException var18) {
            if(this.databaseHandler != null && this.databaseHandler.isCancelled()) {
                throw new ProcessStoppedException(this);
            } else {
                throw new UserError(this, var18, 304, new Object[]{var18.getMessage()});
            }
        }
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        types.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        types.addAll(DatabaseHandler.getQueryParameterTypes(this, true));
        types.addAll(this.attributeSelector.getParameterTypes());
        return types;
    }

    public ConnectionEntry getConnectionEntry() {
        return DatabaseHandler.getConnectionEntry(this);
    }
}
