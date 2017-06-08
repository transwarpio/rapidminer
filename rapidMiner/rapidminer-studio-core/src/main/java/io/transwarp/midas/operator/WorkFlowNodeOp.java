package io.transwarp.midas.operator;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPortExtender;
import com.rapidminer.parameter.ParameterType;
import io.transwarp.midas.constant.midas.params.WorkFlowNodeParams;
import io.transwarp.midas.operator.retrieve.ParameterTypeFileUpload;
import io.transwarp.midas.operator.retrieve.ParameterTypeProcessFile;

import java.util.LinkedList;
import java.util.List;


public class WorkFlowNodeOp extends BaseOp {

    private final InputPortExtender inputExtender = new InputPortExtender("input", getInputPorts());
    private final OutputPortExtender outputExtender = new OutputPortExtender("output", getOutputPorts());
    public WorkFlowNodeOp(OperatorDescription description) {
        super(description);
        inputExtender.start();
        outputExtender.start();
    }
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<>();
        ParameterType file = new ParameterTypeProcessFile(WorkFlowNodeParams.subprocess(), "subprocess rmp file", "rmp", false);
        types.add(file);
        return types;
    }
}
