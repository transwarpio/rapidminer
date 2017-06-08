package io.transwarp.midas.operator.deeplearning.layer.rnn;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.deep.RNNCellParams;
import io.transwarp.midas.operator.deeplearning.layer.AbstractLayer;

import java.util.List;

public abstract class SingleRNNCell extends AbstractLayer {

    public SingleRNNCell(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        // Parameter number of units.
        ParameterTypeInt type = new ParameterTypeInt(
                RNNCellParams.NumUnits(),
                "The number of units in the RNN cell",
                1, Integer.MAX_VALUE, 10, false);
        types.add(type);

        // Parameter activation function.
        types.add(new ParameterTypeCategory(
                RNNCellParams.Activation(),
                "The activation function of the inner states.",
                RNNCellParams.ActivationOptions(),
                0, false));

        return types;
    }
}
