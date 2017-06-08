package com.rapidminer.extension.jdbc.operator.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.ExampleSet;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.io.AbstractExampleSetWriter;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import java.sql.SQLException;
import java.util.List;

public class DatabaseExampleSetWriter extends AbstractExampleSetWriter implements ConnectionProvider {
    public static final String PARAMETER_OVERWRITE_MODE = "overwrite_mode";
    public static final String PARAMETER_SET_DEFAULT_VARCHAR_LENGTH = "set_default_varchar_length";
    public static final String PARAMETER_DEFAULT_VARCHAR_LENGTH = "default_varchar_length";
    public static final String PARAMETER_GET_GENERATED_PRIMARY_KEYS = "add_generated_primary_keys";
    public static final String PARAMETER_GENERATED_KEYS_ATTRIBUTE_NAME = "db_key_attribute_name";
    public static final String PARAMETER_BATCH_SIZE = "batch_size";
    private static final String JTDS_JDBC_CLASSNAME = "net.sourceforge.jtds.jdbc";
    private static final String MSSQL_JDBC_CLASSNAME = "com.microsoft.sqlserver.jdbc";

    public DatabaseExampleSetWriter(OperatorDescription description) {
        super(description);
    }

    public ExampleSet write(ExampleSet exampleSet) throws OperatorException {
        if(this.getParameterAsBoolean("add_generated_primary_keys")) {
            ConnectionEntry e = this.getConnectionEntry();
            if(e != null) {
                String[] driverClasses = e.getProperties().getDriverClasses();
                String[] var4 = driverClasses;
                int var5 = driverClasses.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    String driverClass = var4[var6];
                    if(driverClass.startsWith("net.sourceforge.jtds.jdbc") || driverClass.startsWith("com.microsoft.sqlserver.jdbc")) {
                        throw new UserError(this, 966);
                    }
                }
            }
        }

        try {
            DatabaseHandler var19 = DatabaseHandler.getConnectedDatabaseHandler(this);
            Throwable var20 = null;

            try {
                if(this.getParameterAsBoolean("add_generated_primary_keys")) {
                    exampleSet = (ExampleSet)exampleSet.clone();
                }

                this.getProgress().setTotal(exampleSet.size());
                var19.createTable(exampleSet, DatabaseHandler.getSelectedTableName(this), this.getParameterAsInt("overwrite_mode"), this.getApplyCount() == 1, this.getParameterAsBoolean("set_default_varchar_length")?this.getParameterAsInt("default_varchar_length"):-1, this.getParameterAsBoolean("add_generated_primary_keys"), this.getParameterAsString("db_key_attribute_name"), this.getParameterAsInt("batch_size"), this);
                this.getProgress().complete();
            } catch (Throwable var16) {
                var20 = var16;
                throw var16;
            } finally {
                if(var19 != null) {
                    if(var20 != null) {
                        try {
                            var19.close();
                        } catch (Throwable var15) {
                            var20.addSuppressed(var15);
                        }
                    } else {
                        var19.close();
                    }
                }

            }

            return exampleSet;
        } catch (SQLException var18) {
            throw new UserError(this, var18, 304, new Object[]{var18.getMessage()});
        }
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        types.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        types.addAll(DatabaseHandler.getQueryParameterTypes(this, true));
        types.add(new ParameterTypeCategory("overwrite_mode", "Indicates if an existing table should be overwritten or if data should be appended.", DatabaseHandler.OVERWRITE_MODES, 0));
        types.add(new ParameterTypeBoolean("set_default_varchar_length", "Set varchar columns to default length.", false));
        ParameterTypeInt type = new ParameterTypeInt("default_varchar_length", "Default length of varchar columns.", 0, 2147483647, 128);
        type.registerDependencyCondition(new BooleanParameterCondition(this, "set_default_varchar_length", true, true));
        types.add(type);
        ParameterTypeBoolean type1 = new ParameterTypeBoolean("add_generated_primary_keys", "Indicates whether a new attribute holding the auto generated primary keys is added to the result set.", false);
        type1.setExpert(true);
        types.add(type1);
        ParameterTypeString type2 = new ParameterTypeString("db_key_attribute_name", "The name of the attribute for the auto generated primary keys", "generated_primary_key", true);
        type2.setExpert(true);
        type2.registerDependencyCondition(new BooleanParameterCondition(this, "add_generated_primary_keys", true, true));
        types.add(type2);
        type = new ParameterTypeInt("batch_size", "The number of examples which are written at once with one single query to the database. Larger values can greatly improve the speed - too large values however can drastically <i>decrease</i> the performance. Additionally, some databases have restrictions on the maximum number of values written at once.", 1, 2147483647, 1, true);
        type.setExpert(true);
        types.add(type);
        return types;
    }

    public ConnectionEntry getConnectionEntry() {
        return DatabaseHandler.getConnectionEntry(this);
    }
}
