package com.rapidminer.extension.jdbc.operator.io;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.extension.jdbc.tools.jdbc.db.InceptorAttributeStore;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.io.AbstractExampleSetWriter;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.tools.Ontology;
import io.transwarp.midas.client.ClientConfig;
import io.transwarp.midas.utils.DynamicHdfsIO;
import io.transwarp.midas.utils.HdfsIO;
import io.transwarp.midas.utils.HdfsUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HdfsExampleSetWriter extends AbstractExampleSetWriter implements ConnectionProvider {
    public static final String PARAMETER_OVERWRITE_MODE = "overwrite_mode";
    public static final String PARAMETER_USE_Kerberos = "use_kerberos";
    public static final String PARAMETER_KERBEROS_PRINCIPAL = "kerberos_principal";
    public static final String PARAMETER_KERBEROS_KEYTAB = "kerberos_keytab";
    private static final String JTDS_JDBC_CLASSNAME = "net.sourceforge.jtds.jdbc";
    private static final String MSSQL_JDBC_CLASSNAME = "com.microsoft.sqlserver.jdbc";

    public HdfsExampleSetWriter(OperatorDescription description) {
        super(description);
    }


    @Override
    public ConnectionEntry getConnectionEntry() {
        return null;
    }

    @Override
    public ExampleSet write(ExampleSet exampleSet) throws OperatorException {
        try {
            DatabaseHandler handler = DatabaseHandler.getConnectedDatabaseHandler(this);

            try {
                this.getProgress().setTotal(exampleSet.size());
                TableName tableName = DatabaseHandler.getSelectedTableName(this);
                handler.simpleCreataTable(exampleSet,
                        tableName,
                        this.getParameterAsBoolean(PARAMETER_OVERWRITE_MODE),
                        255,
                        this);

                String dbName = handler.getConnection().getSchema();
                String url = (String) HdfsUtils.getHDFSUrl(handler.getConnection(), tableName.getTableName(), dbName);
                url = HdfsUtils.urlToDir(url);

                String delimiter = (String) HdfsUtils.getFieldDelimiter(handler.getConnection(), tableName.getTableName(), dbName);

                final Attributes attrs = exampleSet.getAttributes();
                final Iterator<Example> iter = exampleSet.iterator();
                Iterator<String[]> rows = new Iterator<String[]>() {
                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public String[] next() {
                        Example example = iter.next();

                        String[] cols = new String[attrs.allSize()];

                        int j = 0;

                        Iterator<Attribute> attrIter = attrs.allAttributes();
                        while(attrIter.hasNext()) {
                            Attribute attr = attrIter.next();

                            cols[j] = getValue(example, attr);
                            j++;
                        }
                        return cols;
                    }

                    @Override
                    public void remove() {

                    }
                };

                ConnectionEntry entry = DatabaseHandler.getConnectionEntry(this);
                String jar = entry.getProperties().getDriverJarFile();
                DynamicHdfsIO io = new DynamicHdfsIO(ClientConfig.wrapDriverClassPath(jar) );

                boolean useKerberos = this.getParameterAsBoolean(PARAMETER_USE_Kerberos);
                String filename = "" + System.currentTimeMillis()  + ".txt";
                if (useKerberos) {
                    String principal = this.getParameterAsString(PARAMETER_KERBEROS_PRINCIPAL);
                    String keytab = this.getParameterAsFile(PARAMETER_KERBEROS_KEYTAB).getAbsolutePath();
                    io.login(principal, keytab);
                    io.writeRows(url, rows, delimiter, filename, true);
                } else {
                    io.writeRows(url, rows, delimiter, filename, false);
                }
                this.getProgress().complete();
            } finally {
                if(handler != null) {
                    handler.close();
                }

            }
            return exampleSet;
        } catch (SQLException var18) {
            throw new UserError(this, var18, 304, new Object[]{var18.getMessage()});
        }
    }

    // convert the value to string
    private String getValue(Example example, Attribute attribute) {
		double value = example.getValue(attribute);

		if (Double.isNaN(value)) {
			return null;
		}

		if (attribute == null) {
			return null;
		} else if (attribute.isNominal()) {
			return example.getValueAsString(attribute);
		} else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.INTEGER)) {
			return Integer.toString((int) example.getValue(attribute));
		} else if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.DATE_TIME)) {
			return (new Date((long) example.getValue(attribute))).toString();
		} else {
			return Double.toString(example.getValue(attribute));
		}
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList();
        types.addAll(DatabaseHandler.getConnectionParameterTypes(this));
        types.addAll(DatabaseHandler.getQueryParameterTypes(this, true));
        types.add(new ParameterTypeBoolean(PARAMETER_OVERWRITE_MODE, "Indicates if an existing table should be overwritten or if data should be appended.", false));

        ParameterType kerberos = new ParameterTypeBoolean(PARAMETER_USE_Kerberos, "Indicates if the HDFS cluster uses kerberos", false);
        types.add(kerberos);

        ParameterType type = new ParameterTypeString(PARAMETER_KERBEROS_PRINCIPAL, "kerberos principal", true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_Kerberos, true, true));
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeFile(PARAMETER_KERBEROS_KEYTAB, "kerberos keytab file", "keytab", true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_Kerberos, true, true));
        type.setExpert(true);
        types.add(type);
        return types;
    }
}
