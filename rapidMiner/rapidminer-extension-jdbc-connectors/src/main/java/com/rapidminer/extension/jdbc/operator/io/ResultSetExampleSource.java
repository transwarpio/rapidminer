package com.rapidminer.extension.jdbc.operator.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.example.table.ResultSetDataRowReader;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.error.AttributeNotFoundError;
import com.rapidminer.operator.io.AbstractExampleSource;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeString;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class ResultSetExampleSource extends AbstractExampleSource {
    public static final String PARAMETER_LABEL_ATTRIBUTE = "label_attribute";
    public static final String PARAMETER_ID_ATTRIBUTE = "id_attribute";
    public static final String PARAMETER_WEIGHT_ATTRIBUTE = "weight_attribute";
    public static final String PARAMETER_DATAMANAGEMENT = "datamanagement";

    public ResultSetExampleSource(OperatorDescription description) {
        super(description);
    }

    public abstract ResultSet getResultSet() throws OperatorException;

    public abstract void tearDown();

    public abstract void setNominalValues(List<Attribute> var1, ResultSet var2, Attribute var3) throws OperatorException;

    public ExampleSet createExampleSet() throws OperatorException {
        int dataRowType = this.getParameterAsInt("datamanagement");
        ResultSet resultSet = this.getResultSet();
        List attributeList = null;

        try {
            attributeList = DatabaseHandler.createAttributes(resultSet);
        } catch (SQLException var6) {
            throw new UserError(this, var6, 304, new Object[]{var6.getMessage()});
        }

        this.setNominalValues(attributeList, resultSet, find(attributeList, this.getParameterAsString("label_attribute")));
        ResultSetDataRowReader reader = new ResultSetDataRowReader(new DataRowFactory(dataRowType, '.'), attributeList, resultSet);
        MemoryExampleTable table = new MemoryExampleTable(attributeList, reader);
        this.tearDown();
        return createExampleSet(table, this);
    }

    private static Attribute find(List<Attribute> attributeList, String name) throws OperatorException {
        if(name == null) {
            return null;
        } else {
            Iterator i = attributeList.iterator();

            Attribute attribute;
            do {
                if(!i.hasNext()) {
                    throw new AttributeNotFoundError((Operator)null, (String)null, name);
                }

                attribute = (Attribute)i.next();
            } while(!attribute.getName().equals(name));

            return attribute;
        }
    }

    public static ExampleSet createExampleSet(ExampleTable table, Operator operator) throws OperatorException {
        String labelName = operator.getParameterAsString("label_attribute");
        String weightName = operator.getParameterAsString("weight_attribute");
        String idName = operator.getParameterAsString("id_attribute");
        Attribute label = table.findAttribute(labelName);
        Attribute weight = table.findAttribute(weightName);
        Attribute id = table.findAttribute(idName);
        HashMap specialMap = new HashMap();
        if(label != null) {
            specialMap.put(label, "label");
        }

        if(weight != null) {
            specialMap.put(weight, "weight");
        }

        if(id != null) {
            specialMap.put(id, "id");
        }

        return table.createExampleSet(specialMap);
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        ParameterTypeString type = new ParameterTypeString("label_attribute", "The (case sensitive) name of the label attribute");
        type.setExpert(false);
        types.add(type);
        types.add(new ParameterTypeString("id_attribute", "The (case sensitive) name of the id attribute"));
        types.add(new ParameterTypeString("weight_attribute", "The (case sensitive) name of the weight attribute"));
        types.add(new ParameterTypeCategory("datamanagement", "Determines, how the data is represented internally.", DataRowFactory.TYPE_NAMES, 0));
        return types;
    }
}
