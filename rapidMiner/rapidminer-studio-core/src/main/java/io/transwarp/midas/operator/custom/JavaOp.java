package io.transwarp.midas.operator.custom;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.client.CustomOpManager;
import io.transwarp.midas.client.CustomOpProperty;
import io.transwarp.midas.constant.midas.params.CustomParams;

import java.util.ArrayList;
import java.util.List;

public class JavaOp extends BaseCustomOp {
    public JavaOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        List<String> clazz = new ArrayList<>();
        scala.collection.Iterator<CustomOpProperty> iter = CustomOpManager.getJavaOps().toIterator();
        while(iter.hasNext()) {
            CustomOpProperty p = iter.next();
            clazz.add(p.getClazz());
        }
        String[] a = new String[clazz.size()];
        ParameterType type = new ParameterTypeStringCategory(CustomParams.Clazz(), "class to run", clazz.toArray(a), "unknown");
        type.setExpert(false);
        types.add(type);

        return types;
    }
}
