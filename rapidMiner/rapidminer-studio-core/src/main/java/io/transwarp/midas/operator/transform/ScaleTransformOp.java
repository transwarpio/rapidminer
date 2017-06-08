package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.PortNames;
import io.transwarp.midas.constant.midas.params.data.ScaleTransformParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viki on 17-2-15.
 */
public class ScaleTransformOp extends BaseOp {
    public ScaleTransformOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    private InputPort input = getInputPorts().createPort(PortNames.Input());
    private OutputPort output = getOutputPorts().createPort(PortNames.Output());

    private String[] MODE_TYPES = ScaleTransformParams.supportedMethod();

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> list = new ArrayList<>();

        ParameterType type = new ParameterTypeStringCategory(ScaleTransformParams.scaleMethod(),
                "scale method", MODE_TYPES , MODE_TYPES[0], false);
        type.setOptional(false);
        list.add(type);

        type = new ParameterTypeString(ScaleTransformParams.scaleCol(), "column to transform");
        type.setOptional(false);
        list.add(type);

        return list;
    }
}
