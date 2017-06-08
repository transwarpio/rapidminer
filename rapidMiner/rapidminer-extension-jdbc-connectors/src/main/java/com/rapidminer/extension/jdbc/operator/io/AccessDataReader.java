package com.rapidminer.extension.jdbc.operator.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseTable;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeSQLQuery;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.AccessConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.license.annotation.LicenseConstraint;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.nio.file.FileInputPortHandler;
import com.rapidminer.operator.nio.file.FileObject;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.MetaDataChangeListener;
import com.rapidminer.operator.ports.Port;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.operator.ports.metadata.SimplePrecondition;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import com.rapidminer.tools.cipher.CipherException;
import com.rapidminer.tools.cipher.CipherTools;

import java.io.File;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

@LicenseConstraint(
        productId = "rapidminer-[^\\-]+",
        constraintId = "connectors",
        value = "ADVANCED_CONNECTORS"
)
public class AccessDataReader extends DatabaseDataReader {
    public static final String PARAMETER_DATABASE_FILE = "database_file";
    public static final String PARAMETER_USERNAME = "username";
    public static final String PARAMETER_PASSWORD = "password";
    public static final String ACCESS_URL_PREFIX = "jdbc:ucanaccess://";
    public static final String ACCESS_URL_SUFFIX = ";jackcessOpener=com.rapidminer.jdbc.AccessCryptCodecOpener";
    private static final String DEFAULT_USER_NAME = "noUser";
    private static final String DEFAULT_PASSWORD;
    private boolean wasInputPortConnected = false;
    private final InputPort fileInputPort = (InputPort)this.getInputPorts().createPort("file");
    private final FileInputPortHandler filePortHandler;

    public AccessDataReader(OperatorDescription description) throws OperatorCreationException {
        super(description);
        this.filePortHandler = new FileInputPortHandler(this, this.fileInputPort, "database_file");
        this.fileInputPort.addPrecondition(new SimplePrecondition(this.fileInputPort, new MetaData(FileObject.class)) {
            protected boolean isMandatory() {
                return false;
            }
        });
        this.fileInputPort.registerMetaDataChangeListener(new MetaDataChangeListener() {
            public void informMetaDataChanged(MetaData newMetadata) {
                if(AccessDataReader.this.wasInputPortConnected != AccessDataReader.this.fileInputPort.isConnected()) {
                    AccessDataReader.this.wasInputPortConnected = AccessDataReader.this.fileInputPort.isConnected();
                    AccessDataReader.this.setParameter("database_url", "");
                }

            }
        });
        this.setParameter("define_connection", DatabaseHandler.CONNECTION_MODES[1]);
        this.setParameter("database_system", "UCanAccess");
        this.setParameter("username", "noUser");
        this.setParameter("password", DEFAULT_PASSWORD);
    }

    protected ResultSet getResultSet() throws OperatorException {
        this.setAccessParameters();
        return super.getResultSet();
    }

    public MetaData getGeneratedMetaData() throws OperatorException {
        if(!this.fileInputPort.isConnected()) {
            this.setAccessParameters();
            return super.getGeneratedMetaData();
        } else {
            return new ExampleSetMetaData();
        }
    }

    protected void setAccessParameters() throws OperatorException {
        String fileName = this.filePortHandler.getSelectedFile().getAbsolutePath();
        this.setParameter("database_url", "jdbc:ucanaccess://" + fileName + ";jackcessOpener=com.rapidminer.jdbc.AccessCryptCodecOpener");
        String userName = this.getParameterAsString("username");
        if(userName == null) {
            userName = "noUser";
        }

        String password = this.getParameterAsString("password");
        if(password == null) {
            password = DEFAULT_PASSWORD;
        }

        this.setParameter("username", userName);
        this.setParameter("password", password);
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        Iterator type = types.iterator();

        while(type.hasNext()) {
            ParameterType t = (ParameterType)type.next();
            t.setHidden(true);
        }

        types.add(FileInputPortHandler.makeFileParameterType(this, "database_file", "The file containing the Access database which should be read from.", new PortProvider() {
            public Port getPort() {
                return AccessDataReader.this.fileInputPort;
            }
        }, true, new String[]{"mdb", "accdb"}));
        types.add(new ParameterTypeString("username", "The username for the Access database.", true, false));
        ParameterTypePassword type1 = new ParameterTypePassword("password", "The password for the Access database.");
        type1.setExpert(false);
        types.add(type1);
        ParameterTypeCategory type2 = new ParameterTypeCategory("define_query", "Specifies whether the database query should be defined directly, through a file or implicitely by a given table name.", DatabaseHandler.QUERY_MODES, 2);
        type2.setExpert(false);
        types.add(type2);
        ParameterTypeSQLQuery type3 = new ParameterTypeSQLQuery("query", "An SQL query.");
        type3.registerDependencyCondition(new EqualTypeCondition(this, "define_query", DatabaseHandler.QUERY_MODES, true, new int[]{0}));
        type3.setExpert(false);
        types.add(type3);
        ParameterTypeFile type4 = new ParameterTypeFile("query_file", "A file containing an SQL query.", (String)null, true);
        type4.registerDependencyCondition(new EqualTypeCondition(this, "define_query", DatabaseHandler.QUERY_MODES, true, new int[]{1}));
        type4.setExpert(false);
        types.add(type4);
        ParameterTypeDatabaseTable type5 = new ParameterTypeDatabaseTable("table_name", "The name of a single table within the Access database which should be read.");
        type5.registerDependencyCondition(new EqualTypeCondition(this, "define_query", DatabaseHandler.QUERY_MODES, true, new int[]{2}));
        type5.setExpert(false);
        types.add(type5);
        return types;
    }

    public ConnectionEntry getConnectionEntry() {
        try {
            File file = this.getParameterAsFile("database_file");
            if(file != null) {
                return new AccessConnectionEntry(file);
            }
        } catch (UserError var2) {
            ;
        }

        return null;
    }

    static {
        String pw = "noPassword";
        if(CipherTools.isKeyAvailable()) {
            try {
                pw = CipherTools.encrypt("noPassword");
            } catch (CipherException var2) {
                pw = "noPassword";
            }
        }

        DEFAULT_PASSWORD = pw;
    }
}
