package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.parameter.conditions.EqualStringCondition;
import com.rapidminer.parameter.conditions.ParameterCondition;
import io.transwarp.midas.constant.midas.params.association.AssociationParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 16-8-30.
 */
public class ReshapeDataOp extends BaseOp {
    private String[] DATA_TYPES = {"transaction", "sequence"};

    private InputPort exampleSetInput = getInputPorts().createPort("example set");
    private OutputPort output = getOutputPorts().createPort("output");
    private OutputPort original = getOutputPorts().createPort("original");

    public ReshapeDataOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        types.add(new ParameterTypeStringCategory(AssociationParams.DataType(), "model type using a string", DATA_TYPES , DATA_TYPES[0], false));

        ParameterType type = new ParameterTypeString(AssociationParams.CustomerId(), "id", true);
        type.registerDependencyCondition(new EqualStringCondition(this, AssociationParams.DataType(), false, "sequence"));
        types.add(type);

        type = new ParameterTypeString(AssociationParams.TimeAttribute(), "time", true);
        type.registerDependencyCondition(new EqualStringCondition(this, AssociationParams.DataType(), false, "sequence"));
        types.add(type);

        return types;
    }
}
