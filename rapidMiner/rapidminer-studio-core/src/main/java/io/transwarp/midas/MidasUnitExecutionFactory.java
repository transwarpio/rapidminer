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

import com.rapidminer.operator.ExecutionUnit;
import com.rapidminer.operator.execution.SimpleUnitExecutor;
import com.rapidminer.operator.execution.UnitExecutionFactory;
import com.rapidminer.operator.execution.UnitExecutor;


/**
 * Returns a shared instance of a {@link SimpleUnitExecutor}.
 *
 * @author Simon Fischer
 *
 */
public class MidasUnitExecutionFactory extends UnitExecutionFactory {

  private final UnitExecutor executor = new MidasUnitExecutor();

  @Override
  public UnitExecutor getExecutor(ExecutionUnit unit) {
    return executor;
  }

}
