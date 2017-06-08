package io.transwarp.midas;

import com.rapidminer.Process;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.io.process.GUIProcessXMLFilter;
import com.rapidminer.operator.ExecutionUnit;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.OutputPorts;
import com.rapidminer.parameter.*;
import io.transwarp.midas.thrift.message.*;
import io.transwarp.midas.utils.MidasJson;

import java.awt.geom.Rectangle2D;
import java.util.*;

public class MidasJsonExporter {
    public String export(Operator operator) {
        OperatorMsg op = exportOperator(operator);
        List<OperatorMsg> ops = new ArrayList<>();
        ops.add(op);
        ProcessMsg p = new ProcessMsg(ops, new ArrayList<ConnectMsg>());
        Process process = RapidMinerGUI.getMainFrame().getProcess();
        String name = process.getProcessLocation() == null ?
                process.getRootOperator().getName() :
                process.getProcessLocation().getShortName();
        RequestMsg r = new RequestMsg(p);
        r.setName(name);
        return MidasJson.toJson(r, true);
    }

    private OperatorMsg exportOperator(Operator operator) {
        OperatorMsg op = new OperatorMsg();
        op.setName(operator.getName());
        op.setClazz(operator.getOperatorDescription().getKey());
        op.setEnabled(operator.isEnabled());

        Rectangle2D rect = GUIProcessXMLFilter.lookupOperatorRectangle(operator);
        if (rect != null) {
            op.setX((int)rect.getX());
            op.setY((int)rect.getY());
        }

        // parameters
        Parameters parameters = operator.getParameters();
        Map<String, String> map = new HashMap<>();
        Map<String, List<String>> lists = new HashMap<>();
        Map<String, List<TupleMsg>> tuples = new HashMap<>();
        for (String key : parameters.getKeys()) {
            ParameterType type = parameters.getParameterType(key);
            try {
                String value = parameters.getParameter(key);
                List<String> list = type.getValueAsList(value);
                List<String[]> list2d = type.getValueAs2DList(value);
                String transformedValue = type.getValueString(value);

                if (list != null) {
                    lists.put(key, list);
                } else if (list2d != null) {
                    List<TupleMsg> v = new ArrayList<>();
                    for (String[] arr : list2d) {
                        v.add(new TupleMsg(arr[0], arr[1]));
                    }
                    tuples.put(key, v);
                } else if (transformedValue != null) {
                    map.put(key, transformedValue);
                } else if (value != null) {
                    map.put(key, value);
                }
            } catch (UndefinedParameterError undefinedParameterError) {
                undefinedParameterError.printStackTrace();
            }

        }
        op.setParameters(map);
        op.setListParameters(lists);
        op.setMapParameters(tuples);

        if (operator instanceof OperatorChain) {
            OperatorChain chain = (OperatorChain) operator;
            List<ProcessMsg> process = new ArrayList<>();
            for (ExecutionUnit executionUnit : chain.getSubprocesses()) {
                process.add(exportUnit(executionUnit));
            }
            op.setProcesses(process);
            op.setConnects(new ArrayList<ConnectMsg>());
        } else {
            op.setConnects(new ArrayList<ConnectMsg>());
            op.setProcesses(new ArrayList<ProcessMsg>());
        }
        return op;
    }

    private List<ConnectMsg> exportConnections(OutputPorts outputPorts, ExecutionUnit processInScope){
        List<ConnectMsg> connects = new ArrayList<>();
        for (OutputPort outputPort : outputPorts.getAllPorts()) {
            if (outputPort.isConnected()) {
                ConnectMsg connect = new ConnectMsg();
                if (processInScope.getEnclosingOperator() != outputPorts.getOwner().getOperator()) {
                    connect.setFromOp(outputPorts.getOwner().getOperator().getName());
                }
                connect.setFromPort(outputPort.getName());
                InputPort destination = outputPort.getDestination();
                if (processInScope.getEnclosingOperator() != destination.getPorts().getOwner().getOperator()) {
                    connect.setToOp(destination.getPorts().getOwner().getOperator().getName());
                }
                connect.setToPort(destination.getName());

                connects.add(connect);
            }
        }
        return connects;
    }

    private ProcessMsg exportUnit(ExecutionUnit unit) {
        List<OperatorMsg> ops = new ArrayList<>();
        for (Operator op : unit.getOperators()) {
            ops.add(exportOperator(op));
        }

        List<ConnectMsg> connects = new ArrayList<>();
        connects.addAll(exportConnections(unit.getInnerSources(), unit));
        for (Operator op : unit.getOperators()) {
            connects.addAll(exportConnections(op.getOutputPorts(), unit));
        }

        return new ProcessMsg(ops, connects);
    }
}
