package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.data.DataTypeTransformerParams;
import java.util.List;

/**
 * Created by linchen on 16-10-28.
 */
public class DataTypeTransformerOp extends SelectAttributeOp {
    public DataTypeTransformerOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = new ParameterTypeStringCategory(DataTypeTransformerParams.defaultTargetType(),
                "Data type.", DataTypeTransformerParams.dataTypes(), "string");
        types.add(type);

        ParameterTypeList list = new ParameterTypeList(DataTypeTransformerParams.additionalAttributes(),
                "Please choose attributes which you want to cast data type.",
                new ParameterTypeAttribute
                        (DataTypeTransformerParams.attributeName(),
                                "The data type of the attribute whose role should be changed.",
                                exampleSetInput, false, false),
                new ParameterTypeStringCategory(DataTypeTransformerParams.targetDataType(),
                        "The target data type of the attribute (only changed if parameter " +
                                "choose_attributes is true).",
                        DataTypeTransformerParams.dataTypes(), DataTypeTransformerParams.dataTypes()[0]), false);
        types.add(list);

        return types;
    }
}
