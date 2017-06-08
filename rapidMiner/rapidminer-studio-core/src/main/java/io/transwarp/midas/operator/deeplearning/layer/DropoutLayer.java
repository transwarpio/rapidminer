package io.transwarp.midas.operator.deeplearning.layer;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.deep.DropoutLayerParams;

import java.util.List;

public class DropoutLayer extends AbstractLayer {

    // Constructor
    public DropoutLayer(OperatorDescription description) {
        super(description);
    }

    public List<ParameterType> getParameterTypes() {

        List<ParameterType> types = super.getParameterTypes();
        // Parameter dropout
        types.add(new ParameterTypeDouble(
                DropoutLayerParams.Dropout(),
                "Probability of being randomly eliminated",
                0, 1, 0.5, false));

        return types;
    }
}
