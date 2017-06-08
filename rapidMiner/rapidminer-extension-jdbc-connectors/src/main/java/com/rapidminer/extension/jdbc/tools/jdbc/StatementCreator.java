package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class StatementCreator {
    private Map<Integer, DataTypeSyntaxInformation> typeMap;
    private String identifierQuote;
    private long defaultVarCharLength;

    public StatementCreator(Connection connection) throws SQLException {
        this(connection, -1L);
    }

    StatementCreator(Connection connection, long defaultVarcharLength) throws SQLException {
        this.typeMap = new LinkedHashMap();
        this.defaultVarCharLength = -1L;
        this.defaultVarCharLength = defaultVarcharLength;
        this.buildTypeMap(connection);
    }

    public void setDefaultVarcharLength(long defaultVarcharLength) {
        this.defaultVarCharLength = defaultVarcharLength;
    }

    private void buildTypeMap(Connection con) throws SQLException {
        DatabaseMetaData dbMetaData = con.getMetaData();
        this.identifierQuote = dbMetaData.getIdentifierQuoteString();
        LogService.getRoot().log(Level.FINE, "com.rapidminer.tools.jdbc.StatementCreator.initialization_of_quote_character", this.identifierQuote);
        HashMap dataTypeToMDMap = new HashMap();
        ResultSet typesResult = null;

        try {
            typesResult = dbMetaData.getTypeInfo();

            while(typesResult.next()) {
                if(!dbMetaData.getDatabaseProductName().contains("PostgreSQL") || !typesResult.getString("TYPE_NAME").equals("name")) {
                    DataTypeSyntaxInformation dtmd = new DataTypeSyntaxInformation(typesResult, dbMetaData.getDatabaseProductName());
                    if(!dataTypeToMDMap.containsKey(Integer.valueOf(dtmd.getDataType())) || this.isUCanAccessVarchar(dbMetaData, dataTypeToMDMap, dtmd)) {
                        dataTypeToMDMap.put(Integer.valueOf(dtmd.getDataType()), dtmd);
                    }
                }
            }
        } finally {
            if(typesResult != null) {
                typesResult.close();
            }

        }

        this.registerSyntaxInfo(1, dataTypeToMDMap, new int[]{12});
        this.registerSyntaxInfo(5, dataTypeToMDMap, new int[]{2005, 2004, -1, -16, 12});
        this.registerSyntaxInfo(4, dataTypeToMDMap, new int[]{8, 7, 6});
        this.registerSyntaxInfo(2, dataTypeToMDMap, new int[]{8, 7, 6});
        this.registerSyntaxInfo(3, dataTypeToMDMap, new int[]{4});
        this.registerSyntaxInfo(10, dataTypeToMDMap, new int[]{91, 93});
        this.registerSyntaxInfo(9, dataTypeToMDMap, new int[]{93});
        this.registerSyntaxInfo(11, dataTypeToMDMap, new int[]{92, 93});
        this.registerSyntaxInfo(0, dataTypeToMDMap, new int[]{8, 7, 6});
        this.registerSyntaxInfo(6, dataTypeToMDMap, new int[]{12});
    }

    private boolean isUCanAccessVarchar(DatabaseMetaData dbMetaData, Map<Integer, DataTypeSyntaxInformation> dataTypeToMDMap, DataTypeSyntaxInformation dtmd) throws SQLException {
        return dbMetaData.getDatabaseProductName().toLowerCase(Locale.ENGLISH).contains("ucanaccess") && ((DataTypeSyntaxInformation)dataTypeToMDMap.get(Integer.valueOf(dtmd.getDataType()))).getTypeName().equals("NVARCHAR");
    }

    private void registerSyntaxInfo(int attributeType, Map<Integer, DataTypeSyntaxInformation> dataTypeToMDMap, int... possibleDataTypes) throws SQLException {
        int[] var4 = possibleDataTypes;
        int var5 = possibleDataTypes.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            int i = var4[var6];
            DataTypeSyntaxInformation si = (DataTypeSyntaxInformation)dataTypeToMDMap.get(Integer.valueOf(i));
            if(si != null) {
                this.typeMap.put(Integer.valueOf(attributeType), si);
                LogService.getRoot().log(Level.FINE, "com.rapidminer.tools.jdbc.StatementCreator.mapping_ontology_value_type", new Object[]{Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(attributeType), si});
                return;
            }
        }

        LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.jdbc.StatementCreator.no_sql_value_type_found", Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(attributeType));
    }

    private String mapAttributeTypeToSQLDataType(int type) {
        DataTypeSyntaxInformation typeStr = this.getSQLTypeForRMValueType(type);
        return typeStr.getTypeName();
    }

    public DataTypeSyntaxInformation getSQLTypeForRMValueType(int type) {
        for(int parent = type; parent != 0; parent = Ontology.ATTRIBUTE_VALUE_TYPE.getParent(parent)) {
            DataTypeSyntaxInformation si = (DataTypeSyntaxInformation)this.typeMap.get(Integer.valueOf(parent));
            if(si != null) {
                return si;
            }
        }

        throw new NoSuchElementException("No SQL type mapped to attribute type " + Ontology.ATTRIBUTE_VALUE_TYPE.mapIndex(type));
    }

    public String makeTableCreator(Attributes attributes, TableName tableName, int defaultVarcharLength) throws SQLException {
        this.defaultVarCharLength = (long)defaultVarcharLength;
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE ");
        b.append(this.makeIdentifier(tableName));
        b.append(" (");
        Iterator a = attributes.allAttributeRoles();
        boolean first = true;

        while(a.hasNext()) {
            if(!first) {
                b.append(", ");
            }

            first = false;
            AttributeRole idAttribute = (AttributeRole)a.next();
            this.makeColumnCreator(b, idAttribute);
        }

        Attribute idAttribute1 = attributes.getId();
        if(idAttribute1 != null) {
            b.append(", PRIMARY KEY( ");
            b.append(this.makeColumnIdentifier(idAttribute1));
            b.append(")");
        }

        b.append(")");
        return b.toString();
    }

    public String makeIdentifier(TableName tableName) {
        return tableName.getSchema() != null?this.makeIdentifier(tableName.getSchema()) + "." + this.makeIdentifier(tableName.getTableName()):this.makeIdentifier(tableName.getTableName());
    }

    public String makeIdentifier(String identifier) {
        if(identifier == null) {
            throw new NullPointerException("Identifier must not be null");
        } else {
            if(this.identifierQuote != null) {
                switch(this.identifierQuote.length()) {
                    case 0:
                        break;
                    case 1:
                        identifier = identifier.replace(this.identifierQuote.charAt(0), '_');
                        break;
                    default:
                        identifier = identifier.replace(this.identifierQuote, "_");
                }
            }

            identifier = identifier
                    .replace(' ', '_')
                    .replace('-', '_')
                    .replace('(', '_')
                    .replace(')', '_');
            return this.identifierQuote + identifier + this.identifierQuote;
        }
    }

    public String makeInsertStatement(TableName tableName, ExampleSet exampleSet) throws SQLException {
        return this.makeInsertStatement(tableName, exampleSet, 1);
    }

    public String makeInsertStatement(TableName tableName, ExampleSet exampleSet, int batchSize) throws SQLException {
        StringBuilder b = new StringBuilder("INSERT INTO ");
        b.append(this.makeIdentifier(tableName));
        b.append(" (");
        Iterator a = exampleSet.getAttributes().allAttributes();

        for(boolean first = true; a.hasNext(); first = false) {
            Attribute size = (Attribute)a.next();
            if(!first) {
                b.append(", ");
            }

            b.append(this.makeColumnIdentifier(size));
        }

        b.append(")");
        b.append(" VALUES ");
        int var10 = exampleSet.getAttributes().allSize();

        for(int r = 0; r < batchSize; ++r) {
            if(r != 0) {
                b.append(",");
            }

            b.append("(");

            for(int i = 0; i < var10; ++i) {
                if(i != 0) {
                    b.append(", ");
                }

                b.append("?");
            }

            b.append(")");
        }

        return b.toString();
    }

    public void makeColumnAlter(StringBuilder b, AttributeRole role) throws SQLException {
        b.append("ALTER ");
        this.makeColumnCreator(b, role);
    }

    public void makeColumnAdd(StringBuilder b, AttributeRole role) throws SQLException {
        b.append("ADD ");
        this.makeColumnCreator(b, role);
    }

    public void makeColumnDrop(StringBuilder b, Attribute attribute) throws SQLException {
        b.append("DROP ");
        b.append(this.makeColumnIdentifier(attribute));
        b.append(" CASCADE");
    }

    private void makeColumnCreator(StringBuilder b, AttributeRole role) throws SQLException {
        Attribute attribute = role.getAttribute();
        b.append(this.makeColumnIdentifier(attribute));
        b.append(" ");
        DataTypeSyntaxInformation si = this.getSQLTypeForRMValueType(attribute.getValueType());
        b.append(si.getTypeName());
        if(attribute.isNominal()) {
            if(si.getPrecision() > 0L && this.defaultVarCharLength > si.getPrecision()) {
                throw new SQLException("minimum requested varchar length >" + si.getPrecision() + " which is the maximum length for columns of SQL type " + si.getTypeName());
            }

            int maxLength = 1;
            Iterator var6 = attribute.getMapping().getValues().iterator();

            while(var6.hasNext()) {
                String value = (String)var6.next();
                int length = value.length();
                if(length > maxLength) {
                    maxLength = length;
                    if(si.getPrecision() > 0L && (long)length > si.getPrecision()) {
                        throw new SQLException("Attribute " + attribute.getName() + " contains values with length >" + si.getPrecision() + " which is the maximum length for columns of SQL type " + si.getTypeName());
                    }

                    if(this.defaultVarCharLength != -1L && (long)length > this.defaultVarCharLength) {
                        throw new SQLException("Attribute " + attribute.getName() + " contains values with length >" + this.defaultVarCharLength + " which is the requested default varchar length.");
                    }
                }
            }

            if(si.getTypeName().toLowerCase().startsWith("varchar")) {
                if(this.defaultVarCharLength != -1L && this.defaultVarCharLength < (long)maxLength) {
                    b.append("(").append(maxLength).append(")");
                } else if(this.defaultVarCharLength != -1L && (long)maxLength < this.defaultVarCharLength) {
                    b.append("(").append(this.defaultVarCharLength).append(")");
                } else {
                    b.append("(").append(maxLength).append(")");
                }
            }
        }

        if(role.isSpecial() && role.getSpecialName().equals("id")) {
            b.append(" NOT NULL");
        }

    }

    private void makeColumnCreator(StringBuilder b, Attribute attribute) {
        b.append(this.makeColumnIdentifier(attribute));
        b.append(" ");
        b.append(this.mapAttributeTypeToSQLDataType(attribute.getValueType()));
    }

    public String makeColumnCreator(Attribute attribute) {
        StringBuilder b = new StringBuilder();
        this.makeColumnCreator(b, attribute);
        return b.toString();
    }

    public String makeColumnIdentifier(Attribute attribute) {
        return this.makeIdentifier(attribute.getName());
    }

    public String makeDropStatement(TableName tableName) {
        return "DROP TABLE " + this.makeIdentifier(tableName);
    }

    public String makeDropStatement(String tableName) {
        return this.makeDropStatement(new TableName(tableName));
    }

    public String makeDeleteStatement(TableName tableName) {
        return "DELETE FROM " + this.makeIdentifier(tableName);
    }

    public String makeDeleteStatement(String tableName) {
        return this.makeDeleteStatement(new TableName(tableName));
    }

    public String makeSelectAllStatement(String tableName) {
        return "SELECT * FROM " + this.makeIdentifier(tableName);
    }

    public String makeSelectAllStatement(TableName tableName) {
        return "SELECT * FROM " + this.makeIdentifier(tableName);
    }

    public String makeSelectStatement(String tableName, boolean distinct, String... columns) {
        StringBuilder b = new StringBuilder("SELECT ");
        if(distinct) {
            b.append(" DISTINCT ");
        }

        boolean first = true;
        String[] var6 = columns;
        int var7 = columns.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            String col = var6[var8];
            if(first) {
                first = false;
            } else {
                b.append(", ");
            }

            b.append(this.makeIdentifier(col));
        }

        b.append(" FROM ");
        b.append(this.makeIdentifier(tableName));
        return b.toString();
    }

    public String makeSelectSizeStatement(String tableName) {
        return "SELECT count(*) FROM " + this.makeIdentifier(tableName);
    }

    public String makeSelectSizeStatement(TableName tableName) {
        return "SELECT count(*) FROM " + this.makeIdentifier(tableName);
    }

    /** @deprecated */
    @Deprecated
    public String makeSelectEmptySetStatement(String tableName) {
        return "SELECT * FROM " + this.makeIdentifier(tableName) + " WHERE 1=0";
    }

    public String makeClobCreator(String columnName, int minLength) {
        StringBuilder b = new StringBuilder();
        b.append(this.makeIdentifier(columnName)).append(" ");
        String typeString = this.mapAttributeTypeToSQLDataType(5);
        b.append(typeString);
        if(typeString.toLowerCase().startsWith("varchar")) {
            if(minLength != -1) {
                b.append("(").append(minLength).append(")");
            } else {
                b.append("(").append(this.defaultVarCharLength).append(")");
            }
        }

        return b.toString();
    }

    public String makeVarcharCreator(String columnName, int minLength) {
        if(minLength == 0) {
            minLength = 1;
        }

        StringBuilder b = new StringBuilder();
        b.append(this.makeIdentifier(columnName)).append(" ");
        String typeString = this.mapAttributeTypeToSQLDataType(1);
        b.append(typeString);
        if(typeString.toLowerCase().startsWith("varchar")) {
            if(minLength != -1) {
                b.append("(").append(minLength).append(")");
            } else {
                b.append("(").append(this.defaultVarCharLength).append(")");
            }
        }

        return b.toString();
    }

    public String makeIntegerCreator(String columnName) {
        StringBuilder b = new StringBuilder();
        b.append(this.makeIdentifier(columnName)).append(" ");
        String typeString = this.mapAttributeTypeToSQLDataType(3);
        b.append(typeString);
        return b.toString();
    }
}
