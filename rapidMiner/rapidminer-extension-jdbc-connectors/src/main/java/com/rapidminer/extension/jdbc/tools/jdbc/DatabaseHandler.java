package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.Process;
import com.rapidminer.ProcessStateListener;
import com.rapidminer.core.license.DatabaseConstraintViolationException;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.example.*;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseConnection;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseSchema;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseTable;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeSQLQuery;
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;
import com.rapidminer.extension.jdbc.tools.jdbc.JDBCProperties;
import com.rapidminer.extension.jdbc.tools.jdbc.StatementCreator;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.FieldConnectionEntry;
import com.rapidminer.license.LicenseConstants;
import com.rapidminer.license.LicenseManagerRegistry;
import com.rapidminer.license.violation.LicenseConstraintViolation;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ProcessStoppedException;
import com.rapidminer.operator.UserError;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeEnumeration;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypePassword;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeTupel;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.ProgressListener;
import com.rapidminer.tools.Tools;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseHandler implements AutoCloseable {
    public static final String PARAMETER_DEFINE_CONNECTION = "define_connection";
    public static final String[] CONNECTION_MODES = new String[]{"predefined", "url", "jndi"};
    public static final int CONNECTION_MODE_PREDEFINED = 0;
    public static final int CONNECTION_MODE_URL = 1;
    public static final int CONNECTION_MODE_JNDI = 2;
    public static final String PARAMETER_CONNECTION = "connection";
    public static final String PARAMETER_DATABASE_SYSTEM = "database_system";
    public static final String PARAMETER_DATABASE_URL = "database_url";
    public static final String PARAMETER_USERNAME = "username";
    public static final String PARAMETER_PASSWORD = "password";
    public static final String PARAMETER_DEFINE_QUERY = "define_query";
    public static final String PARAMETER_JNDI_NAME = "jndi_name";
    public static final String[] QUERY_MODES = new String[]{"query", "query file", "table name"};
    public static final int QUERY_QUERY = 0;
    public static final int QUERY_FILE = 1;
    public static final int QUERY_TABLE = 2;
    public static final String PARAMETER_QUERY = "query";
    public static final String PARAMETER_QUERY_FILE = "query_file";
    public static final String PARAMETER_TABLE_NAME = "table_name";
    public static final String PARAMETER_USE_DEFAULT_SCHEMA = "use_default_schema";
    public static final String PARAMETER_SCHEMA_NAME = "schema_name";
    public static final String[] OVERWRITE_MODES = new String[]{"none", "overwrite first, append then", "overwrite", "append"};
    public static final int OVERWRITE_MODE_NONE = 0;
    public static final int OVERWRITE_MODE_OVERWRITE_FIRST = 1;
    public static final int OVERWRITE_MODE_OVERWRITE = 2;
    public static final int OVERWRITE_MODE_APPEND = 3;
    private String databaseURL;
    private StatementCreator statementCreator;
    private String user;
    private volatile boolean cancelled = false;
    private Connection connection;
    public static final String PARAMETER_PARAMETERS = "parameters";
    public static final String PARAMETER_PREPARE_STATEMENT = "prepare_statement";
    private static final String[] SQL_TYPES = new String[]{"VARCHAR", "INTEGER", "REAL", "LONG"};

    private DatabaseHandler(String databaseURL, String user) {
        this.databaseURL = databaseURL;
        this.user = user;
    }

    public static DatabaseHandler getConnectedDatabaseHandler(ConnectionEntry entry) throws SQLException {
        DatabaseHandler handler = new DatabaseHandler(entry.getURL(), entry.getUser());
        Properties props;
        if(entry instanceof FieldConnectionEntry) {
            props = ((FieldConnectionEntry)entry).getConnectionProperties();
        } else {
            props = new Properties();
        }

        handler.connect(entry.getPassword(), props, true);
        return handler;
    }

    public static DatabaseHandler getHandler(Connection connection) throws OperatorException, SQLException {
        DatabaseHandler databaseHandler = new DatabaseHandler("preconnected", "unknown");
        databaseHandler.connection = connection;
        databaseHandler.statementCreator = new StatementCreator(connection);
        return databaseHandler;
    }

    public static DatabaseHandler getConnectedDatabaseHandler(String databaseURL, String username, String password) throws OperatorException, SQLException {
        return getConnectedDatabaseHandler(databaseURL, username, password, true);
    }

    public static DatabaseHandler getConnectedDatabaseHandler(String databaseURL, String username, String password, boolean autoCommit) throws SQLException {
        DatabaseHandler databaseHandler = new DatabaseHandler(databaseURL, username);
        databaseHandler.connect(password.toCharArray(), new Properties(), autoCommit);
        return databaseHandler;
    }

    public StatementCreator getStatementCreator() {
        return this.statementCreator;
    }

    private void connect(char[] passwd, Properties props, boolean autoCommit) throws SQLException {
        if(this.connection != null) {
            throw new SQLException("Connection to database \'" + this.databaseURL + "\' already exists!");
        } else {
            LogService.getRoot().log(Level.CONFIG, "com.rapidminer.tools.jdbc.DatabaseHandler.connecting_to_database", new Object[]{this.databaseURL, this.user});
            DriverManager.setLoginTimeout(30);
            props.put("SetBigStringTryClob", "true");
            if(this.user != null && !this.user.isEmpty()) {
                props.put("user", this.user);
                props.put("password", new String(passwd));
            }

            this.connection = DriverManager.getConnection(this.databaseURL, props);
            this.connection.setAutoCommit(autoCommit);
            this.statementCreator = new StatementCreator(this.connection);
        }
    }

    public void disconnect() throws SQLException {
        if(this.connection != null) {
            this.connection.close();
            this.unregister();
        }

    }

    private void unregister() {
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Statement createStatement(boolean scrollable, boolean updateable) throws SQLException {
        if(this.connection == null) {
            throw new SQLException("Could not create a statement for \'" + this.databaseURL + "\': not connected.");
        } else {
            Statement statement = null;
            if(scrollable && updateable) {
                statement = this.connection.createStatement(1005, 1008);
            } else if(scrollable) {
                statement = this.connection.createStatement(1005, 1007);
            } else {
                statement = this.connection.createStatement(1003, 1007);
            }

            return statement;
        }
    }

    public Statement createStatement(boolean scrollable) throws SQLException {
        return this.createStatement(scrollable, false);
    }

    public PreparedStatement createPreparedStatement(String sqlString, boolean scrollableAndUpdatable) throws SQLException {
        if(this.connection == null) {
            throw new SQLException("Could not create a prepared statement for \'" + this.databaseURL + "\': not connected.");
        } else {
            return scrollableAndUpdatable?this.connection.prepareStatement(sqlString, 1005, 1008):this.connection.prepareStatement(sqlString, 1003, 1007);
        }
    }

    public void commit() throws SQLException {
        if(this.connection != null && !this.connection.isClosed()) {
            this.connection.commit();
        } else {
            throw new SQLException("Could not commit: no open connection to database \'" + this.databaseURL + "\' !");
        }
    }

    /** @deprecated */
    @Deprecated
    public ResultSet query(String sqlQuery) throws SQLException {
        if(!sqlQuery.toLowerCase().startsWith("select")) {
            throw new SQLException("Query: Only SQL-Statements starting with SELECT are allowed: " + sqlQuery);
        } else {
            Statement st = this.createStatement(false);
            ResultSet rs = st.executeQuery(sqlQuery);
            return rs;
        }
    }

    public void addColumn(Attribute attribute, String tableName) throws SQLException {
        boolean exists = this.existsColumnInTable(tableName, attribute.getName());
        if(exists) {
            this.removeColumn(attribute, tableName);
        }

        Statement st = this.connection.createStatement(1003, 1008);
        Throwable var5 = null;

        try {
            String query = "ALTER TABLE " + this.statementCreator.makeIdentifier(tableName) + " ADD COLUMN " + this.statementCreator.makeColumnCreator(attribute);
            st.execute(query);
        } catch (Throwable var14) {
            var5 = var14;
            throw var14;
        } finally {
            if(st != null) {
                if(var5 != null) {
                    try {
                        st.close();
                    } catch (Throwable var13) {
                        var5.addSuppressed(var13);
                    }
                } else {
                    st.close();
                }
            }

        }

    }

    public void removeColumn(Attribute attribute, String tableName) throws SQLException {
        Statement st = this.connection.createStatement(1003, 1008);
        Throwable var4 = null;

        try {
            String query = "ALTER TABLE " + this.statementCreator.makeIdentifier(tableName) + " DROP COLUMN " + this.statementCreator.makeColumnIdentifier(attribute);
            st.execute(query);
        } catch (Throwable var13) {
            var4 = var13;
            throw var13;
        } finally {
            if(st != null) {
                if(var4 != null) {
                    try {
                        st.close();
                    } catch (Throwable var12) {
                        var4.addSuppressed(var12);
                    }
                } else {
                    st.close();
                }
            }

        }

    }

    public void dropTable(TableName tableName) throws SQLException {
        Statement statement = this.createStatement(false);
        Throwable var3 = null;

        try {
            String sql = this.statementCreator.makeDropStatement(tableName);
            this.executeUpdate(statement, (Operator)null, sql);
        } catch (Throwable var12) {
            var3 = var12;
            throw var12;
        } finally {
            if(statement != null) {
                if(var3 != null) {
                    try {
                        statement.close();
                    } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                    }
                } else {
                    statement.close();
                }
            }

        }

    }

    /** @deprecated */
    @Deprecated
    public void dropTable(String tableName) throws SQLException {
        this.dropTable(new TableName(tableName));
    }

    public void emptyTable(TableName tableName) throws SQLException {
        Statement statement = this.createStatement(false);
        Throwable var3 = null;

        try {
            this.executeUpdate(statement, (Operator)null, this.statementCreator.makeDeleteStatement(tableName));
        } catch (Throwable var12) {
            var3 = var12;
            throw var12;
        } finally {
            if(statement != null) {
                if(var3 != null) {
                    try {
                        statement.close();
                    } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                    }
                } else {
                    statement.close();
                }
            }

        }

    }

    /** @deprecated */
    @Deprecated
    public void emptyTable(String tableName) throws SQLException {
        this.emptyTable(new TableName(tableName));
    }

    // this is for midas
    public void simpleCreataTable(ExampleSet exampleSet, TableName tableName, boolean overwrite, int defaultVarcharLength, Operator operator) throws SQLException, DatabaseConstraintViolationException {
        checkDatabaseConstraintOnOperator(operator, this.databaseURL);
        Statement statement = this.createStatement(false);

        try{
            boolean exists = this.existsTable(tableName);
            String genPrimaryKeyAttribute;
            if (exists) {
                if (overwrite) {
                    this.executeUpdate(statement, operator, this.statementCreator.makeDropStatement(tableName));
                    exampleSet.recalculateAllAttributeStatistics();
                    genPrimaryKeyAttribute = this.statementCreator.makeTableCreator(exampleSet.getAttributes(), tableName, defaultVarcharLength);
                    this.executeUpdate(statement, operator, genPrimaryKeyAttribute);
                }
            } else {
                exampleSet.recalculateAllAttributeStatistics();
                genPrimaryKeyAttribute = this.statementCreator.makeTableCreator(exampleSet.getAttributes(), tableName, defaultVarcharLength);
                this.executeUpdate(statement, operator, genPrimaryKeyAttribute);
            }
        } finally {
            statement.close();
        }

    }

    public void createTable(ExampleSet exampleSet, String tableName, int overwriteMode, boolean firstAttempt, int defaultVarcharLength) throws SQLException {
        this.createTable(exampleSet, tableName, overwriteMode, firstAttempt, defaultVarcharLength, false, "does_not_matter");
    }

    public void createTable(ExampleSet exampleSet, String tableName, int overwriteMode, boolean firstAttempt, int defaultVarcharLength, boolean addAutoGeneratedPrimaryKeys, String generatedPrimaryKeyAttributeName) throws SQLException {
        try {
            this.createTable(exampleSet, new TableName(tableName), overwriteMode, firstAttempt, defaultVarcharLength, addAutoGeneratedPrimaryKeys, generatedPrimaryKeyAttributeName, 1, (Operator)null);
        } catch (ProcessStoppedException var9) {
            ;
        } catch (DatabaseConstraintViolationException var10) {
            ;
        }

    }

    public void createTable(ExampleSet exampleSet, TableName tableName, int overwriteMode, boolean firstAttempt, int defaultVarcharLength, boolean addAutoGeneratedPrimaryKeys, String generatedPrimaryKeyAttributeName, int batchSize) throws SQLException {
        try {
            this.createTable(exampleSet, tableName, overwriteMode, firstAttempt, defaultVarcharLength, addAutoGeneratedPrimaryKeys, generatedPrimaryKeyAttributeName, batchSize, (Operator)null);
        } catch (ProcessStoppedException var10) {
            ;
        } catch (DatabaseConstraintViolationException var11) {
            ;
        }

    }

    public void createTable(ExampleSet exampleSet, TableName tableName, int overwriteMode, boolean firstAttempt, int defaultVarcharLength, boolean addAutoGeneratedPrimaryKeys, String generatedPrimaryKeyAttributeName, int batchSize, Operator operator) throws SQLException, ProcessStoppedException, IllegalArgumentException, DatabaseConstraintViolationException {
        checkDatabaseConstraintOnOperator(operator, this.databaseURL);
        Statement statement = this.createStatement(false);
        Throwable var11 = null;

        try {
            boolean exists = this.existsTable(tableName);
            String genPrimaryKeyAttribute;
            if(exists) {
                switch(overwriteMode) {
                    case 0:
                        throw new SQLException("Table with name \'" + tableName + "\' already exists and overwriting mode is not activated." + Tools.getLineSeparator() + "Please change table name or activate overwriting mode.");
                    case 1:
                        if(firstAttempt) {
                            this.executeUpdate(statement, operator, this.statementCreator.makeDropStatement(tableName));
                            exampleSet.recalculateAllAttributeStatistics();
                            genPrimaryKeyAttribute = this.statementCreator.makeTableCreator(exampleSet.getAttributes(), tableName, defaultVarcharLength);
                            this.executeUpdate(statement, operator, genPrimaryKeyAttribute);
                        }
                        break;
                    case 2:
                        this.executeUpdate(statement, operator, this.statementCreator.makeDropStatement(tableName));
                        exampleSet.recalculateAllAttributeStatistics();
                        genPrimaryKeyAttribute = this.statementCreator.makeTableCreator(exampleSet.getAttributes(), tableName, defaultVarcharLength);
                        this.executeUpdate(statement, operator, genPrimaryKeyAttribute);
                }
            } else {
                exampleSet.recalculateAllAttributeStatistics();
                genPrimaryKeyAttribute = this.statementCreator.makeTableCreator(exampleSet.getAttributes(), tableName, defaultVarcharLength);
                this.executeUpdate(statement, operator, genPrimaryKeyAttribute);
            }

            Attribute var104 = null;
            PreparedStatement batchSizeInsertStatement = this.getInsertIntoTableStatement(tableName, exampleSet, addAutoGeneratedPrimaryKeys);
            Throwable var15 = null;

            try {
                if(batchSizeInsertStatement != null) {
                    boolean oldAutoCommitStatus = true;
                    oldAutoCommitStatus = batchSizeInsertStatement.getConnection().getAutoCommit();
                    batchSizeInsertStatement.getConnection().setAutoCommit(false);
                    if(addAutoGeneratedPrimaryKeys) {
                        var104 = AttributeFactory.createAttribute(generatedPrimaryKeyAttributeName, 3);
                        exampleSet.getExampleTable().addAttribute(var104);
                        exampleSet.getAttributes().addRegular(var104);
                    }

                    int counter = 0;
                    int rowCounter = 0;
                    ArrayList generatedKeysList = new ArrayList();
                    boolean needToCommitBatch = false;
                    Iterator i = exampleSet.iterator();

                    while(i.hasNext()) {
                        Example example = (Example)i.next();
                        this.applyBatchInsertIntoTable(batchSizeInsertStatement, example, exampleSet.getAttributes().allAttributeRoles(), addAutoGeneratedPrimaryKeys, var104);
                        needToCommitBatch = true;
                        ++rowCounter;
                        if(rowCounter % batchSize == 0) {
                            batchSizeInsertStatement.executeBatch();
                            needToCommitBatch = false;
                            if(addAutoGeneratedPrimaryKeys) {
                                ResultSet key = batchSizeInsertStatement.getGeneratedKeys();
                                Throwable var24 = null;

                                try {
                                    while(key.next()) {
                                        int key1 = key.getInt(1);
                                        generatedKeysList.add(Integer.valueOf(key1));
                                    }
                                } catch (Throwable var98) {
                                    var24 = var98;
                                    throw var98;
                                } finally {
                                    if(key != null) {
                                        if(var24 != null) {
                                            try {
                                                key.close();
                                            } catch (Throwable var95) {
                                                var24.addSuppressed(var95);
                                            }
                                        } else {
                                            key.close();
                                        }
                                    }

                                }
                            }
                        }

                        if(operator != null) {
                            ++counter;
                            if(counter % 100 == 0) {
                                operator.getProgress().step(100);
                                counter = 0;
                            }
                        }
                    }

                    if(needToCommitBatch) {
                        batchSizeInsertStatement.executeBatch();
                        needToCommitBatch = false;
                        if(addAutoGeneratedPrimaryKeys) {
                            ResultSet var107 = batchSizeInsertStatement.getGeneratedKeys();
                            Throwable var105 = null;

                            try {
                                while(var107.next()) {
                                    int var106 = var107.getInt(1);
                                    generatedKeysList.add(Integer.valueOf(var106));
                                }
                            } catch (Throwable var96) {
                                var105 = var96;
                                throw var96;
                            } finally {
                                if(var107 != null) {
                                    if(var105 != null) {
                                        try {
                                            var107.close();
                                        } catch (Throwable var94) {
                                            var105.addSuppressed(var94);
                                        }
                                    } else {
                                        var107.close();
                                    }
                                }

                            }
                        }
                    }

                    if(addAutoGeneratedPrimaryKeys) {
                        if(generatedKeysList.size() != exampleSet.size()) {
                            throw new SQLException("The table does not contain a auto increment primary key. Please deactivate the Parameter \"add_generated_primary_keys\".");
                        }

                        for(int var108 = 0; var108 < generatedKeysList.size(); ++var108) {
                            exampleSet.getExample(var108).setValue(var104, (double)((Integer)generatedKeysList.get(var108)).intValue());
                        }
                    }

                    batchSizeInsertStatement.getConnection().commit();
                    batchSizeInsertStatement.getConnection().setAutoCommit(oldAutoCommitStatus);
                }
            } catch (Throwable var100) {
                var15 = var100;
                throw var100;
            } finally {
                if(batchSizeInsertStatement != null) {
                    if(var15 != null) {
                        try {
                            batchSizeInsertStatement.close();
                        } catch (Throwable var93) {
                            var15.addSuppressed(var93);
                        }
                    } else {
                        batchSizeInsertStatement.close();
                    }
                }

            }
        } catch (Throwable var102) {
            var11 = var102;
            throw var102;
        } finally {
            if(statement != null) {
                if(var11 != null) {
                    try {
                        statement.close();
                    } catch (Throwable var92) {
                        var11.addSuppressed(var92);
                    }
                } else {
                    statement.close();
                }
            }

        }

    }

    private PreparedStatement getInsertIntoTableStatement(TableName tableName, ExampleSet exampleSet, boolean addAutoGeneratedPrimaryKeys) throws SQLException {
        if(this.connection == null) {
            throw new SQLException("Could not create a prepared statement for \'" + this.databaseURL + "\': not connected.");
        } else {
            return addAutoGeneratedPrimaryKeys?this.connection.prepareStatement(this.statementCreator.makeInsertStatement(tableName, exampleSet), 1):this.connection.prepareStatement(this.statementCreator.makeInsertStatement(tableName, exampleSet));
        }
    }

    private void applyBatchInsertIntoTable(PreparedStatement statement, Example example, Iterator<AttributeRole> attributes, boolean addAutoGeneratedPrimaryKeys, Attribute genPrimaryKey) throws SQLException {
        LinkedList attributeList = new LinkedList();

        while(attributes.hasNext()) {
            attributeList.add(((AttributeRole)attributes.next()).getAttribute());
        }

        int counter = 1;
        Iterator var8 = attributeList.iterator();

        while(true) {
            Attribute attribute;
            do {
                if(!var8.hasNext()) {
                    statement.addBatch();
                    return;
                }

                attribute = (Attribute)var8.next();
            } while(addAutoGeneratedPrimaryKeys && attribute == genPrimaryKey);

            double value = example.getValue(attribute);
            if(Double.isNaN(value)) {
                int valueString = this.statementCreator.getSQLTypeForRMValueType(attribute.getValueType()).getDataType();
                statement.setNull(counter, valueString);
            } else if(attribute.isNominal()) {
                String var13 = attribute.getMapping().mapIndex((int)value);
                statement.setString(counter, var13);
            } else if(Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), 9)) {
                if(Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), 11)) {
                    statement.setTime(counter, new Time((long)value));
                } else {
                    statement.setTimestamp(counter, new Timestamp((long)value));
                }
            } else {
                statement.setDouble(counter, value);
            }

            ++counter;
        }
    }

    public static int getRapidMinerTypeIndex(int sqlType) {
        switch(sqlType) {
            case -7:
            case -4:
            case -3:
            case -2:
            case 1:
            case 12:
            case 2000:
            case 2002:
                return 1;
            case -6:
            case -5:
            case 4:
            case 5:
                return 3;
            case -1:
            case 2004:
            case 2005:
                return 5;
            case 2:
                return 2;
            case 3:
            case 6:
            case 7:
            case 8:
                return 4;
            case 91:
                return 10;
            case 92:
                return 11;
            case 93:
                return 9;
            default:
                return 1;
        }
    }

    public static List<Attribute> createAttributes(ResultSet rs) throws SQLException {
        LinkedList attributes = new LinkedList();
        if(rs == null) {
            throw new IllegalArgumentException("Cannot create attributes: ResultSet must not be null!");
        } else {
            ResultSetMetaData metadata;
            try {
                metadata = rs.getMetaData();
            } catch (NullPointerException var7) {
                throw new RuntimeException("Could not create attribute list: ResultSet object seems closed.");
            }

            int numberOfColumns = metadata.getColumnCount();

            for(int column = 1; column <= numberOfColumns; ++column) {
                String name = metadata.getColumnLabel(column);
                Attribute attribute = AttributeFactory.createAttribute(name, getRapidMinerTypeIndex(metadata.getColumnType(column)));
                attributes.add(attribute);
            }

            return attributes;
        }
    }

    private boolean existsTable(TableName tableName) throws SQLException {
        ResultSet tableNames = this.connection.getMetaData().getTables(tableName.getCatalog(), tableName.getSchema(), tableName.getTableName(), (String[])null);
        Throwable var3 = null;

        boolean var5;
        try {
            boolean existsTable = tableNames.next();
            var5 = existsTable;
        } catch (Throwable var14) {
            var3 = var14;
            throw var14;
        } finally {
            if(tableNames != null) {
                if(var3 != null) {
                    try {
                        tableNames.close();
                    } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                    }
                } else {
                    tableNames.close();
                }
            }

        }

        return var5;
    }

    private boolean existsColumnInTable(String tableName, String columnName) throws SQLException {
        ResultSet columnNames = this.connection.getMetaData().getColumns((String)null, (String)null, tableName, columnName);
        Throwable var4 = null;

        boolean var5;
        try {
            var5 = columnNames.next();
        } catch (Throwable var14) {
            var4 = var14;
            throw var14;
        } finally {
            if(columnNames != null) {
                if(var4 != null) {
                    try {
                        columnNames.close();
                    } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                    }
                } else {
                    columnNames.close();
                }
            }

        }

        return var5;
    }

    public Map<TableName, List<ColumnIdentifier>> getAllTableMetaData() throws SQLException {
        return this.getAllTableMetaData((ProgressListener)null, 0, 0, true);
    }

    public Map<TableName, List<ColumnIdentifier>> getAllTableMetaData(ProgressListener progressListener, int minProgress, int maxProgress, boolean fetchColumns) throws SQLException {
        if(this.connection == null) {
            throw new SQLException("Could not retrieve all table names: no open connection to database \'" + this.databaseURL + "\' !");
        } else if(this.connection.isClosed()) {
            this.unregister();
            throw new SQLException("Could not retrieve all table names: connection is closed.");
        } else {
            DatabaseMetaData metaData = this.connection.getMetaData();
            String[] types;
            if(!"false".equals(ParameterService.getParameterValue("rapidminer.tools.db.assist.show_only_standard_tables"))) {
                types = new String[]{"TABLE"};
            } else {
                types = null;
            }

            LinkedList tableNameList = new LinkedList();
            ResultSet result = metaData.getTables((String)null, (String)null, (String)null, types);
            Throwable i = null;

            try {
                while(result.next()) {
                    String size = result.getString("TABLE_NAME");
                    String count = result.getString("TABLE_SCHEM");
                    String tableName = result.getString("TABLE_CAT");
                    String e = result.getString("REMARKS");
                    TableName tableNameObject = new TableName(size, count, tableName);
                    tableNameObject.setComment(e);
                    tableNameList.add(tableNameObject);
                }
            } catch (Throwable var24) {
                i = var24;
                throw var24;
            } finally {
                if(result != null) {
                    if(i != null) {
                        try {
                            result.close();
                        } catch (Throwable var22) {
                            i.addSuppressed(var22);
                        }
                    } else {
                        result.close();
                    }
                }

            }

            LinkedHashMap var26 = new LinkedHashMap();
            Iterator var27 = tableNameList.iterator();
            int var29 = tableNameList.size();
            int var28 = 0;

            while(var27.hasNext()) {
                TableName var30 = (TableName)var27.next();
                if(progressListener != null && var29 > 0) {
                    progressListener.setCompleted(var28 * (maxProgress - minProgress) / var29 + minProgress);
                }

                ++var28;
                if(fetchColumns) {
                    try {
                        List var31 = this.getAllColumnNames(var30, metaData);
                        var26.put(var30, var31);
                    } catch (SQLException var23) {
                        LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.DatabaseHandler.fetching_column_metadata_error", new Object[]{var30, var23}), var23);
                        var26.put(var30, Collections.emptyList());
                    }
                } else {
                    var26.put(var30, Collections.emptyList());
                }
            }

            return var26;
        }
    }

    public List<ColumnIdentifier> getAllColumnNames(TableName tableName, DatabaseMetaData metaData) throws SQLException {
        if(tableName == null) {
            throw new SQLException("Cannot read column names: table name must not be null!");
        } else {
            Statement statement = this.createStatement(false);
            Throwable var4 = null;

            LinkedList var71;
            try {
                ResultSet columnResult = metaData.getColumns(tableName.getCatalog(), tableName.getSchema(), tableName.getTableName(), "%");
                Throwable var6 = null;

                try {
                    LinkedList result;
                    try {
                        String var70;
                        LinkedList e;
                        for(e = new LinkedList(); columnResult.next(); e.add(new ColumnIdentifier(this, tableName, columnResult.getString("COLUMN_NAME"), columnResult.getInt("DATA_TYPE"), columnResult.getString("TYPE_NAME"), var70))) {
                            var70 = columnResult.getString("REMARKS");
                            if(var70 != null && var70.isEmpty()) {
                                var70 = null;
                            }
                        }

                        result = e;
                        return result;
                    } catch (SQLException var65) {
                        result = new LinkedList();
                        String emptySelect = "SELECT * FROM " + this.statementCreator.makeIdentifier(tableName) + " WHERE 0=1";
                        ResultSet emptyQueryResult = this.executeQuery(statement, (Operator)null, emptySelect);
                        Throwable var11 = null;

                        try {
                            ResultSetMetaData resultSetMetaData = emptyQueryResult.getMetaData();

                            for(int i = 0; i < resultSetMetaData.getColumnCount(); ++i) {
                                result.add(new ColumnIdentifier(this, tableName, resultSetMetaData.getColumnName(i + 1), resultSetMetaData.getColumnType(i + 1), resultSetMetaData.getColumnTypeName(i + 1), (String)null));
                            }

                            var71 = result;
                        } catch (Throwable var63) {
                            var11 = var63;
                            throw var63;
                        } finally {
                            if(emptyQueryResult != null) {
                                if(var11 != null) {
                                    try {
                                        emptyQueryResult.close();
                                    } catch (Throwable var62) {
                                        var11.addSuppressed(var62);
                                    }
                                } else {
                                    emptyQueryResult.close();
                                }
                            }

                        }
                    }
                } catch (Throwable var66) {
                    var6 = var66;
                    throw var66;
                } finally {
                    if(columnResult != null) {
                        if(var6 != null) {
                            try {
                                columnResult.close();
                            } catch (Throwable var61) {
                                var6.addSuppressed(var61);
                            }
                        } else {
                            columnResult.close();
                        }
                    }

                }
            } catch (Throwable var68) {
                var4 = var68;
                throw var68;
            } finally {
                if(statement != null) {
                    if(var4 != null) {
                        try {
                            statement.close();
                        } catch (Throwable var60) {
                            var4.addSuppressed(var60);
                        }
                    } else {
                        statement.close();
                    }
                }

            }

            return var71;
        }
    }

    public static DatabaseHandler getConnectedDatabaseHandler(Operator operator) throws OperatorException, SQLException {
        Process process = operator.getProcess();
        switch(operator.getParameterAsInt("define_connection")) {
            case 0:
                String repositoryName = null;
                if(process != null) {
                    RepositoryLocation entry = process.getRepositoryLocation();
                    if(entry != null) {
                        repositoryName = entry.getRepositoryName();
                    }
                }

                ConnectionEntry entry1 = DatabaseConnectionService.getConnectionEntry(operator.getParameterAsString("connection"), repositoryName, process != null?process.getRepositoryAccessor():null);
                if(entry1 == null) {
                    throw new UserError(operator, 318, new Object[]{operator.getParameterAsString("connection")});
                }

                return getConnectedDatabaseHandler(entry1);
            case 1:
            default:
                return getConnectedDatabaseHandler(operator.getParameterAsString("database_url"), operator.getParameterAsString("username"), operator.getParameterAsString("password"));
            case 2:
                String jndiName = operator.getParameterAsString("jndi_name");

                try {
                    InitialContext e = new InitialContext();
                    DataSource source = (DataSource)e.lookup(jndiName);
                    return getHandler(source.getConnection());
                } catch (NamingException var7) {
                    throw new OperatorException("Failed to lookup \'" + jndiName + "\': " + var7, var7);
                }
        }
    }

    public static TableName getSelectedTableName(ParameterHandler operator) throws UndefinedParameterError {
        return operator.getParameterAsBoolean("use_default_schema")?new TableName(operator.getParameterAsString("table_name")):new TableName(operator.getParameterAsString("table_name"), operator.getParameterAsString("schema_name"), (String)null);
    }

    public static ConnectionEntry getConnectionEntry(Operator operator) {
        Process process = operator.getProcess();
        String repositoryName = null;
        if(process != null) {
            RepositoryLocation repositoryLocation = process.getRepositoryLocation();
            if(repositoryLocation != null) {
                repositoryName = repositoryLocation.getRepositoryName();
            }
        }

        return getConnectionEntry(operator, repositoryName);
    }

    public static ConnectionEntry getConnectionEntry(ParameterHandler parameterHandler) {
        return getConnectionEntry(parameterHandler, (String)null);
    }

    public static ConnectionEntry getConnectionEntry(ParameterHandler parameterHandler, String repositoryName) {
        try {
            int connectionMode = parameterHandler.getParameterAsInt("define_connection");
            switch(connectionMode) {
                case 0:
                    return DatabaseConnectionService.getConnectionEntry(parameterHandler.getParameterAsString("connection"), repositoryName);
                case 1:
                    final String connectionUrl = parameterHandler.getParameterAsString("database_url");
                    final String connectionUsername = parameterHandler.getParameterAsString("username");
                    final char[] connectionPassword = parameterHandler.getParameterAsString("password").toCharArray();
                    return new ConnectionEntry("urlConnection", (JDBCProperties)DatabaseService.getJDBCProperties().get(parameterHandler.getParameterAsInt("database_system"))) {
                        public String getURL() {
                            return connectionUrl;
                        }

                        public String getUser() {
                            return connectionUsername;
                        }

                        public char[] getPassword() {
                            return connectionPassword;
                        }
                    };
                case 2:
                default:
                    return null;
            }
        } catch (UndefinedParameterError var6) {
            return null;
        }
    }

    public static List<ParameterType> getConnectionParameterTypes(ParameterHandler handler) {
        LinkedList types = new LinkedList();
        ParameterTypeCategory type = new ParameterTypeCategory("define_connection", "Indicates how the database connection should be specified.", CONNECTION_MODES, 0);
        type.setExpert(false);
        types.add(type);
        ParameterTypeDatabaseConnection type1 = new ParameterTypeDatabaseConnection("connection", "A predefined database connection.");
        type1.registerDependencyCondition(new EqualTypeCondition(handler, "define_connection", CONNECTION_MODES, true, new int[]{0}));
        type1.setExpert(false);
        types.add(type1);
        type = new ParameterTypeCategory("database_system", "The used database system.", DatabaseService.getDBSystemNames(), 0);
        type.registerDependencyCondition(new EqualTypeCondition(handler, "define_connection", CONNECTION_MODES, true, new int[]{1}));
        type.setExpert(false);
        types.add(type);
        ParameterTypeString type2 = new ParameterTypeString("database_url", "The URL connection string for the database, e.g. \'jdbc:mysql://foo.bar:portnr/database\'");
        type2.registerDependencyCondition(new EqualTypeCondition(handler, "define_connection", CONNECTION_MODES, true, new int[]{1}));
        type2.setExpert(false);
        types.add(type2);
        type2 = new ParameterTypeString("username", "The database username.");
        type2.registerDependencyCondition(new EqualTypeCondition(handler, "define_connection", CONNECTION_MODES, true, new int[]{1}));
        type2.setExpert(false);
        types.add(type2);
        ParameterTypePassword type3 = new ParameterTypePassword("password", "The password for the database.");
        type3.registerDependencyCondition(new EqualTypeCondition(handler, "define_connection", CONNECTION_MODES, true, new int[]{1}));
        type3.setExpert(false);
        types.add(type3);
//        type2 = new ParameterTypeString("jndi_name", "JNDI name for a data source.");
//        type2.registerDependencyCondition(new EqualTypeCondition(handler, "define_connection", CONNECTION_MODES, true, new int[]{2}));
//        type2.setExpert(false);
//        types.add(type2);
        return types;
    }

    public static List<ParameterType> getQueryParameterTypes(ParameterHandler handler, boolean tableOnly) {
        LinkedList types = new LinkedList();
        ParameterTypeCategory type = null;
        if(!tableOnly) {
            type = new ParameterTypeCategory("define_query", "Specifies whether the database query should be defined directly, through a file or implicitely by a given table name.", QUERY_MODES, 0);
            type.setExpert(false);
            types.add(type);
            ParameterTypeSQLQuery type1 = new ParameterTypeSQLQuery("query", "An SQL query.");
            type1.registerDependencyCondition(new EqualTypeCondition(handler, "define_query", QUERY_MODES, true, new int[]{0}));
            type1.setExpert(false);
            types.add(type1);
            ParameterTypeFile type2 = new ParameterTypeFile("query_file", "A file containing an SQL query.", (String)null, true);
            type2.registerDependencyCondition(new EqualTypeCondition(handler, "define_query", QUERY_MODES, true, new int[]{1}));
            type2.setExpert(false);
            types.add(type2);
        }

        ParameterTypeBoolean type3 = new ParameterTypeBoolean("use_default_schema", "If checked, the user\'s default schema will be used.", true);
        if(!tableOnly) {
            type3.registerDependencyCondition(new EqualTypeCondition(handler, "define_query", QUERY_MODES, true, new int[]{2}));
        }

        type3.setExpert(true);
        types.add(type3);
        ParameterTypeDatabaseSchema type4 = new ParameterTypeDatabaseSchema("schema_name", "The schema name to use, unless use_default_schema is true.", true);
        type4.registerDependencyCondition(new BooleanParameterCondition(handler, "use_default_schema", true, false));
        type4.setExpert(true);
        types.add(type4);
        ParameterTypeDatabaseTable type5 = new ParameterTypeDatabaseTable("table_name", "A database table.");
        if(!tableOnly) {
            type5.registerDependencyCondition(new EqualTypeCondition(handler, "define_query", QUERY_MODES, true, new int[]{2}));
        } else {
            type5.setOptional(false);
        }

        type5.setExpert(false);
        types.add(type5);
        return types;
    }

    public static List<ParameterType> getStatementPreparationParamterTypes(ParameterHandler handler) {
        LinkedList types = new LinkedList();
        ParameterTypeBoolean prepareParam = new ParameterTypeBoolean("prepare_statement", "If checked, the statement is prepared, and \'?\'-parameters can be filled in using the parameter \'parameters\'.", false);
        types.add(prepareParam);
        ParameterTypeTupel argumentType = new ParameterTypeTupel("parameter", "Parameter to insert when statement is prepared", new ParameterType[]{new ParameterTypeCategory("type", "SQL type to use for insertion.", SQL_TYPES, 0), new ParameterTypeString("parameter", "Parameter")});
        ParameterTypeEnumeration paramsParam = new ParameterTypeEnumeration("parameters", "Parameters to insert into \'?\' placeholders when statement is prepared.", argumentType);
        paramsParam.registerDependencyCondition(new BooleanParameterCondition(handler, "prepare_statement", false, true));
        types.add(paramsParam);
        return types;
    }

    public ResultSet executeStatement(String sql, boolean isQuery, Operator parameterHandler, Logger logger) throws SQLException, OperatorException {
        checkDatabaseConstraintOnOperator(parameterHandler, this.databaseURL);
        ResultSet resultSet = null;
        Object statement = null;

        try {
            if(parameterHandler.getParameterAsBoolean("prepare_statement")) {
                PreparedStatement prepared = this.getConnection().prepareStatement(sql);
                String[] parameters = ParameterTypeEnumeration.transformString2Enumeration(parameterHandler.getParameterAsString("parameters"));

                for(int i = 0; i < parameters.length; ++i) {
                    String[] argDescription = ParameterTypeTupel.transformString2Tupel(parameters[i]);
                    String sqlType = argDescription[0];
                    String replacementValue = argDescription[1];
                    if("VARCHAR".equals(sqlType)) {
                        prepared.setString(i + 1, replacementValue);
                    } else if("REAL".equals(sqlType)) {
                        try {
                            prepared.setDouble(i + 1, Double.parseDouble(replacementValue));
                        } catch (NumberFormatException var21) {
                            prepared.close();
                            throw new UserError(parameterHandler, 158, new Object[]{replacementValue, sqlType});
                        }
                    } else if("LONG".equals(sqlType)) {
                        try {
                            prepared.setLong(i + 1, Long.parseLong(replacementValue));
                        } catch (NumberFormatException var20) {
                            prepared.close();
                            throw new UserError(parameterHandler, 158, new Object[]{replacementValue, sqlType});
                        }
                    } else {
                        if(!"INTEGER".equals(sqlType)) {
                            prepared.close();
                            throw new OperatorException("Illegal data type: " + sqlType);
                        }

                        try {
                            prepared.setInt(i + 1, Integer.parseInt(replacementValue));
                        } catch (NumberFormatException var19) {
                            prepared.close();
                            throw new UserError(parameterHandler, 158, new Object[]{replacementValue, sqlType});
                        }
                    }
                }

                if(isQuery) {
                    resultSet = this.executeQuery(prepared, parameterHandler);
                } else {
                    this.execute(prepared, parameterHandler);
                }

                statement = prepared;
            } else {
                logger.info("Executing query: \'" + sql + "\'");
                statement = this.createStatement(false);
                if(isQuery) {
                    resultSet = this.executeQuery((Statement)statement, parameterHandler, sql);
                } else {
                    this.execute((Statement)statement, parameterHandler, sql);
                }
            }
        } finally {
            logger.fine("Query executed.");
            if(!isQuery && statement != null) {
                ((Statement)statement).close();
            }

        }

        return resultSet;
    }

    public String getDatabaseUrl() {
        return this.databaseURL;
    }

    /** @deprecated */
    @Deprecated
    public void updateTable(ExampleSet exampleSet, TableName selectedTableName, Set<Attribute> idAttributes, Logger logger) throws SQLException {
        try {
            this.updateTable((Operator)null, exampleSet, selectedTableName, idAttributes, logger);
        } catch (OperatorException var6) {
            ;
        }

    }

    public void updateTable(Operator operator, ExampleSet exampleSet, TableName selectedTableName, Set<Attribute> idAttributes, Logger logger) throws SQLException, OperatorException {
        if(exampleSet.getAttributes().size() == 0 && exampleSet.getAttributes().getId() == null) {
            throw new UserError(operator, 125, new Object[]{Integer.valueOf(0), Integer.valueOf(1)});
        } else {
            checkDatabaseConstraintOnOperator(operator, this.databaseURL);
            StatementCreator sc = new StatementCreator(this.getConnection());
            List allColumnNames = this.getAllColumnNames(selectedTableName, this.getConnection().getMetaData());
            LinkedList columnTableNames = new LinkedList();
            Iterator updateAttStringBuilder = allColumnNames.iterator();

            while(updateAttStringBuilder.hasNext()) {
                ColumnIdentifier updateIdStringBuilder = (ColumnIdentifier)updateAttStringBuilder.next();
                columnTableNames.add(updateIdStringBuilder.getColumnName());
            }

            StringBuilder var114 = new StringBuilder();
            StringBuilder var115 = new StringBuilder();
            StringBuilder insertAttStringBuilder = new StringBuilder();
            StringBuilder insertValStringBuilder = new StringBuilder();
            var114.append("SET ");
            insertAttStringBuilder.append('(');
            insertValStringBuilder.append('(');
            Iterator it = exampleSet.getAttributes().allAttributes();
            int attCount = 0;
            LinkedList attList = new LinkedList();
            LinkedList allAttList = new LinkedList();
            LinkedList idAttNameList = new LinkedList();
            Iterator updateStatementString = idAttributes.iterator();

            while(updateStatementString.hasNext()) {
                Attribute insertStatementString = (Attribute)updateStatementString.next();
                idAttNameList.add(insertStatementString.getName());
            }

            Attribute var118;
            String var116;
            for(; it.hasNext(); allAttList.add(var118)) {
                var118 = (Attribute)it.next();
                var116 = var118.getName();
                if(!columnTableNames.contains(var116)) {
                    throw new SQLException("Table does not contain column for attribute " + var116 + "!");
                }

                if(!idAttNameList.contains(var116)) {
                    ++attCount;
                    attList.add(var118);
                    var114.append(sc.makeIdentifier(var116) + " = ?");
                    var114.append(", ");
                    insertAttStringBuilder.append(sc.makeIdentifier(var116) + ", ");
                    insertValStringBuilder.append("?, ");
                } else {
                    insertAttStringBuilder.append(sc.makeIdentifier(var116) + ", ");
                    insertValStringBuilder.append("?, ");
                    var115.append(sc.makeIdentifier(var116) + " = ?");
                    var115.append(" AND ");
                }
            }

            if(var114.substring(var114.length() - 2, var114.length()).equals(", ")) {
                var114.delete(var114.length() - 2, var114.length());
            }

            if(var115.substring(var115.length() - 5, var115.length()).equals(" AND ")) {
                var115.delete(var115.length() - 5, var115.length());
            }

            if(insertAttStringBuilder.substring(insertAttStringBuilder.length() - 2, insertAttStringBuilder.length()).equals(", ")) {
                insertAttStringBuilder.delete(insertAttStringBuilder.length() - 2, insertAttStringBuilder.length());
                insertAttStringBuilder.append(')');
            }

            if(insertValStringBuilder.substring(insertValStringBuilder.length() - 2, insertValStringBuilder.length()).equals(", ")) {
                insertValStringBuilder.delete(insertValStringBuilder.length() - 2, insertValStringBuilder.length());
                insertValStringBuilder.append(')');
            }

            String var117 = "UPDATE " + sc.makeIdentifier(selectedTableName) + " " + var114.toString() + " WHERE " + var115.toString();
            var116 = "INSERT INTO " + sc.makeIdentifier(selectedTableName) + " " + insertAttStringBuilder.toString() + " VALUES " + insertValStringBuilder.toString();
            String selectStatementString = "SELECT COUNT(*) FROM " + sc.makeIdentifier(selectedTableName) + " WHERE " + var115.toString();
            int counter = 0;
            PreparedStatement prepUpdateStatement = this.createPreparedStatement(var117, false);
            Throwable var23 = null;

            try {
                PreparedStatement prepInsertStatement = this.createPreparedStatement(var116, false);
                Throwable var25 = null;

                try {
                    PreparedStatement prepSelectStatement = this.createPreparedStatement(selectStatementString, false);
                    Throwable var27 = null;

                    try {
                        Iterator var28 = exampleSet.iterator();

                        while(var28.hasNext()) {
                            Example ex = (Example)var28.next();
                            if(operator != null) {
                                ++counter;
                                if(counter % 100 == 0) {
                                    operator.getProgress().step(100);
                                    counter = 0;
                                }
                            }

                            int idCount = 0;
                            Iterator updatedRowCount = idAttributes.iterator();

                            Attribute rs;
                            while(updatedRowCount.hasNext()) {
                                rs = (Attribute)updatedRowCount.next();
                                if(rs.isNumerical()) {
                                    ++idCount;
                                    prepUpdateStatement.setDouble(attCount + idCount, ex.getValue(rs));
                                    prepInsertStatement.setDouble(allAttList.indexOf(rs) + 1, ex.getValue(rs));
                                    prepSelectStatement.setDouble(idAttNameList.indexOf(rs.getName()) + 1, ex.getValue(rs));
                                } else if(rs.isNominal()) {
                                    ++idCount;
                                    prepUpdateStatement.setString(attCount + idCount, ex.getValueAsString(rs));
                                    prepInsertStatement.setString(allAttList.indexOf(rs) + 1, ex.getValueAsString(rs));
                                    prepSelectStatement.setString(idAttNameList.indexOf(rs.getName()) + 1, ex.getValueAsString(rs));
                                } else if(rs.getValueType() == 10) {
                                    Date value = new Date((new Double(ex.getValue(rs))).longValue());
                                    ++idCount;
                                    prepUpdateStatement.setDate(attCount + idCount, value);
                                    prepInsertStatement.setDate(allAttList.indexOf(rs) + 1, value);
                                    prepSelectStatement.setDate(idAttNameList.indexOf(rs.getName()) + 1, value);
                                } else if(rs.getValueType() == 11) {
                                    Time var121 = new Time((new Double(ex.getValue(rs))).longValue());
                                    ++idCount;
                                    prepUpdateStatement.setTime(attCount + idCount, var121);
                                    prepInsertStatement.setTime(allAttList.indexOf(rs) + 1, var121);
                                    prepSelectStatement.setTime(idAttNameList.indexOf(rs.getName()) + 1, var121);
                                } else if(rs.getValueType() == 9) {
                                    Timestamp var119 = new Timestamp((new Double(ex.getValue(rs))).longValue());
                                    ++idCount;
                                    prepUpdateStatement.setTimestamp(attCount + idCount, var119);
                                    prepInsertStatement.setTimestamp(allAttList.indexOf(rs) + 1, var119);
                                    prepSelectStatement.setTimestamp(idAttNameList.indexOf(rs.getName()) + 1, var119);
                                }
                            }

                            updatedRowCount = attList.iterator();

                            while(updatedRowCount.hasNext()) {
                                rs = (Attribute)updatedRowCount.next();
                                double var120 = ex.getValue(rs);
                                if(Double.isNaN(var120)) {
                                    int timestamp = this.statementCreator.getSQLTypeForRMValueType(rs.getValueType()).getDataType();
                                    prepUpdateStatement.setNull(attList.indexOf(rs) + 1, timestamp);
                                    prepInsertStatement.setNull(allAttList.indexOf(rs) + 1, timestamp);
                                } else if(rs.isNumerical()) {
                                    prepUpdateStatement.setDouble(attList.indexOf(rs) + 1, ex.getValue(rs));
                                    prepInsertStatement.setDouble(allAttList.indexOf(rs) + 1, ex.getValue(rs));
                                } else if(rs.isNominal()) {
                                    prepUpdateStatement.setString(attList.indexOf(rs) + 1, ex.getValueAsString(rs));
                                    prepInsertStatement.setString(allAttList.indexOf(rs) + 1, ex.getValueAsString(rs));
                                } else if(rs.getValueType() == 10) {
                                    Date var126 = new Date((new Double(ex.getValue(rs))).longValue());
                                    prepUpdateStatement.setDate(attList.indexOf(rs) + 1, var126);
                                    prepInsertStatement.setDate(allAttList.indexOf(rs) + 1, var126);
                                } else if(rs.getValueType() == 11) {
                                    Time var124 = new Time((new Double(ex.getValue(rs))).longValue());
                                    prepUpdateStatement.setTime(attList.indexOf(rs) + 1, var124);
                                    prepInsertStatement.setTime(allAttList.indexOf(rs) + 1, var124);
                                } else if(rs.getValueType() == 9) {
                                    Timestamp var125 = new Timestamp((new Double(ex.getValue(rs))).longValue());
                                    prepUpdateStatement.setTimestamp(attList.indexOf(rs) + 1, var125);
                                    prepInsertStatement.setTimestamp(allAttList.indexOf(rs) + 1, var125);
                                }
                            }

                            int var123 = 0;
                            if(attCount > 0) {
                                if(logger != null) {
                                    logger.fine(prepUpdateStatement.toString());
                                }

                                var123 = this.executeUpdate(prepUpdateStatement, operator);
                            } else {
                                ResultSet var122 = this.executeQuery(prepSelectStatement, operator);
                                Throwable var127 = null;

                                try {
                                    if(var122.next()) {
                                        var123 = var122.getInt(1);
                                    }
                                } catch (Throwable var106) {
                                    var127 = var106;
                                    throw var106;
                                } finally {
                                    if(var122 != null) {
                                        if(var127 != null) {
                                            try {
                                                var122.close();
                                            } catch (Throwable var105) {
                                                var127.addSuppressed(var105);
                                            }
                                        } else {
                                            var122.close();
                                        }
                                    }

                                }
                            }

                            if(var123 <= 0) {
                                if(logger != null) {
                                    logger.fine(prepInsertStatement.toString());
                                }

                                this.executeUpdate(prepInsertStatement, operator);
                            }
                        }
                    } catch (Throwable var108) {
                        var27 = var108;
                        throw var108;
                    } finally {
                        if(prepSelectStatement != null) {
                            if(var27 != null) {
                                try {
                                    prepSelectStatement.close();
                                } catch (Throwable var104) {
                                    var27.addSuppressed(var104);
                                }
                            } else {
                                prepSelectStatement.close();
                            }
                        }

                    }
                } catch (Throwable var110) {
                    var25 = var110;
                    throw var110;
                } finally {
                    if(prepInsertStatement != null) {
                        if(var25 != null) {
                            try {
                                prepInsertStatement.close();
                            } catch (Throwable var103) {
                                var25.addSuppressed(var103);
                            }
                        } else {
                            prepInsertStatement.close();
                        }
                    }

                }
            } catch (Throwable var112) {
                var23 = var112;
                throw var112;
            } finally {
                if(prepUpdateStatement != null) {
                    if(var23 != null) {
                        try {
                            prepUpdateStatement.close();
                        } catch (Throwable var102) {
                            var23.addSuppressed(var102);
                        }
                    } else {
                        prepUpdateStatement.close();
                    }
                }

            }

        }
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private boolean execute(Statement statement, Operator operator, String sql) throws SQLException {
        this.addCancelListener(statement, operator);
        return statement.execute(sql);
    }

    private boolean execute(PreparedStatement statement, Operator operator) throws SQLException {
        this.addCancelListener(statement, operator);
        return statement.execute();
    }

    private int executeUpdate(Statement statement, Operator operator, String sql) throws SQLException {
        this.addCancelListener(statement, operator);
        return statement.executeUpdate(sql);
    }

    private int executeUpdate(PreparedStatement statement, Operator operator) throws SQLException {
        this.addCancelListener(statement, operator);
        return statement.executeUpdate();
    }

    private ResultSet executeQuery(Statement statement, Operator operator, String sql) throws SQLException {
        this.addCancelListener(statement, operator);
        return statement.executeQuery(sql);
    }

    private ResultSet executeQuery(PreparedStatement statement, Operator operator) throws SQLException {
        this.addCancelListener(statement, operator);
        return statement.executeQuery();
    }

    private void addCancelListener(Statement statement, Operator operator) {
        if(operator != null) {
            Process process = operator.getProcess();
            if(process != null) {
                DatabaseHandler.StatementCancelListener listener = new DatabaseHandler.StatementCancelListener(statement, operator);
                process.addProcessStateListener(listener);
            }
        }

    }

    public void close() throws SQLException {
        this.disconnect();
    }

    private static void checkDatabaseConstraintOnOperator(Operator operator, String databaseURL) throws IllegalArgumentException, DatabaseConstraintViolationException {
        if(databaseURL == null) {
            throw new IllegalArgumentException("databaseURL must not be null!");
        } else {
            if(!DatabaseService.isDatabaseURLOpenSource(databaseURL)) {
                LicenseConstraintViolation violation = LicenseManagerRegistry.INSTANCE.get().checkConstraintViolation(ProductConstraintManager.INSTANCE.getProduct(), LicenseConstants.CONNECTORS_CONSTRAINT, "COMMMERCIAL_DATABASES", true);
                if(violation != null) {
                    throw new DatabaseConstraintViolationException(operator, databaseURL, violation);
                }
            }

        }
    }

    private class StatementCancelListener implements ProcessStateListener {
        private Statement statement;
        private Operator operator;

        public StatementCancelListener(Statement statement, Operator operator) {
            this.statement = statement;
            this.operator = operator;
        }

        public void stopped(Process process) {
            if(this.statement != null) {
                try {
                    if(this.operator.isRunning() && !DatabaseHandler.this.isCancelled()) {
                        DatabaseHandler.this.setCancelled(true);
                        this.statement.cancel();
                    }
                } catch (SQLException var6) {
                    DatabaseHandler.this.setCancelled(false);
                    LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.jdbc.DatabaseHandler.cancel_request_error");
                } finally {
                    this.operator.getProcess().removeProcessStateListener(this);
                }
            }

        }

        public void started(Process process) {
        }

        public void resumed(Process process) {
        }

        public void paused(Process process) {
        }
    }
}