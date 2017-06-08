package io.transwarp.midas.operator.custom;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPortExtender;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.parameter.conditions.EqualStringCondition;
import io.transwarp.midas.client.CustomOpManager;
import io.transwarp.midas.client.CustomOpProperty;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.constant.midas.params.custom.PythonParams;
import io.transwarp.midas.operator.BaseOp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PythonOp extends BaseCustomOp {
    public PythonOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        String desc = "please do not use pyspark method to create spark context, use entry.sc, " +
                "entry.spark to get SparkContext and SparkSession. use entry.get_df(index) to get " +
                "DataFrame from input, entry.put_df(DataFrame) to put result to output";
        ParameterType type = new ParameterTypeText(PythonParams.Code(), desc, TextType.PYTHON);
        type.setExpert(false);
        types.add(type);

        List<String> files = new ArrayList<>();
        scala.collection.Iterator<CustomOpProperty> iter = CustomOpManager.getPythonOps().toIterator();
        while(iter.hasNext()) {
            CustomOpProperty p = iter.next();
            files.add(p.getFile());
        }

        type = new ParameterTypeBoolean(PythonParams.HasLibs(), "use custom libs or not", false, false);
        types.add(type);

        String[] a = new String[files.size()];
        type = new ParameterTypeStringCategory(PythonParams.File(), "script dependency", files.toArray(a), "");
        type.setExpert(false);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PythonParams.HasLibs(), true, true));
        types.add(type);

        return types;
    }
}
