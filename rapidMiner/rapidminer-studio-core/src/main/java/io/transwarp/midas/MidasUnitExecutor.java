/**
 * Copyright (C) 2016 Transwarp Technology(Shanghai ) Co., Ltd.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.transwarp.midas;

import com.rapidminer.core.io.data.DataSetException;
import com.rapidminer.core.io.data.ParseException;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.tools.ProcessGUITools;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.*;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.execution.SimpleUnitExecutor;
import com.rapidminer.operator.execution.UnitExecutor;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.Port;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.studio.io.data.DataSetReader;
import com.rapidminer.tools.OperatorService;
import io.transwarp.midas.adaptor.ISchema;
import io.transwarp.midas.constant.midas.ValidationMode;
import io.transwarp.midas.impl.MemoryDataSet;
import io.transwarp.midas.remote.RemoteExecutor;
import io.transwarp.midas.result.JobStatus;
import io.transwarp.midas.result.OpMetaResult;
import io.transwarp.midas.result.PortMetaResult;
import io.transwarp.midas.result.ValidationResult;
import io.transwarp.midas.ui.RemoteResultDisplay;
import io.transwarp.midas.ui.RemoteResultOverview;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes an {@link ExecutionUnit} by invoking the operators in their (presorted) ordering.
 * Instances of this class can be shared.
 *
 * @author Simon Fischer
 *
 */
public class MidasUnitExecutor implements UnitExecutor {
  private Logger logger =  Logger.getLogger(MidasUnitExecutor.class.getName());
  private UnitExecutor simple = new SimpleUnitExecutor();
  private RemoteExecutor remote = new RemoteExecutor();

  private boolean validateRemote(ExecutionUnit unit, String mode) {
    ValidationResult response = remote.validate(unit, mode);
    boolean runnable = true;
    for(OpMetaResult result : response.metas()) {
      Operator op = findOp(unit, result.op());
      if (op == null) {
        logger.warning("operator " + result.op() + " not found");
        continue;
      }

      if (result.error() != null) {
        int code = 1000; // this code is map to the code in UserErrorMessages.properties
        final UserError e = new UserError(op, code, result.error());
        SwingTools.invokeLater(new Runnable() {
          @Override
          public void run() {
            ProcessGUITools.displayBubbleForUserError(e);
          }
        });
        runnable = false;
      } else {
        // add meta info to operator
        scala.collection.immutable.Map<String, PortMetaResult> ports = result.ports();

        op.getInputPorts().clear(Port.CLEAR_META_DATA_ERRORS);
        op.getOutputPorts().clear(Port.CLEAR_META_DATA_ERRORS);
        for (OutputPort out : op.getOutputPorts().getAllPorts()) {
          if (ports.contains(out.getName())) {
            MetaData meta = convertMeta(ports.get(out.getName()).get().schema());
            out.deliverMD(meta);
          }
        }
      }
    }

    unit.transformMetaData();
    return runnable;
  }

  private MetaData convertMeta(ISchema[] schemas) {
    List<ISchema> s = new ArrayList<>();
    for (ISchema tmp : schemas) {
      s.add(tmp);
    }
    MemoryDataSet memoryDataSet = new MemoryDataSet(s);
    DataSetReader reader = new DataSetReader(null, memoryDataSet.getColumnMetaData(), true);
    ExampleSet exampleSet = null;
    try {
      exampleSet = reader.read(memoryDataSet, null);
    } catch (UserError userError) {
      userError.printStackTrace();
    } catch (DataSetException e) {
      e.printStackTrace();
    } catch (ProcessStoppedException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new ExampleSetMetaData(exampleSet);
  }

  private Operator findOp(ExecutionUnit unit, String opName) {
    Operator op = null;
    for (Operator tmp : unit.getOperators()) {
      if (tmp.getName().equals(opName)) {
        op = tmp;
      } else if(tmp instanceof OperatorChain) {
          OperatorChain chain = (OperatorChain) tmp;
          for (ExecutionUnit sub : chain.getSubprocesses()) {
            Operator subOp = findOp(sub, opName);
            if (subOp != null) {
              op = subOp;
              break;
            }
          }
        }

      if (op != null) break;
    }
    return op;
  }

  @Override
  public boolean isRemote(ExecutionUnit unit) {
    return remote.isRemote(unit);
  }

  @Override
  public void execute(ExecutionUnit unit) throws OperatorException {
    Logger logger = unit.getEnclosingOperator().getLogger();
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Executing subprocess " + unit.getEnclosingOperator().getName() + "." + unit.getName()
              + ". Execution order is: " + unit.getOperators());
    }

    if (remote.isRemote(unit)) {
      boolean runnable = validateRemote(unit, ValidationMode.STRICT());
      if (runnable) {
        JobStatus status = remote.execute(unit);
        RemoteResultDisplay display = RapidMinerGUI.getMainFrame().getRemoteResultDisplay();
        boolean shouldSwitchPerspective = !OperatorService.isRapidGUITestMode();
        display.addJobStatus(status, shouldSwitchPerspective);
        final RemoteResultOverview overview = display.getOverview();

        // Scroll the view.
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            Rectangle visibleRect = overview.getVisibleRect();
            visibleRect.y = overview.getHeight() - visibleRect.height;
            overview.scrollRectToVisible(visibleRect);
          }
        });
      }
    } else if (remote.isLocal(unit)) {
        // fallback to simple local mode
        simple.execute(unit);
    } else {
      // mix of remote and local, can't execute now
      throw new RuntimeException("can not mix remote operator with local one");
    }
  }

  @Override
  public boolean validate(ExecutionUnit unit, String mode) throws OperatorException {
    if (remote.isRemote(unit)) {
      return validateRemote(unit, mode);
    } else if (remote.isLocal(unit)){
      return simple.validate(unit, mode);
    } else {
      // mix of remote and local, can't execute now
      throw new RuntimeException("can not mix remote operator with local one");
    }
  }
}
