package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.parameter.conditions.ParameterCondition;
import io.transwarp.midas.constant.midas.params.features.OutlierSoftenParams;
import io.transwarp.midas.operator.BaseDataProcessOp;

import java.util.List;

/**
 * Created by viki on 17-2-21.
 */
public class OutlierSoftenOp extends BaseDataProcessOp {
    public OutlierSoftenOp(OperatorDescription description) {
        super(description);
        remote = true;
    }

    AttributeSelector selector = new AttributeSelector(this, exampleSetInput);

    @Override
    public List<ParameterType> getParameterTypes() {
        // selected attributes
        List<ParameterType> types = super.getParameterTypes();
        types.addAll(selector.getParameterTypes());

        // method
        ParameterTypeStringCategory method = new ParameterTypeStringCategory(
                OutlierSoftenParams.method(), OutlierSoftenParams.methodDoc(),
                OutlierSoftenParams.supportedMethod(), OutlierSoftenParams.defaultMethod());
        method.setExpert(false);
        types.add(method);

        // the jump-out percentage parameter
        // upper percentage
        ParameterTypeDouble upperPercentage = new ParameterTypeDouble(
                OutlierSoftenParams.upperPercentage(), OutlierSoftenParams.upperPerDoc(), 0, 1, 1.0);
        upperPercentage.registerDependencyCondition(
                new ParameterCondition(this, OutlierSoftenParams.method(), true) {
                    @Override
                    public boolean isConditionFullfilled() {
                        try {
                            if (getParameter(OutlierSoftenParams.method())
                                    .equals(OutlierSoftenParams.percentage())) { return true; }
                        } catch (UndefinedParameterError e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
        );
        types.add(upperPercentage);
        // lower percentage
        ParameterTypeDouble lowerPercentage = new ParameterTypeDouble(
                OutlierSoftenParams.lowerPercentage(), OutlierSoftenParams.lowerPerDoc(), 0, 1, 0.0);
        lowerPercentage.registerDependencyCondition(
                new ParameterCondition(this, OutlierSoftenParams.method(), true) {
                    @Override
                    public boolean isConditionFullfilled() {
                        try {
                            if (getParameter(OutlierSoftenParams.method())
                                    .equals(OutlierSoftenParams.percentage())) { return true; }
                        } catch (UndefinedParameterError e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
        );
        types.add(lowerPercentage);

        // the jump-out threshold parameter
        // upper threshold
        ParameterTypeDouble upperThreshold = new ParameterTypeDouble(OutlierSoftenParams.upperThreshold(),
                OutlierSoftenParams.upperThresholdDoc(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        upperThreshold.registerDependencyCondition(
                new ParameterCondition(this, OutlierSoftenParams.method(), true) {
                    @Override
                    public boolean isConditionFullfilled() {
                        try {
                            if (getParameter(OutlierSoftenParams.method())
                                    .equals(OutlierSoftenParams.threshold())) { return true; }
                        } catch (UndefinedParameterError e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
        );
        types.add(upperThreshold);
        // lower threshold
        ParameterTypeDouble lowerThreshold = new ParameterTypeDouble(OutlierSoftenParams.lowerThreshold(),
                OutlierSoftenParams.lowerThresholdDoc(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        lowerThreshold.registerDependencyCondition(
                new ParameterCondition(this, OutlierSoftenParams.method(), true) {
                    @Override
                    public boolean isConditionFullfilled() {
                        try {
                            if (getParameter(OutlierSoftenParams.method())
                                    .equals(OutlierSoftenParams.threshold())) { return true; }
                        } catch (UndefinedParameterError e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
        );
        types.add(lowerThreshold);

        return types;
    }

}
