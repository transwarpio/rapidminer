package io.transwarp.midas.ui.property;

import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.MetaDataChangeListener;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.operator.ports.metadata.ModelMetaData;
import com.rapidminer.parameter.MetaDataProvider;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.XMLException;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.Vector;

public class ParameterTypeSqlExpr extends ParameterTypeString {
    private MetaDataProvider metaDataProvider;

    public ParameterTypeSqlExpr(Element element) throws XMLException {
        super(element);
    }

    public ParameterTypeSqlExpr(String key, String description, final InputPort inPort, boolean optional) {
        super(key, description);
        setOptional(optional);
        setExpert(false);

        this.metaDataProvider = new MetaDataProvider() {

            @Override
            public MetaData getMetaData() {
                if (inPort != null) {
                    return inPort.getMetaData();
                } else {
                    return null;
                }
            }

            @Override
            public void addMetaDataChangeListener(MetaDataChangeListener l) {
                inPort.registerMetaDataChangeListener(l);
            }

            @Override
            public void removeMetaDataChangeListener(MetaDataChangeListener l) {
                inPort.removeMetaDataChangeListener(l);

            }
        };
    }

    public Vector<String> getAttributeNames() {
        Vector<String> names = new Vector<>();

        MetaData metaData = getMetaData();
        if (metaData != null) {
            if (metaData instanceof ExampleSetMetaData) {
                ExampleSetMetaData emd = (ExampleSetMetaData) metaData;
                for (AttributeMetaData amd : emd.getAllAttributes()) {
                    names.add(amd.getName());
                }
            }
        }

        Collections.sort(names);
        return names;
    }

    public MetaData getMetaData() {
        MetaData metaData = null;
        if (metaDataProvider != null) {
            metaData = metaDataProvider.getMetaData();
        }
        return metaData;
    }
}
