package io.transwarp.midas.operator.table;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import io.transwarp.midas.constant.midas.PortNames;
import io.transwarp.midas.constant.midas.params.table.JoinParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;

/**
 * Created by linchen on 16-10-13.
 */
public class JoinOp extends BaseOp {
    protected static final String LEFT_EXAMPLE_SET_INPUT = PortNames.Left();
    protected static final String RIGHT_EXAMPLE_SET_INPUT = PortNames.Right();

    private InputPort leftInput = getInputPorts().createPort(LEFT_EXAMPLE_SET_INPUT);
    private InputPort rightInput = getInputPorts().createPort(RIGHT_EXAMPLE_SET_INPUT);
    private OutputPort joinOutput = getOutputPorts().createPort("example set output");

    public static final String PARAMETER_JOIN_TYPE = JoinParams.JoinType();
    public static final String PARAMETER_JOIN_ATTRIBUTES = JoinParams.On();
    public static final String PARAMETER_LEFT_ATTRIBUTE_FOR_JOIN = JoinParams.LeftKeyColumns();
    public static final String PARAMETER_RIGHT_ATTRIBUTE_FOR_JOIN = JoinParams.RightKeyColumns();
    public static final String[] JOIN_TYPES = JoinParams.JoinTypes();

    public static final int JOIN_TYPE_INNER = 0;

    public JoinOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeCategory(PARAMETER_JOIN_TYPE, "Specifies which join should be executed.", JOIN_TYPES,
                JOIN_TYPE_INNER, false));

        types.add(new ParameterTypeEnumeration(JoinParams.LeftColumns(),
                "the columns in left table",
                new ParameterTypeAttribute("column",
                        "The columns, support sql expression", leftInput, false)));

        types.add(new ParameterTypeEnumeration(JoinParams.RightColumns(),
                "the columns in right table",
                new ParameterTypeAttribute("column",
                        "The columns, support sql expression", rightInput, false)));

        ParameterType joinAttributes = new ParameterTypeList(PARAMETER_JOIN_ATTRIBUTES,
                "The attributes which shall be used for join. Attributes which shall be matched must be of the same type.",
                new ParameterTypeAttribute(
                        PARAMETER_LEFT_ATTRIBUTE_FOR_JOIN,
                        "The attribute in the left example set to be used for the join.",
                        leftInput, false),
                new ParameterTypeAttribute(
                        PARAMETER_RIGHT_ATTRIBUTE_FOR_JOIN,
                        "The attribute in the left example set to be used for the join.",
                        rightInput, false), false);
        types.add(joinAttributes);


        ParameterType addLeftPrefix = new ParameterTypeBoolean(JoinParams.AddLeftPrefix(),
                "Indicates if adding left prefix to columns in the left table.", false);
        addLeftPrefix.setExpert(false);
        types.add(addLeftPrefix);

        ParameterType leftPrefix = new ParameterTypeString(
                JoinParams.LeftPrefix(),
                "The prefix of left columns to be added",
                true);

        leftPrefix.registerDependencyCondition(new BooleanParameterCondition(this, JoinParams.AddLeftPrefix(), false, true));
        types.add(leftPrefix);

        ParameterType addRightPrefix = new ParameterTypeBoolean(JoinParams.AddRightPrefix(),
                "Indicates if adding right prefix to columns in the right table.", false);
        addRightPrefix.setExpert(false);
        types.add(addRightPrefix);

        ParameterType rightPrefix = new ParameterTypeString(
                JoinParams.RightPrefix(),
                "The prefix of right columns to be added", true);

        rightPrefix.registerDependencyCondition(new BooleanParameterCondition(this, JoinParams.AddRightPrefix(), false, true));
        types.add(rightPrefix);

        return types;
    }
}
