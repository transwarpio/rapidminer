package com.rapidminer.extension.jdbc.operator.io;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.JdbcParams;

import java.util.List;

public class WriteJdbcOp extends BaseWriteDatabaseOp {
    public WriteJdbcOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeInt(JdbcParams.BatchSize(), "write batch size",
                                1, Integer.MAX_VALUE, 1000));

        types.add(new ParameterTypeBoolean(JdbcParams.Truncate(),
                "truncate table when overwrite", false, true));
        return types;
    }
}
