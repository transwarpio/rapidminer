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
package io.transwarp.midas.ui.dialog;

import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.renderer.RendererService;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.I18N;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.*;

public class RendersDialog extends ButtonDialog {

	private static final long serialVersionUID = 6615295138614219914L;

	private RendersPane rendersPane = new RendersPane();

	public RendersDialog() {
		this(null);
	}

	public RendersDialog(String initialSelectedTab) {
		super(ApplicationFrame.getApplicationFrame(), "renders", ModalityType.APPLICATION_MODAL, new Object[] {});

		// Create buttons
		Collection<AbstractButton> buttons = new LinkedList<>();
		buttons.add(new JButton(new ResourceAction("settings_ok") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					rendersPane.saveToMemAndDisk();
					setConfirmed(true);
					dispose();
				} catch (IOException ioe) {
					SwingTools.showSimpleErrorMessage("cannot_save_properties", ioe);
				}
			}
		}));
		buttons.add(makeCancelButton());

		layoutDefault(new JScrollPane(rendersPane), NORMAL_EXTENDED, buttons);
	}

	@Override
	public String getInfoText() {
		return I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.renders.message");
	}

	@Override
	protected void close() {
		resetItemStatus();
		super.close();
	}

	@Override
	protected void cancel() {
		resetItemStatus();
		super.cancel();
	}

	@Override
	protected void ok() {
		resetItemStatus();
		super.ok();
	}

	private void resetItemStatus() {

	}

}

class RendersPane extends JPanel {

	class RendersChangeListener implements ItemListener {

		String renderableObjectName;
		String renderName;
		JCheckBox checkBox;

		public RendersChangeListener(String renderableObjectName_, String renderName_, JCheckBox checkBox_) {
			renderableObjectName = renderableObjectName_;
			renderName = renderName_;
			checkBox = checkBox_;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				removeFromBlackList(renderableObjectName, renderName);
			} else {
				addToBlackList(renderableObjectName, renderName);
			}
		}
	}

	public void saveToMemAndDisk() throws IOException {
		RendererService.setBlackList(blackListTmp);
		File save = FileSystemService.getRendererSaveFile();
		FileOutputStream outputStream = new FileOutputStream(save);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(blackListTmp);
		objectOutputStream.close();
		outputStream.close();
	}

	private Map<String, Set<String>> blackListTmp;

	public RendersPane() {
		blackListTmp = RendererService.getBlackList();
		Map<String, Set<String>> OldBlackList = RendererService.getBlackList();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		Set<String> allNames = RendererService.getAllRenderableObjectNames();
		for (String name : allNames) {
			JLabel label = new JLabel(name);
			Font font = label.getFont();
			label.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
			this.add(label);
			for (com.rapidminer.gui.renderer.Renderer render : RendererService.getRenderers(name)) {
				JCheckBox checkBox = new JCheckBox(render.getName());
				checkBox.addItemListener(new RendersChangeListener(name, render.getName(), checkBox));
				checkBox.setSelected(!inBlackList(name, render.getName()));
				this.add(checkBox);
			}
		}
	}

	private boolean inBlackList(String renderableObjectName,
								String renderName) {
		if (blackListTmp.containsKey(renderableObjectName)) {
			if (blackListTmp.get(renderableObjectName).contains(renderName)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void addToBlackList(String renderableObjectName, String renderName) {
		if (!blackListTmp.containsKey(renderableObjectName)) {
			blackListTmp.put(renderableObjectName, new HashSet<String>());
		}
		blackListTmp.get(renderableObjectName).add(renderName);
	}

	private void removeFromBlackList(String renderableObjectName, String renderName) {
		if (blackListTmp.containsKey(renderableObjectName)) {
			Set<String> set = blackListTmp.get(renderableObjectName);
			set.remove(renderName);
			if (set.isEmpty()) {
				blackListTmp.remove(renderableObjectName);
			}
		}
	}
}