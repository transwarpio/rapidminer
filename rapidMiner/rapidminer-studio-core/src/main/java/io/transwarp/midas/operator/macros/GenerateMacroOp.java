package io.transwarp.midas.operator.macros;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.macros.MacroParams;

import java.util.ArrayList;
import java.util.List;

public class GenerateMacroOp extends BaseMacroOp {
    public GenerateMacroOp(OperatorDescription description) {
        super(description);
    }
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(
                new ParameterTypeString(MacroParams.Macro(),
                        "macro name", false, false));
        types.add(new ParameterTypeText(
                MacroParams.Script(), "script to run",
                TextType.PLAIN, false));
        return types;
    }
}
