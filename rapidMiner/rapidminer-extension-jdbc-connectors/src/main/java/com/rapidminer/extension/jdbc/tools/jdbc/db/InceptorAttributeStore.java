package com.rapidminer.extension.jdbc.tools.jdbc.db;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.extension.jdbc.operator.io.DatabaseDataReader;
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.tools.Ontology;
import io.transwarp.midas.utils.TablePropertiesUtils;

import java.util.*;
import java.sql.*;

/**
 * Created by liusheng on 5/12/16.
 */
public class InceptorAttributeStore extends AttributeStore{
    private String sqlTemplate = "ALTER TABLE %s SET TBLPROPERTIES ('%s'='%s');";
    private String type = "type";
    private String role = "role";
    private String range = "range";
    private String regular = "regular";
    private String fieldSep = "#";
    private String fieldPropSep = ":";
    private String emptyRange = "[]";
    private String typePatten = "'type'=";
    private String rolePatten = "'role'=";
    private String rangePatten = "'range'=";

    public static MetaData genMeta(Map<String, Map<String, String>> map) {
        ExampleSetMetaData metaData = new ExampleSetMetaData();

        Map<String, String> typeMap = map.get(TablePropertiesUtils.PropertyRole());
        Map<String, String> roleMap = map.get(TablePropertiesUtils.PropertyRole());
        List<String> fields = new ArrayList<>();
        fields.addAll(typeMap.keySet());


        for (String field: fields) {
            metaData.addAttribute(new AttributeMetaData(
                    field,
                    find(Ontology.VALUE_TYPE_NAMES,
                            AttributeFactory.reverseMapAttributeName(typeMap.get(field))),
                    roleMap.get(field)));
        }
        return metaData;
    }

    public static MetaData genDefaultMeta(List<ColumnIdentifier> columns) {
        ExampleSetMetaData metaData = new ExampleSetMetaData();
        for (ColumnIdentifier column : columns) {
            AttributeMetaData meta =
                    new AttributeMetaData(column.getColumnName(), DatabaseHandler.getRapidMinerTypeIndex(column.getSqlType()));
            metaData.addAttribute(meta);
        }
        return metaData;
    }

    @Override
    public void save(IOObject object, ConnectionEntry connection, String table) throws RepositoryException {
        if (object instanceof ExampleSet) {
            ExampleSet exampleSet = (ExampleSet)object;
            Attributes attributes = exampleSet.getAttributes();
            Statement statement = null;
            Connection conn = null;
            try {
                conn = getConnection(connection);
                statement = conn.createStatement();
                Map<String, String> result = genInfoString(attributes);
                statement.execute(String.format(sqlTemplate, table, type, result.get(this.type)));
                statement.execute(String.format(sqlTemplate, table, role, result.get(this.role)));
                statement.execute(String.format(sqlTemplate, table, range, result.get(this.range)));
            } catch (Exception e) {
                throw new RepositoryException(e);
            } finally {
                try {
                    statement.close();
                    conn.close();
                } catch (Exception e) {
                    throw new RepositoryException(e);
                }

            }
        } else {
            throw new RepositoryException("can't save object for class " + object.getClass().getCanonicalName());
        }
    }

