package com.rapidminer.extension.jdbc.operator.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.extension.jdbc.PluginInitJDBCConnectors;
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.StatementCreator;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.extension.jdbc.tools.jdbc.db.AttributeStore;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.OperatorProgress;
import com.rapidminer.operator.ProcessStoppedException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.io.AbstractExampleSource;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.Tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseDataReader extends AbstractExampleSource implements ConnectionProvider {
    public static final String PROPERTY_EVALUATE_MD_FOR_SQL_QUERIES = "rapidminer.gui.evaluate_meta_data_for_sql_queries";
    private DatabaseHandler databaseHandler;
    private AttributeStore attributeStore;
    private String table;

    public void setTable(String table) { this.table = table; }
    public void setAttributeStore(AttributeStore attributeStore) {
        this.attributeStore = attributeStore;
    }

    public DatabaseDataReader(OperatorDescription description) {
        super(description);
    }

    public ExampleSet read() throws OperatorException {
        ExampleSet var2;
        try {
            ExampleSet result = super.read();
            var2 = result;
        } finally {
            if(this.databaseHandler != null && this.databaseHandler.getConnection() != null) {
                try {
                    this.databaseHandler.getConnection().close();
                } catch (SQLException var9) {
                    this.getLogger().log(Level.WARNING, "Error closing database connection: " + var9, var9);
                }
            }

        }

        return var2;
    }

    protected ResultSet getResultSet() throws OperatorException {
        try {
            this.databaseHandler = DatabaseHandler.getConnectedDatabaseHandler(this);
            String sqle = this.getQuery(this.databaseHandler.getStatementCreator());
            if(sqle == null) {
                throw new UserError(this, 202, new Object[]{"query", "query_file", "table_name"});
            } else {
                return this.databaseHandler.executeStatement(sqle, true, this, this.getLogger());
            }
        } catch (SQLException var2) {
            if(this.databaseHandler != null && this.databaseHandler.isCancelled()) {
                throw new ProcessStoppedException(this);
            } else {
                throw new UserError(this, var2, 304, new Object[]{var2.getMessage()});
            }
        }
    }

    public ExampleSet createExampleSet() throws OperatorException {
        ResultSet resultSet = this.getResultSet();

        ExampleSet exampleSet;
        try {
            if (attributeStore == null) {
                // just read data, normal jdbc
                exampleSet = DatabaseDataReader.createExampleTable(resultSet,
                DatabaseDataReader.getAttributes(resultSet.getMetaData()),
                getParameterAsInt("datamanagement"),
                getLogger(),
                getProgress()).createExampleSet();
            } else {
                // here is for inceptor meta data
                exampleSet = attributeStore.getExampleSet(databaseHandler.getConnection(), table, resultSet, this);
            }

        } catch (SQLException var11) {
            throw new UserError(this, var11, 304, new Object[]{var11.getMessage()});
        } finally {
            try {
                resultSet.close();
            } catch (SQLException var10) {
                this.getLogger().log(Level.WARNING, "DB error closing result set: " + var10, var10);
            }

        }

        this.getProgress().complete();
        return exampleSet;
    }

    public MetaData getGeneratedMetaData() throws OperatorException {
        ExampleSetMetaData metaData = new ExampleSetMetaData();

        try {
            this.databaseHandler = DatabaseHandler.getConnectedDatabaseHandler(this);
            switch(this.getParameterAsInt("define_query")) {
                case 0:
                case 1:
                default:
                    if(!"false".equals(ParameterService.getParameterValue("rapidminer.gui.evaluate_meta_data_for_sql_queries"))) {
                        String query1 = this.getQuery(this.databaseHandler.getStatementCreator());
                        PreparedStatement prepared1 = this.databaseHandler.getConnection().prepareStatement(query1);
                        List attributes = getAttributes(prepared1.getMetaData());
                        Iterator var6 = attributes.iterator();

                        while(var6.hasNext()) {
                            Attribute att = (Attribute)var6.next();
                            metaData.addAttribute(new AttributeMetaData(att));
                        }

                        prepared1.close();
                    }
                    break;
                case 2:
                    List e = this.databaseHandler.getAllColumnNames(DatabaseHandler.getSelectedTableName(this), this.databaseHandler.getConnection().getMetaData());
                    Iterator query = e.iterator();

                    while(query.hasNext()) {
                        ColumnIdentifier prepared = (ColumnIdentifier)query.next();
                        metaData.addAttribute(new AttributeMetaData(prepared.getColumnName(), DatabaseHandler.getRapidMinerTypeIndex(prepared.getSqlType())));
                    }
            }
        } catch (SQLException var16) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.operator.io.DatabaseDataReader.fetching_meta_data_error", new Object[]{var16}), var16);
        } finally {
            try {
                if(this.databaseHandler != null && this.databaseHandler.getConnection() != null) {
                    this.databaseHandler.disconnect();
                }
            } catch (SQLException var15) {
                this.getLogger().log(Level.WARNING, "DB error closing connection: " + var15, var15);
            }

        }

        return metaData;
    }

    /** @deprecated */
    @Deprecated
    public static MemoryExampleTable createExampleTable(ResultSet resultSet, List<Attribute> attributes, int dataManagementType, Logger logger) throws SQLException, OperatorException {
        return createExampleTable(resultSet, attributes, dataManagementType, logger, (OperatorProgress)null);
    }

    public static MemoryExampleTable createExampleTable(ResultSet resultSet, List<Attribute> attributes, int dataManagementType, Logger logger, OperatorProgress opProg) throws SQLException, OperatorException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Attribute[] attributeArray = (Attribute[])attributes.toArray(new Attribute[attributes.size()]);
        MemoryExampleTable table = new MemoryExampleTable(attributes);
        DataRowFactory factory = new DataRowFactory(dataManagementType, '.');
        int counter = 0;

        while(resultSet.next()) {
            DataRow dataRow = factory.create(attributeArray.length);

            for(int i = 1; i <= metaData.getColumnCount(); ++i) {
                Attribute attribute = attributeArray[i - 1];
                int valueType = attribute.getValueType();
                double value;
                if(Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, 9)) {
                    Timestamp var30 = resultSet.getTimestamp(i);
                    if(resultSet.wasNull()) {
                        value = 0.0D / 0.0;
                    } else {
                        value = (double)var30.getTime();
                    }
                } else if(Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, 2)) {
                    value = resultSet.getDouble(i);
                    if(resultSet.wasNull()) {
                        value = 0.0D / 0.0;
                    }
                } else if(!Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, 1)) {
                    if(logger != null) {
                        logger.warning("Unknown column type: " + attribute);
                    }

                    value = 0.0D / 0.0;
                } else {
                    String valueString;
                    if(metaData.getColumnType(i) == 2005) {
                        Clob clob = resultSet.getClob(i);
                        if(clob != null) {
                            BufferedReader in = null;

                            try {
                                in = new BufferedReader(clob.getCharacterStream());
                                String line = null;

                                try {
                                    StringBuffer e = new StringBuffer();

                                    while((line = in.readLine()) != null) {
                                        e.append(line + "\n");
                                    }

                                    valueString = e.toString();
                                } catch (IOException var28) {
                                    throw new OperatorException("Database error occurred: " + var28, var28);
                                }
                            } finally {
                                try {
                                    in.close();
                                } catch (IOException var27) {
                                    ;
                                }

                            }
                        } else {
                            valueString = null;
                        }
                    } else {
                        valueString = resultSet.getString(i);
                    }

                    if(!resultSet.wasNull() && valueString != null) {
                        value = (double)attribute.getMapping().mapString(valueString);
                    } else {
                        value = 0.0D / 0.0;
                    }
                }

                dataRow.set(attribute, value);
            }

            table.addDataRow(dataRow);
            if(opProg != null && opProg.getTotal() > -1) {
                ++counter;
                if(counter % 100 == 0) {
                    opProg.step(100);
                    counter = 0;
                }
            }
        }

        return table;
    }


    public static List<Attribute> getAttributes(ResultSetMetaData metaData) throws SQLException {
        LinkedList result = new LinkedList();
        if(metaData != null) {
            HashMap duplicateNameMap = new HashMap();

            for(int columnIndex = 1; columnIndex <= metaData.getColumnCount(); ++columnIndex) {
                String dbColumnName = metaData.getColumnLabel(columnIndex);
                String columnName = dbColumnName;
                Integer duplicateCount = (Integer)duplicateNameMap.get(dbColumnName);
                boolean isUnique = duplicateCount == null;
                if(isUnique) {
                    duplicateNameMap.put(dbColumnName, new Integer(1));
                } else {
                    while(!isUnique) {
                        duplicateCount = new Integer(duplicateCount.intValue() + 1);
                        columnName = dbColumnName + "_" + (duplicateCount.intValue() - 1);
                        isUnique = duplicateNameMap.get(columnName) == null;
                    }

                    duplicateNameMap.put(dbColumnName, duplicateCount);
                }

                int attributeType = DatabaseHandler.getRapidMinerTypeIndex(metaData.getColumnType(columnIndex));
                Attribute attribute = AttributeFactory.createAttribute(columnName, attributeType);
                attribute.getAnnotations().setAnnotation("sql_type", metaData.getColumnTypeName(columnIndex));
                result.add(attribute);
            }
        }

        return result;
    }

    private String getQuery(StatementCreator sc) throws OperatorException {
        switch(this.getParameterAsInt("define_query")) {
            case 0:
                String tableName2 = this.getParameterAsString("query");
                if(tableName2 != null) {
                    tableName2 = tableName2.trim();
                }

                return tableName2;
            case 1:
                File tableName = this.getParameterAsFile("query_file");
                if(tableName != null) {
                    String query = null;

                    try {
                        query = Tools.readTextFile(tableName);
                    } catch (IOException var5) {
                        throw new UserError(this, var5, 302, new Object[]{tableName, var5.getMessage()});
                    }

                    if(query != null && query.trim().length() != 0) {
                        return query;
                    }

                    throw new UserError(this, 305, new Object[]{tableName});
                }
            case 2:
                TableName tableName1 = DatabaseHandler.getSelectedTableName(this);
                if (DatabaseHandler.getConnectionEntry(this).getURL().startsWith("jdbc:hive")) {
                    return "SELECT * FROM " + sc.makeIdentifier(tableName1)
                            + " LIMIT "
                            + ParameterService.getParameterValue(PluginInitJDBCConnectors.MIDAS_LIMIT);
                } else {
                    return "SELECT * FROM " + sc.makeIdentifier(tableName1);
                }
            default:
                return null;
        }
    }

    public ConnectionEntry getConnectionEntry() {
        return DatabaseHandler.getConnectionEntry(this);
    }

    protected void addAnnotations(ExampleSet result) {
        try {
            if(this.databaseHandler != null) {
                result.getAnnotations().setAnnotation("Source", this.getQuery(this.databaseHandler.getStatementCreator()));
            }
        } catch (OperatorException var3) {
            ;
        }

    }

    protected boolean isMetaDataCacheable() {
        return true;
    }

    public List<ParameterType> getParameterTypes() {
        List list = super.getParameterTypes();
        list.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        list.addAll(DatabaseHandler.getQueryParameterTypes(this, false));
        list.addAll(DatabaseHandler.getStatementPreparationParamterTypes(this));
        list.add(new ParameterTypeCategory("datamanagement", "Determines, how the data is represented internally.", DataRowFactory.TYPE_NAMES, 0, false));
        return list;
    }

    public long count() throws OperatorException {
        ResultSet resultSet = null;
        try {
            this.databaseHandler = DatabaseHandler.getConnectedDatabaseHandler(this);
            String sqle = this.getCountQuery(this.databaseHandler.getStatementCreator());
            if(sqle == null) {
                throw new UserError(this, 202, new Object[]{"query", "query_file", "table_name"});
            } else {
                resultSet = this.databaseHandler.executeStatement(sqle, true, this, this.getLogger());
                long count = -1;
                while (resultSet.next()) {
                    count = resultSet.getLong(1);
                }
                return count;
            }
        } catch (SQLException var2) {
            if(this.databaseHandler != null && this.databaseHandler.isCancelled()) {
                throw new ProcessStoppedException(this);
            } else {
                throw new UserError(this, var2, 304, new Object[]{var2.getMessage()});
            }
        } finally {
            try {
                resultSet.close();
                databaseHandler.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getCountQuery(StatementCreator statementCreator) {
        TableName tableName1 = null;
        try {
            tableName1 = DatabaseHandler.getSelectedTableName(this);
        } catch (UndefinedParameterError undefinedParameterError) {
            throw new RuntimeException(undefinedParameterError);
        }
        return "SELECT count(1) FROM " + statementCreator.makeIdentifier(tableName1);
    }
}
