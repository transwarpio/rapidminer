package io.transwarp.midas.operator.deeplearning.layer.rnn;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import io.transwarp.midas.constant.midas.params.deep.RNNCellParams;

import java.util.List;

public class BasicLSTMCell extends SingleRNNCell {

    public BasicLSTMCell(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        // Parameter activation function.
        types.add(new ParameterTypeDouble(
                RNNCellParams.forgetBias(),
                "The forget bias of the LSTM cell." +
                        "Biases of the forget gate are initialized by default to 1 " +
                        "in order to reduce the scale of forgetting at the " +
                        "beginning of the training",
                0, Double.MAX_VALUE, 1, true));

        return types;
    }
}
