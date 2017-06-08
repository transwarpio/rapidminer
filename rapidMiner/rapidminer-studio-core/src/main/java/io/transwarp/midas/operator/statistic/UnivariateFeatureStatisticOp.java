package io.transwarp.midas.operator.statistic;

import com.rapidminer.operator.OperatorDescription;
import io.transwarp.midas.operator.transform.SelectAttributeOp;

public class UnivariateFeatureStatisticOp extends SelectAttributeOp {

    public UnivariateFeatureStatisticOp(OperatorDescription description) {
        super(description);
        remote = true;
    }
}
