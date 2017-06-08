package io.transwarp.midas.operator.deeplearning.layer.rnn;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;

import java.util.List;

public class GRUCell extends SingleRNNCell {

    public GRUCell(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        return super.getParameterTypes();
    }
}
