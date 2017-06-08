package com.rapidminer.extension.jdbc.operator.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.ExampleSet;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.license.annotation.LicenseConstraint;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.io.AbstractWriter;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypePassword;
import com.rapidminer.parameter.ParameterTypeString;
import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@LicenseConstraint(
        productId = "rapidminer-[^\\-]+",
        constraintId = "connectors",
        value = "ADVANCED_CONNECTORS"
)
public class AccessDataWriter extends AbstractWriter<ExampleSet> {
    public static final String PARAMETER_DATABASE_FILE = "database_file";
    public static final String PARAMETER_USERNAME = "username";
    public static final String PARAMETER_PASSWORD = "password";
    public static final String PARAMETER_TABLE_NAME = "table_name";
    public static final String PARAMETER_OVERWRITE_MODE = "overwrite_mode";
    public static final String PARAMETER_ACCESS_VERSION = "access_version";
    private static final String[] VERSION_VALUES = new String[]{"V2000", "V2003", "V2007", "V2010"};
    private static final String NEW_DATABASE_KEY = ";newdatabaseversion=";

    public AccessDataWriter(OperatorDescription description) {
        super(description, ExampleSet.class);
    }

    public ExampleSet write(ExampleSet exampleSet) throws OperatorException {
        File databaseFile = this.getParameterAsFile("database_file");
        String databaseURL = "jdbc:ucanaccess://" + databaseFile.getAbsolutePath() + ";jackcessOpener=com.rapidminer.jdbc.AccessCryptCodecOpener";
        if(!databaseFile.exists()) {
            int username = this.getParameterAsInt("access_version");
            databaseURL = databaseURL + ";newdatabaseversion=" + VERSION_VALUES[username];
        }

        String username1 = this.getParameterAsString("username");
        if(username1 == null) {
            username1 = "noUser";
        }

        String password = this.getParameterAsString("password");
        if(password == null) {
            password = "noPassword";
        }

        DatabaseHandler handler = null;

        try {
            handler = DatabaseHandler.getConnectedDatabaseHandler(databaseURL, username1, password);
            handler.createTable(exampleSet, this.getParameterAsString("table_name"), this.getParameterAsInt("overwrite_mode"), this.getApplyCount() == 0, -1);
            handler.disconnect();
            return exampleSet;
        } catch (SQLException var8) {
            throw new UserError(this, var8, 304, new Object[]{var8.getMessage()});
        }
    }

    public List<ParameterType> getParameterTypes() {
        LinkedList types = new LinkedList();
        ParameterTypeFile fileType = new ParameterTypeFile("database_file", "The mdb or accdb file containing the Access database which should be written to.", false, new String[]{"mdb", "accdb"});
        fileType.setExpert(false);
        fileType.setAddAllFileExtensionsFilter(true);
        types.add(fileType);
        types.add(new ParameterTypeString("username", "The username for the Access database.", true, false));
        ParameterTypePassword type = new ParameterTypePassword("password", "The password for the database.");
        type.setExpert(false);
        types.add(type);
        types.add(new ParameterTypeString("table_name", "The name of the table within the Access database to which the data set should be written.", false, false));
        types.add(new ParameterTypeCategory("overwrite_mode", "Indicates if an existing table should be overwritten or if data should be appended.", DatabaseHandler.OVERWRITE_MODES, 0));
        types.add(new ParameterTypeCategory("access_version", "If a new database is created this specifies its format version.", new String[]{"Access 2000", "Access 2003", "Access 2007", "Access 2010"}, 3, true));
        return types;
    }
}
