package io.transwarp.midas.operator.macros;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.macros.MacroParams;

import java.util.ArrayList;
import java.util.List;

public class RemoveMacroOp extends BaseMacroOp {
    public RemoveMacroOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeList(
                MacroParams.Macros(), "macros to be removed",
                new ParameterTypeString("macro name", "macro name", false),
                new ParameterTypeString("value", "value", false)
        ));
        return types;
    }
}
