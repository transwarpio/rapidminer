package io.transwarp.midas.operator.evaluation;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import io.transwarp.midas.constant.midas.PortNames;
import io.transwarp.midas.constant.midas.params.tuning.PerfParams;

public class ClusterPerformanceOp extends AbstractPerformanceOp {

    private InputPort modelInput = getInputPorts().createPort(PortNames.Model());

    public ClusterPerformanceOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public String[] getChoices() {
        return PerfParams.ClusterOptions();
    }
}
