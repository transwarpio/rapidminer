package com.rapidminer.extension.jdbc.parameter;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.parameter.ParameterTypeSingle;
import com.rapidminer.tools.XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ParameterTypeDatabaseConnection extends ParameterTypeSingle {
    private static final long serialVersionUID = 5747692587025691591L;

    public ParameterTypeDatabaseConnection(Element element) throws XMLException {
        super(element);
    }

    public ParameterTypeDatabaseConnection(String key, String description, boolean expert) {
        this(key, description);
        this.setExpert(expert);
    }

    public ParameterTypeDatabaseConnection(String key, String description) {
        super(key, description);
    }

    public boolean isNumerical() {
        return false;
    }

    public Object getDefaultValue() {
        return null;
    }

    public String getRange() {
        return null;
    }

    public void setDefaultValue(Object defaultValue) {
    }

    protected void writeDefinitionToXML(Element typeElement) {
    }

    @Override
    public Element getXMLForServer(String key, String value, boolean hideDefault, Document doc) {
        Element element = doc.createElement("list");
        element.setAttribute("key", key);
        if (value != null) {
            ConnectionEntry ce = DatabaseConnectionService.getConnectionEntry(value);
            if (ce != null) {
                Element elementUrl = doc.createElement("parameter");
                elementUrl.setAttribute("key", "url");
                elementUrl.setAttribute("value", ce.getURL());

                Element elementUserName = doc.createElement("parameter");
                elementUserName.setAttribute("key", "username");
                elementUserName.setAttribute("value", ce.getUser());
                try {

                    Element elementPassword = doc.createElement("parameter");
                    elementPassword.setAttribute("key", "password");
                    elementPassword.setAttribute("value", new String(ce.getPassword()));

                    element.appendChild(elementUrl);
                    element.appendChild(elementUserName);
                    element.appendChild(elementPassword);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalArgumentException(
                        String.format("could not find connection name %s in DatabaseConnectionService", value));
            }
        } else {
            return null;
        }
        return element;
    }

    @Override
    public List<String[]> getValueAs2DList(String value) {
        ConnectionEntry ce = DatabaseConnectionService.getConnectionEntry(value);
        List<String[]> list = new ArrayList<>();
        if (ce != null) {
            list.add(new String[]{"url", ce.getURL()});
            list.add(new String[]{"username", ce.getUser()});
            list.add(new String[]{"password", new String(ce.getPassword())});
        } else {
            throw new IllegalArgumentException(
                    String.format("could not find connection name %s in DatabaseConnectionService", value));
        }
        return list;
    }
}
