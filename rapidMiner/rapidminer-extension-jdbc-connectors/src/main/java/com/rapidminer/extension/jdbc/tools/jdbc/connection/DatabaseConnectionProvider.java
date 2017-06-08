package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import java.io.File;
import java.util.Collection;
import java.util.List;

public interface DatabaseConnectionProvider {
    List<FieldConnectionEntry> readConnectionEntries();

    void writeConnectionEntries(Collection<FieldConnectionEntry> var1);

    void writeXMLConnectionsEntries(Collection<FieldConnectionEntry> var1, File var2);
}
