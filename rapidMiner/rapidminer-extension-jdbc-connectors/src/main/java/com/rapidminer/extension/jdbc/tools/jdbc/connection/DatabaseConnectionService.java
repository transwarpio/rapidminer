package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;
import com.rapidminer.io.Base64;
import com.rapidminer.repository.RepositoryAccessor;
import com.rapidminer.tools.XMLException;
import com.rapidminer.tools.cipher.CipherException;
import com.rapidminer.tools.cipher.CipherTools;
import com.rapidminer.tools.cipher.KeyGeneratorTool;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.sql.SQLException;
import java.util.*;

public class DatabaseConnectionService {
    public static final String PROPERTY_CONNECTIONS_FILE = "connections";
    public static final String PROPERTY_CONNECTIONS_FILE_XML = "connections.xml";
    private static List<FieldConnectionEntry> connections = Collections.synchronizedList(new ArrayList());
    private static DatabaseHandler handler = null;
    private static DatabaseAccessValidator accessValidator = new TrueAccessValidator();
    private static DatabaseConnectionProvider connectionProvider = new FileDatabaseConnectionProvider();

    public DatabaseConnectionService() {
    }

    public static void init() {
        List var0 = connections;
        synchronized(connections) {
            connections.clear();
            connections.addAll(connectionProvider.readConnectionEntries());
            Collections.sort(connections, ConnectionEntry.COMPARATOR);
        }
    }

    public static void setAccessValidator(DatabaseAccessValidator validator) {
        accessValidator = validator;
    }

    public static void setConnectionProvider(DatabaseConnectionProvider provider) {
        connectionProvider = provider;
    }

    public static Collection<FieldConnectionEntry> getConnectionEntries() {
        List var1 = connections;
        synchronized(connections) {
            List list = Collections.unmodifiableList(connections);
            return list;
        }
    }

    public static ConnectionEntry getConnectionEntry(String name) {
        return getConnectionEntry(name, (String)null, (RepositoryAccessor)null);
    }

    public static ConnectionEntry getConnectionEntry(String name, String repository) {
        return getConnectionEntry(name, repository, (RepositoryAccessor)null);
    }

    public static ConnectionEntry getConnectionEntry(String name, String repository, RepositoryAccessor accessor) {
        if(!accessValidator.canAccessDatabaseConnection(name, accessor)) {
            return null;
        } else {
            List var3 = connections;
            Iterator var4;
            ConnectionEntry entry;
            synchronized(connections) {
                var4 = connections.iterator();

                while(true) {
                    if(!var4.hasNext()) {
                        break;
                    }

                    entry = (ConnectionEntry)var4.next();
                    if(entry.getName().equals(name) && entry.getRepository() == null) {
                        return entry;
                    }
                }
            }

            var3 = connections;
            synchronized(connections) {
                var4 = connections.iterator();

                while(true) {
                    if(!var4.hasNext()) {
                        break;
                    }

                    entry = (ConnectionEntry)var4.next();
                    if(entry.getName().equals(name)) {
                        String entryRepository = entry.getRepository();
                        if(entryRepository == null && repository == null) {
                            return entry;
                        }

                        if(entryRepository != null && entryRepository.equals(repository)) {
                            return entry;
                        }
                    }
                }
            }

            var3 = connections;
            synchronized(connections) {
                var4 = connections.iterator();

                do {
                    if(!var4.hasNext()) {
                        return null;
                    }

                    entry = (ConnectionEntry)var4.next();
                } while(!entry.getName().equals(name));

                return entry;
            }
        }
    }

    public static void addConnectionEntry(FieldConnectionEntry entry) {
        addConnectionEntry(entry, false);
    }

    public static void addConnectionEntry(FieldConnectionEntry entry, boolean suppressXmlWrite) {
        connections.add(entry);
        List var2 = connections;
        synchronized(connections) {
            Collections.sort(connections, ConnectionEntry.COMPARATOR);
        }

        if(!suppressXmlWrite) {
            writeConnectionEntries(connections);
        }

    }

    public static void deleteConnectionEntry(ConnectionEntry entry) {
        deleteConnectionEntry(entry, false);
    }

