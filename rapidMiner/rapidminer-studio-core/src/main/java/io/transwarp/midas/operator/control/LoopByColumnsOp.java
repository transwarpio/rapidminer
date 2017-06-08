package io.transwarp.midas.operator.control;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ValueDouble;
import com.rapidminer.operator.ValueString;
import com.rapidminer.operator.ports.CollectingPortPairExtender;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.*;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.params.control.LoopParams;
import io.transwarp.midas.operator.transform.AttributeSelector;

import java.util.List;

public class LoopByColumnsOp extends OperatorChain {

    private final InputPort exampleSetInput = getInputPorts().createPort("example set input", ExampleSet.class);
    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");
    private final OutputPort exampleSetInnerSource = getSubprocess(0).getInnerSources().createPort("source");
    private final InputPort exampleSetInnerSink = getSubprocess(0).getInnerSinks().createPort("sink");

    private int iteration;

    private String currentName = null;

    private final CollectingPortPairExtender innerSinkExtender;

    AttributeSelector selector = new AttributeSelector(this, exampleSetInput);

    public LoopByColumnsOp(OperatorDescription description) {
        super(description, "Subprocess");
        remote = true;

        exampleSetInnerSink.addPrecondition(new SimplePrecondition(exampleSetInnerSink, new ExampleSetMetaData(), false));
        innerSinkExtender = new CollectingPortPairExtender("result", getSubprocess(0).getInnerSinks(), getOutputPorts());
        innerSinkExtender.start();

        getTransformer().addRule(new PassThroughRule(exampleSetInput, exampleSetInnerSource, false));
        getTransformer().addRule(new SubprocessTransformRule(getSubprocess(0)));
        getTransformer().addRule(innerSinkExtender.makePassThroughRule());
        getTransformer().addRule(new PassThroughRule(exampleSetInput, exampleSetOutput, false) {

            @Override
            public MetaData modifyMetaData(MetaData unmodifiedMetaData) {
                if (exampleSetInnerSink.isConnected()) {
                    return exampleSetInnerSink.getMetaData();
                } else {
                    // due to side effects, we cannot make any guarantee about the output.
                    return new ExampleSetMetaData();
                }
            }
        });

        addValue(new ValueDouble("iteration", "The number of the current iteration / loop.") {

            @Override
            public double getDoubleValue() {
                return iteration;
            }
        });

        addValue(new ValueString("feature_name", "The number of the current feature.") {

            @Override
            public String getStringValue() {
                return currentName;
            }
        });
    }

    @Override
    public boolean shouldAutoConnect(InputPort inputPort) {
        if (inputPort == exampleSetInnerSink) {
            return true;
        } else {
            return super.shouldAutoConnect(inputPort);
        }
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.addAll(selector.getParameterTypes());

        types.add(new ParameterTypeString(
                LoopParams.MacroKey(), "macro key", "current_column", false
        ));
        return types;
    }
}
