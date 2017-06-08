package com.rapidminer.extension.jdbc.parameter;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.parameter.ParameterTypeSingle;
import com.rapidminer.tools.XMLException;
import org.w3c.dom.Element;

public class ParameterTypeDatabaseSchema extends ParameterTypeSingle {
    private static final long serialVersionUID = 5747692587025691591L;

    public ParameterTypeDatabaseSchema(Element element) throws XMLException {
        super(element);
    }

    public ParameterTypeDatabaseSchema(String key, String description, boolean expert) {
        this(key, description);
        this.setExpert(expert);
    }

    public ParameterTypeDatabaseSchema(String key, String description) {
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
}
