package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.cipher.KeyGeneratorTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.nio.charset.Charset;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class FileDatabaseConnectionProvider implements DatabaseConnectionProvider {
    public FileDatabaseConnectionProvider() {
    }


    public List<FieldConnectionEntry> readConnectionEntries() {
        LinkedList entries = new LinkedList();
        File connectionsFile = this.getOldConnectionsFile();
        File xmlConnectionsFile = this.getXMLConnectionsFile();

        if (!xmlConnectionsFile.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;

            try {
                is = FileDatabaseConnectionProvider
                        .class.getResourceAsStream("/com/rapidminer/extension/resources/connections.xml");
                fos = new FileOutputStream(xmlConnectionsFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int ch = 0;
            try {
                while((ch=is.read()) != -1){
                    fos.write(ch);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally{
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        if(!xmlConnectionsFile.exists() && !connectionsFile.exists()) {
            try {
                xmlConnectionsFile.createNewFile();
                this.writeXMLConnectionsEntries(DatabaseConnectionService.getConnectionEntries(), xmlConnectionsFile);
            } catch (IOException var8) {
                ;
            }
        } else if(!xmlConnectionsFile.exists() && connectionsFile.exists()) {
            entries.addAll(DatabaseConnectionService.readConnectionEntries(connectionsFile));
            this.writeXMLConnectionsEntries(DatabaseConnectionService.getConnectionEntries(), xmlConnectionsFile);
            connectionsFile.delete();
        } else {
            FileReader reader = null;

            try {
                reader = new FileReader(xmlConnectionsFile);
                if(!"".equals(Tools.readTextFile(reader))) {
                    Document e = XMLTools.parse(xmlConnectionsFile);
                    Element jdbcElement = e.getDocumentElement();
                    entries.addAll(DatabaseConnectionService.parseEntries(jdbcElement));
                }
            } catch (Exception var7) {
                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.connection.DatabaseConnectionService.reading_database_error", new Object[]{var7}), var7);
            }
        }

        return entries;
    }

    public void writeConnectionEntries(Collection<FieldConnectionEntry> connectionEntries) {
        File connectionEntriesFile = this.getXMLConnectionsFile();
        this.writeXMLConnectionsEntries(connectionEntries, connectionEntriesFile);
    }

    public void writeXMLConnectionsEntries(Collection<FieldConnectionEntry> connectionEntries, File connectionEntriesFile) {
        Key key;
        try {
            key = KeyGeneratorTool.getUserKey();
        } catch (IOException var6) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.connection.DatabaseConnectionService.retrieving_key_error", new Object[]{var6}), var6);
            return;
        }

        try {
            XMLTools.stream(DatabaseConnectionService.toXML(connectionEntries, key, (String)null, false), connectionEntriesFile, Charset.forName("UTF-8"));
        } catch (Exception var5) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.connection.DatabaseConnectionService.writing_database_connection_error", new Object[]{var5}), var5);
        }

    }

    private File getOldConnectionsFile() {
        return FileSystemService.getUserConfigFile("connections");
    }

    private File getXMLConnectionsFile() {
        return FileSystemService.getUserConfigFile("connections.xml");
    }
}
