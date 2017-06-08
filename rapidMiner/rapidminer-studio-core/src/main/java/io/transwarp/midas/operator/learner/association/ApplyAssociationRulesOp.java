package io.transwarp.midas.operator.learner.association;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.learner.associations.AssociationRules;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import io.transwarp.midas.operator.BaseOp;

/**
 * Created by linchen on 16-8-29.
 */
public class ApplyAssociationRulesOp extends BaseOp {
    private InputPort exampleSetInput = getInputPorts().createPort("example set", ExampleSet.class);
    private InputPort associationRulesInput = getInputPorts().createPort("association rules", AssociationRules.class);

    private OutputPort exampleSetOutput = getOutputPorts().createPort("example set");

    public ApplyAssociationRulesOp(OperatorDescription description) {
        super(description);
    }
}
