package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ProcessSetupError;
import com.rapidminer.operator.SimpleProcessSetupError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.*;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 16-10-19.
 */
public class UnionOp extends BaseOp {
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");

    private final InputPortExtender inputExtender = new InputPortExtender("example set", getInputPorts()) {

        @Override
        protected Precondition makePrecondition(InputPort port) {
            return new ExampleSetPrecondition(port) {

                {
                    setOptional(true);
                }

                @Override
                public void makeAdditionalChecks(ExampleSetMetaData emd) throws UndefinedParameterError {
                    for (MetaData metaData : inputExtender.getMetaData(true)) {
                        if (metaData instanceof ExampleSetMetaData) {
                            MetaDataInfo result = emd.equalHeader((ExampleSetMetaData) metaData);
                            if (result == MetaDataInfo.NO) {
                                addError(new SimpleProcessSetupError(ProcessSetupError.Severity.ERROR, getPortOwner(),
                                        "exampleset.sets_incompatible"));
                                break;
                            }
                            if (result == MetaDataInfo.UNKNOWN) {
                                addError(new SimpleProcessSetupError(ProcessSetupError.Severity.WARNING, getPortOwner(),
                                        "exampleset.sets_incompatible"));
                                break;
                            }
                        }
                    }
                }
            };
        }
    };

    public UnionOp(OperatorDescription description) {
        super(description);
        inputExtender.start();
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        return types;
    }
}
