package io.transwarp.midas.operator.macros;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeEnumeration;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.macros.MacroParams;

import java.util.ArrayList;
import java.util.List;

public class ExtractMacroOp extends BaseMacroOp {
    public ExtractMacroOp(OperatorDescription description) {
        super(description);
    }
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeEnumeration(
                MacroParams.Keys(), "columns",
                new ParameterTypeString("column", "column name", false)
        ));
        return types;
    }
}
