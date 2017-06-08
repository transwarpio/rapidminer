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
package io.transwarp.midas.renderer;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.gui.renderer.AbstractTableModelTableRenderer;
import com.rapidminer.gui.viewer.DataTableViewerTableModel;
import com.rapidminer.operator.IOContainer;
import io.transwarp.midas.model.LinearModel;

import javax.swing.table.TableModel;

public class LinearModelWeightsRenderer extends AbstractTableModelTableRenderer {

	@Override
	public String getName() {
		return "Weight Table";
	}

	@Override
	public TableModel getTableModel(Object renderable, IOContainer ioContainer, boolean isReporting) {
		LinearModel linearModel = (LinearModel) renderable;
		DataTable weightDataTable = linearModel.createWeightsTable();
		if (weightDataTable != null) {
			return new DataTableViewerTableModel(weightDataTable);
		} else {
			return null;
		}
	}
}
