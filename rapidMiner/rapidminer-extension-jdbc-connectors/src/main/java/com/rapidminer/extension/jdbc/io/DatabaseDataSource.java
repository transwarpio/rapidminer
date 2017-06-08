package com.rapidminer.extension.jdbc.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.core.io.data.DataSet;
import com.rapidminer.core.io.data.DataSetException;
import com.rapidminer.core.io.data.DataSetMetaData;
import com.rapidminer.core.io.data.source.DataSource;
import com.rapidminer.core.io.data.source.DataSourceConfiguration;
import com.rapidminer.extension.jdbc.io.DatabaseDataSet;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.studio.io.data.DefaultDataSetMetaData;
import com.rapidminer.studio.io.data.internal.ResultSetAdapterUtils;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

class DatabaseDataSource implements DataSource {
    private static final String QUERY_KEY = "query";
    private static final String CONNECTION_PASSWORD_HASH_KEY = "connection.password_hash";
    private static final String CONNECTION_USER_KEY = "connection.user";
    private static final String CONNECTION_URL_KEY = "connection.url";
    private static final String CONNECTION_NAME_KEY = "connection.name";
    private Map<String, String> lastConfigurationForMetaData = null;
    private Map<String, String> dataSetConstructionConfiguration = null;
    private Map<String, String> resultSetConstructionConfiguration = null;
    private DataSetMetaData metaData = null;
    private DatabaseDataSet dataSet;
    private Statement statement;
    private ResultSet resultSet;
    private ConnectionEntry databaseConnection;
    private String query;

    DatabaseDataSource() {
    }

    public DataSet getData() throws DataSetException {
        DatabaseDataSet wholeDataSet = this.getCachedDataSet();
        wholeDataSet.setLimit(-1);
        return wholeDataSet;
    }

    private DatabaseDataSet getCachedDataSet() throws DataSetException {
        boolean configurationChanged = this.dataSetConstructionConfiguration != null && !this.dataSetConstructionConfiguration.equals(this.getConstructionConfiguration());
        if(this.dataSet == null || configurationChanged) {
            if(this.dataSet != null) {
                this.dataSet.close();
                this.dataSet = null;
            }

            try {
                this.dataSet = new DatabaseDataSet(this, -1);
                this.dataSetConstructionConfiguration = this.getConstructionConfiguration();
            } catch (SQLException var3) {
                this.dataSetConstructionConfiguration = null;
                throw new DataSetException(var3.getMessage(), var3);
            }
        }

        return this.dataSet;
    }

    public DataSet getPreview(int maxPreviewRows) throws DataSetException {
        DatabaseDataSet previewDataSet = this.getCachedDataSet();
        previewDataSet.setLimit(maxPreviewRows);
        return previewDataSet;
    }

    public void close() {
        if(this.resultSet != null) {
            try {
                this.resultSet.close();
            } catch (SQLException var4) {
                ;
            }
        }

        if(this.statement != null) {
            try {
                this.statement.close();
            } catch (SQLException var3) {
                ;
            }
        }

        if(this.dataSet != null) {
            try {
                this.dataSet.close();
            } catch (DataSetException var2) {
                ;
            }
        }

    }

    ConnectionEntry getDatabaseConnection() {
        return this.databaseConnection;
    }

    void setDatabaseConnection(ConnectionEntry connection) {
        this.databaseConnection = connection;
    }

    public DataSetMetaData getMetadata() throws DataSetException {
        if(this.lastConfigurationForMetaData == null || !this.lastConfigurationForMetaData.equals(this.getConstructionConfiguration())) {
            try {
                this.configureMetaData();
                this.lastConfigurationForMetaData = this.getConstructionConfiguration();
            } catch (DataSetException var2) {
                this.lastConfigurationForMetaData = null;
                throw var2;
            }
        }

        return this.metaData;
    }

    ResultSet getCachedResultSet() throws SQLException {
        boolean configurationChanged = this.resultSetConstructionConfiguration == null || !this.resultSetConstructionConfiguration.equals(this.getConstructionConfiguration());
        if(configurationChanged) {
            if(this.resultSet != null) {
                try {
                    this.resultSet.close();
                } catch (SQLException var4) {
                    ;
                }
            }

            if(this.connectionChanged()) {
                try {
                    if(this.statement != null) {
                        this.statement.close();
                    }
                } catch (SQLException var3) {
                    ;
                }

                DatabaseHandler handler = DatabaseHandler.getConnectedDatabaseHandler(this.getDatabaseConnection());
                this.statement = handler.createStatement(false);
            }

            this.getNewResultSet();
        }

        return this.resultSet;
    }

    ResultSet getNewResultSet() throws SQLException {
        this.resultSet = this.statement.executeQuery(this.query);
        this.resultSetConstructionConfiguration = this.getConstructionConfiguration();
        return this.resultSet;
    }

    private boolean connectionChanged() {
        return this.resultSetConstructionParameterChanged("connection.url") || this.resultSetConstructionParameterChanged("connection.user") || this.resultSetConstructionParameterChanged("connection.password_hash");
    }

    private boolean resultSetConstructionParameterChanged(String parameter) {
        if(this.resultSetConstructionConfiguration == null) {
            return true;
        } else {
            String oldParameter = (String)this.resultSetConstructionConfiguration.get(parameter);
            return oldParameter == null || !oldParameter.equals(this.getConfiguration().getParameters().get(parameter));
        }
    }

    void setQuery(String query) {
        this.query = query;
    }

    String getQuery() {
        return this.query;
    }

    void configureMetaData() throws DataSetException {
        try {
            ResultSetMetaData e = this.getCachedResultSet().getMetaData();
            LinkedList columnNames = new LinkedList();
            LinkedList columnTypes = new LinkedList();
            HashSet usedNames = new HashSet();

            for(int columnIndex = 1; columnIndex <= e.getColumnCount(); ++columnIndex) {
                String dbColumnName = e.getColumnLabel(columnIndex);
                String columnName = dbColumnName;
                int valueType;
                if(usedNames.contains(dbColumnName)) {
                    valueType = 1;

                    do {
                        columnName = dbColumnName + "_" + valueType;
                        ++valueType;
                    } while(usedNames.contains(columnName));
                }

                usedNames.add(columnName);
                columnNames.add(columnName);
                valueType = DatabaseHandler.getRapidMinerTypeIndex(e.getColumnType(columnIndex));
                columnTypes.add(ResultSetAdapterUtils.transformValueType(valueType));
            }

            this.metaData = new DefaultDataSetMetaData(columnNames, columnTypes);
        } catch (SQLException var9) {
            throw new DataSetException(var9.getMessage(), var9);
        }
    }

    private Map<String, String> getConstructionConfiguration() {
        HashMap configurationMap = new HashMap(4);
        configurationMap.put("query", this.query);
        configurationMap.put("connection.url", this.databaseConnection.getURL());
        configurationMap.put("connection.user", this.databaseConnection.getUser());
        configurationMap.put("connection.password_hash", Arrays.hashCode(this.databaseConnection.getPassword()) + "");
        return configurationMap;
    }

    public DataSourceConfiguration getConfiguration() {
        final HashMap parameterMap = new HashMap();
        parameterMap.put("query", this.query);
        parameterMap.put("connection.name", this.databaseConnection.getName());
        return new DataSourceConfiguration() {
            public String getVersion() {
                return "0";
            }

            public Map<String, String> getParameters() {
                return parameterMap;
            }
        };
    }

    public void configure(DataSourceConfiguration configuration) throws DataSetException {
    }
}