    public static void deleteConnectionEntry(ConnectionEntry entry, boolean suppressXmlWrite) {
        connections.remove(entry);
        if(entry != null && !suppressXmlWrite) {
            writeConnectionEntries(connections);
        }

    }

    public static void setConnectionEntries(List<FieldConnectionEntry> entries) {
        if(entries == null) {
            connections = Collections.synchronizedList(new ArrayList());
        } else {
            List var1 = connections;
            synchronized(connections) {
                connections.clear();
                connections.addAll(entries);
                Collections.sort(connections, ConnectionEntry.COMPARATOR);
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public static List<FieldConnectionEntry> readConnectionEntries(File connectionEntriesFile) {
        LinkedList connectionEntries = new LinkedList();
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(connectionEntriesFile));
            String e = in.readLine();
            if(e != null) {
                int numberOfEntries = Integer.parseInt(e);

                for(int i = 0; i < numberOfEntries; ++i) {
                    String name = in.readLine();
                    String system = in.readLine();
                    String host = in.readLine();
                    String port = in.readLine();
                    String database = in.readLine();
                    String property = in.readLine();
                    String user = in.readLine();
                    String password = CipherTools.decrypt(in.readLine());
                    if(name != null && system != null) {
                        connectionEntries.add(new FieldConnectionEntry(name, DatabaseService.getJDBCProperties(system), host, port, database, property, user, password.toCharArray()));
                    }
                }
            }

            in.close();
            Collections.sort(connectionEntries, ConnectionEntry.COMPARATOR);
        } catch (Exception var21) {
            connectionEntries.clear();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException var20) {
                    ;
                }
            }

        }

        return connectionEntries;
    }

    public static void writeConnectionEntries(Collection<FieldConnectionEntry> connectionEntries) {
        connectionProvider.writeConnectionEntries(connectionEntries);
    }

    public static void writeXMLConnectionsEntries(Collection<FieldConnectionEntry> connectionEntries, File connectionEntriesFile) {
        connectionProvider.writeXMLConnectionsEntries(connectionEntries, connectionEntriesFile);
    }

    public static Document toXML(Collection<FieldConnectionEntry> connectionEntries, Key key, String replacementForLocalhost) throws ParserConfigurationException, DOMException, CipherException {
        return toXML(connectionEntries, key, replacementForLocalhost, true);
    }

    public static Document toXML(Collection<FieldConnectionEntry> connectionEntries, Key key, String replacementForLocalhost, boolean includeDynamic) throws ParserConfigurationException, DOMException, CipherException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("jdbc-entries");
        String base64key = Base64.encodeBytes(key.getEncoded());
        root.setAttribute("key", base64key);
        doc.appendChild(root);
        Iterator var7 = connectionEntries.iterator();

        while(true) {
            FieldConnectionEntry entry;
            do {
                if(!var7.hasNext()) {
                    return doc;
                }

                entry = (FieldConnectionEntry)var7.next();
            } while(!includeDynamic && entry.getRepository() != null);

            root.appendChild(entry.toXML(doc, key, replacementForLocalhost));
        }
    }

    public static Collection<FieldConnectionEntry> parseEntries(Element entries) throws XMLException, CipherException, IOException {
        if(!entries.getTagName().equals("jdbc-entries")) {
            throw new XMLException("Outer tag must be <jdbc-entries>");
        } else {
            String base64Key = entries.getAttribute("key");
            if(base64Key == null) {
                throw new XMLException("Cipher key attribute missing.");
            } else {
                SecretKeySpec key = KeyGeneratorTool.makeKey(Base64.decode(base64Key));
                LinkedList result = new LinkedList();
                NodeList children = entries.getElementsByTagName("field-entry");

                for(int i = 0; i < children.getLength(); ++i) {
                    result.add(new FieldConnectionEntry((Element)children.item(i), key));
                }

                return result;
            }
        }
    }

    public static boolean testConnection(ConnectionEntry entry) throws SQLException {
        if(entry != null) {
            if(handler != null) {
                handler.disconnect();
            }

            handler = DatabaseHandler.getConnectedDatabaseHandler(entry);
            if(handler != null) {
                handler.disconnect();
            }

            return true;
        } else {
            return false;
        }
    }
}
