package io.transwarp.midas.operator.learner;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.classification.KNNParams;
import io.transwarp.midas.operator.BaseLearnerOp;

import java.util.ArrayList;
import java.util.List;


public class KNNClassifierOp extends BaseLearnerOp {

    public KNNClassifierOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        ParameterType type = new ParameterTypeInt(KNNParams.K(), "The k value of k nearest " +
                "neighbors", 1,
                Integer.MAX_VALUE, 5, false);
        types.add(type);

        type = new ParameterTypeInt(KNNParams.TopTreeSize(), "Number of points to sample for " +
                "top-level tree "
                , 1,
                Integer.MAX_VALUE, 1000, false);
        types.add(type);

        type = new ParameterTypeInt(KNNParams.TopTreeLeafSize(), "Number of points at which to " +
                "switch to brute-force for top-level tree", 1,
                Integer.MAX_VALUE, 5, false);
        types.add(type);

        type = new ParameterTypeInt(KNNParams.SubTreeLeafSize(), "number of points at which to" +
                " switch to brute-force for distributed sub-trees", 1,
                Integer.MAX_VALUE, 20, false);
        types.add(type);


        return types;
    }
}
