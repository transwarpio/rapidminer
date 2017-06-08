package io.transwarp.midas.operator.transform;


import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.parameter.TextType;
import io.transwarp.midas.constant.midas.PortNames;
import io.transwarp.midas.constant.midas.params.data.SQLTransformerParams;
import io.transwarp.midas.operator.BaseDataProcessOp;
import io.transwarp.midas.operator.BaseOp;
import io.transwarp.midas.ui.property.ParameterTypeSqlExpr;

import java.util.ArrayList;
import java.util.List;

public class SqlTransformerOp  extends BaseDataProcessOp {
    public SqlTransformerOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeSqlExpr(
                SQLTransformerParams.Statement(),
                "SQL statement parameter.", exampleSetInput, false);
        types.add(type);
        return types;
    }
}
