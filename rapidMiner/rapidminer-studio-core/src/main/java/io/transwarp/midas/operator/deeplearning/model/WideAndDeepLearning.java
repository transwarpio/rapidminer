package io.transwarp.midas.operator.deeplearning.model;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import io.transwarp.midas.constant.midas.PortNames;
import io.transwarp.midas.constant.midas.params.deep.ArtificialNeuralNetworkParams;
import io.transwarp.midas.constant.midas.params.deep.WideAndDeepLearningParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

public class WideAndDeepLearning extends BaseOp {
    // Ports
    protected OutputPort modelPort = getOutputPorts().createPort(PortNames.Model());
    protected InputPort trainSetPort = getInputPorts().createPort(PortNames.TrainSet());
    protected InputPort testSetPort = getInputPorts().createPort(PortNames.TestSet());

    public WideAndDeepLearning(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        // Parameter framework
        types.add(new ParameterTypeCategory(
                WideAndDeepLearningParams.Framework(),
                "The framework used to generate deep learning code",
                WideAndDeepLearningParams.FrameworkOptions(),
                0, false));

        // Parameter model type
        types.add(new ParameterTypeCategory(
                WideAndDeepLearningParams.ModelType(),
                "The model type",
                WideAndDeepLearningParams.ModelTypeOptions(),
                2, false));

        // Parameter model dir
        types.add(new ParameterTypeString(
                WideAndDeepLearningParams.ModelDir(),
                "The full path to store summary after the process is finished",
                "", false));

        // Parameter training steps
        types.add(new ParameterTypeInt(
                WideAndDeepLearningParams.TrainingSteps(),
                "The number of steps used for the neural network training.",
                1, Integer.MAX_VALUE, 200));

        return types;
    }
}