    @Override
    public MetaData convertMetaData(Connection conn, TableName tableName, List<ColumnIdentifier> columns) {
        try {
            ExampleSetMetaData metaData = new ExampleSetMetaData();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("show create table %s", tableName.toString()));
            Map<String, String> result = new HashMap<>();
            while (resultSet.next()) {
                String line = resultSet.getString(1);
                if (line.trim().startsWith(typePatten)) {
                    result.put(type, line.trim().split("='")[1].split("'")[0]);
                }
                if (line.trim().startsWith(rolePatten)) {
                    result.put(role, line.trim().split("='")[1].split("'")[0]);
                }
                if (line.trim().startsWith(rangePatten)) {
                    result.put(range, line.trim().split("='")[1].split("'")[0]);
                }
            }
            resultSet.close();
            statement.close();
            genMetaData(metaData, result);
            return metaData;
        } catch (Exception e){
            return genDefaultMeta(columns);
        }
    }

    @Override
    public ExampleSet getExampleSet(Connection conn, String table,
                                    ResultSet resultSet_, DatabaseDataReader reader) throws SQLException, OperatorException {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("show create table %s", table));
            Map<String, String> result = new HashMap<>();
            while (resultSet.next()) {
                String line = resultSet.getString(1);
                if (line.trim().startsWith(typePatten)) {
                    result.put(type, line.trim().split("='")[1].split("'")[0]);
                }
                if (line.trim().startsWith(rolePatten)) {
                    result.put(role, line.trim().split("='")[1].split("'")[0]);
                }
                if (line.trim().startsWith(rangePatten)) {
                    result.put(range, line.trim().split("='")[1].split("'")[0]);
                }
            }
            resultSet.close();
            statement.close();
            ExampleSet examples = DatabaseDataReader.createExampleTable(resultSet_,
                    genAttributeList(result),
                    reader.getParameterAsInt("datamanagement"),
                    reader.getLogger(),
                    reader.getProgress()).createExampleSet();
            genRole(examples, result);
            return examples;
        } catch (Exception e){
            return DatabaseDataReader.createExampleTable(resultSet_,
                    DatabaseDataReader.getAttributes(resultSet_.getMetaData()),
                    reader.getParameterAsInt("datamanagement"),
                    reader.getLogger(),
                    reader.getProgress()).createExampleSet();
        }
    }

    private void genRole(ExampleSet examples, Map<String, String> result) {
        Map<String, String> roleMap = new HashMap<>();
        List<Attribute> attrMap = new ArrayList<>();
        String[] roles = result.get(role).split(fieldSep);
        for (String r: roles) {
            String[] fieldType = r.split(fieldPropSep);
            roleMap.put(fieldType[0], fieldType[1]);
        }

        Iterator<Attribute> iterator = examples.getAttributes().iterator();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            attrMap.add(attribute);
        }
        examples.getAttributes().clearRegular();
        examples.getAttributes().clearSpecial();
        for (Attribute attr : attrMap) {
            String role = roleMap.get(attr.getName());
            if (!role.equals(regular)) {
                examples.getAttributes().setSpecialAttribute(attr, role);
            } else {
                examples.getAttributes().addRegular(attr);
            }
        }
    }

    private Map<String, String> genInfoString(Attributes attributes) {
        Map<String, String> result = new HashMap<>();
        List<AttributeRole> sorted = new ArrayList<>();
        StringBuffer type = new StringBuffer();
        StringBuffer role = new StringBuffer();
        StringBuffer range = new StringBuffer();
        Iterator<AttributeRole> iterator = attributes.allAttributeRoles();
        while (iterator.hasNext()) {
            sorted.add(iterator.next());
        }
        Collections.sort(sorted, new Comparator<AttributeRole>() {
            @Override
            public int compare(AttributeRole o1, AttributeRole o2) {
                return o1.getAttribute().getTableIndex() - o2.getAttribute().getTableIndex();
            }
        });
        int index = 0;
        for (AttributeRole attribute: sorted) {
            if(index != 0) {
                type.append(fieldSep);
                role.append(fieldSep);
                range.append(fieldSep);
            }

            type.append(attribute.getAttribute().getName());
            type.append(fieldPropSep);
            type.append(AttributeFactory
                    .mapAttributeName(Ontology.VALUE_TYPE_NAMES[attribute.getAttribute().getValueType()]));

            role.append(attribute.getAttribute().getName());
            role.append(fieldPropSep);
            String r = attribute.getSpecialName();
            if (r == null) {
                r = regular;
            }
            role.append(r);

            range.append(attribute.getAttribute().getName());
            range.append(fieldPropSep);
            range.append(emptyRange);

            index++;
        }
        result.put(this.type, type.toString());
        result.put(this.role, role.toString());
        result.put(this.range, range.toString());
        return result;
    }

    private Connection getConnection(ConnectionEntry connection) throws Exception {
        String jdbcUrl = connection.getURL();
        String username = connection.getUser();
        String password = new String(connection.getPassword());

        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        return conn;
    }

    private List<Attribute> genAttributeList(Map<String, String> result){
        List<String> fields = new LinkedList<>();
        List<Attribute> attributes = new LinkedList<>();
        Map<String, String> typeMap = new HashMap<>();
        String[] types = result.get(type).split(fieldSep);
        for (String t: types) {
            String[] fieldType = t.split(fieldPropSep);
            typeMap.put(fieldType[0], fieldType[1]);
            fields.add(fieldType[0]);
            Attribute attribute = AttributeFactory.createAttribute(
                    fieldType[0],
                    find(Ontology.VALUE_TYPE_NAMES,
                            AttributeFactory.reverseMapAttributeName(fieldType[1])));
            attributes.add(attribute);
        }
        return attributes;
    }

    private void genMetaData(ExampleSetMetaData metaData, Map<String, String> result){
        List<String> fields = new LinkedList<>();

        Map<String, String> typeMap = new HashMap<>();
        String[] types = result.get(type).split(fieldSep);
        for (String t: types) {
            String[] fieldType = t.split(fieldPropSep);
            typeMap.put(fieldType[0], fieldType[1]);
            fields.add(fieldType[0]);
        }

        Map<String, String> roleMap = new HashMap<>();
        String[] roles = result.get(role).split(fieldSep);
        for (String r: roles) {
            String[] fieldRole = r.split(fieldPropSep);
            roleMap.put(fieldRole[0], fieldRole[1]);
        }

        for (String field: fields) {
            metaData.addAttribute(new AttributeMetaData(
                    field,
                    find(Ontology.VALUE_TYPE_NAMES,
                            AttributeFactory.reverseMapAttributeName(typeMap.get(field))),
                    roleMap.get(field)));
        }
    }

    private static int find(String[] arr, String targetValue) {
        for (int i = 0; i< arr.length; i++) {
            if (arr[i].equals(targetValue)) {
                return i;
            }
        }
        return -1;
    }

}
