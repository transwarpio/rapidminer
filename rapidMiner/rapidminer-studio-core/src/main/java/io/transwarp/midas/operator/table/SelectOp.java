package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.table.SelectParams;
import io.transwarp.midas.operator.BaseDataProcessOp;
import io.transwarp.midas.ui.property.ParameterTypeSqlExpr;

import java.util.ArrayList;
import java.util.List;

public class SelectOp extends BaseDataProcessOp {
    public SelectOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeList(SelectParams.Columns(),
                "the columns to be selected",
                new ParameterTypeSqlExpr("column",
                        "The columns, support sql expression", exampleSetInput, false),
                new ParameterTypeString("rename",
                        "the name, leave it empty if you do not want to rename", ""),false));
        return types;
    }
}
