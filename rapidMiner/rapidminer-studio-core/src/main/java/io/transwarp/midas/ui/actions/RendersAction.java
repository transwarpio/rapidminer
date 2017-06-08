/**
 * Copyright (C) 2016 Transwarp Technology(Shanghai ) Co., Ltd.
 *
 * This program is free software; you can redistribute  it and/or modify it
 * under  the terms of  the GNU General  Public License as published by the
 * Free Software Foundation;  either version 3 of the  License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.transwarp.midas.ui.actions;

import com.rapidminer.gui.tools.ResourceAction;
import io.transwarp.midas.ui.dialog.RendersDialog;

import java.awt.event.ActionEvent;


/**
 * Start the corresponding action.
 *
 * @author Ingo Mierswa
 */
public class RendersAction extends ResourceAction {

	private static final long serialVersionUID = 4675057674892640002L;

	public RendersAction() {
		super("renders");
	}

	/**
	 * Opens the settings dialog
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new RendersDialog().setVisible(true);
	}
}
