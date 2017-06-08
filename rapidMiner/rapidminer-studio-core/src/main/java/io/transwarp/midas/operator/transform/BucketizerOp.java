package io.transwarp.midas.operator.transform;


import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeEnumeration;
import io.transwarp.midas.constant.midas.params.preprocess.BucketizerParams;

import java.util.List;

public class BucketizerOp extends SelectAttributeOp {
    public BucketizerOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = new ParameterTypeEnumeration(
                BucketizerParams.Splits(),
                "Split points for mapping continuous features into buckets. With n+1 splits, " +
                 "there are n buckets. A bucket defined by splits x,y holds values in the " +
                 "range [x,y) except the last bucket, which also includes y. The splits " +
                 "should be of length >= 3 and strictly increasing",
                new ParameterTypeDouble("split", "The value of split",
                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(BucketizerParams.DefineBoundaries(), "Whether add -inf " +
                "and +inf as the bounds of your splits to prevent a potential out of bucketizer " +
                "bounds exception", false, false);
        types.add(type);

        return types;
    }
}
