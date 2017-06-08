package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;

import java.io.File;

public class AccessConnectionEntry extends ConnectionEntry {
    private File file;

    public AccessConnectionEntry() {
        this((File)null);
    }

    public AccessConnectionEntry(File file) {
        super("", DatabaseService.getJDBCProperties("UCanAccess"));
        this.file = null;
        this.user = "noUser";
        this.password = "noPassword".toCharArray();
        this.file = file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getURL() {
        return "jdbc:ucanaccess://" + this.file.getAbsolutePath();
    }
}
