package io.transwarp.midas.operator.deeplearning.layer.rnn;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import io.transwarp.midas.constant.midas.params.deep.RNNCellParams;

import java.util.ArrayList;
import java.util.List;

public class MultipleRNNCell extends SingleRNNCell {

    public MultipleRNNCell(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<ParameterType>();

        // Parameter cell.
        types.add(new ParameterTypeCategory(
                RNNCellParams.Cell(),
                "Specify the RNN cell to be used. Multiple specified cells " +
                        "will be assembled to one Multiple RNN Cell here.",
                RNNCellParams.CellOptions(),
                0, false));

        // Parameter number of cells.
        types.add(new ParameterTypeInt(
                RNNCellParams.NumCells(),
                "Combine specified number of cell to a Multiple RNN Cell.",
                0, Integer.MAX_VALUE, 2, false));

        // Add parent's parameters.
        types.addAll(super.getParameterTypes());

        return types;
    }
}
