package io.transwarp.midas.operator.table;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.SplittedExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPortExtender;
import com.rapidminer.parameter.*;
import com.rapidminer.tools.RandomGenerator;
import io.transwarp.midas.constant.midas.params.data.SplitDataParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

public class SplitOp extends BaseOp {
    private InputPort exampleSetInput = getInputPorts().createPort("example set", ExampleSet.class);
    private OutputPortExtender outExtender = new OutputPortExtender("partition", getOutputPorts());

    public SplitOp(OperatorDescription description) {
        super(description);
        outExtender.start();
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeEnumeration(SplitDataParams.Partitions(), "The partitions that should be created.",
                new ParameterTypeDouble("ratio", "The relative size of this partition.", 0, 1), false));
        types.add(new ParameterTypeInt(SplitDataParams.Seed(), "random seed", Integer.MIN_VALUE, Integer.MAX_VALUE, 4000,false));
        return types;
    }
}
