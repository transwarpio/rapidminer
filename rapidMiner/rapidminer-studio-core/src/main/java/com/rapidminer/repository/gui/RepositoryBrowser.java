/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
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
package com.rapidminer.repository.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.TreePath;

import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.actions.ImportDataAction;
import com.rapidminer.gui.actions.OpenAction;
import com.rapidminer.gui.dnd.AbstractPatchedTransferHandler;
import com.rapidminer.gui.dnd.DragListener;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.properties.PropertyPanel;
import com.rapidminer.gui.tools.*;
import com.rapidminer.gui.tools.components.DropDownPopupButton;
import com.rapidminer.gui.tools.components.DropDownPopupButton.PopupMenuProvider;
import com.rapidminer.repository.Entry;
import com.rapidminer.repository.IOObjectEntry;
import com.rapidminer.repository.ProcessEntry;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.tools.I18N;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;


/**
 * A component to browse through repositories.
 *
 * @author Simon Fischer
 */
public class RepositoryBrowser extends JPanel implements Dockable, FilterListener {

	private static final long serialVersionUID = 1L;

	public static final Action ADD_REPOSITORY_ACTION = new ResourceAction(true, "add_repository") {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			addRepository();
		}
	};

	private transient final ResourceAction CLEAR_FILTER_ACTION = new ResourceAction(true, "clear_filter") {

		private static final long serialVersionUID = 3236281211064051583L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			filterField.clearFilter();
			filterField.requestFocusInWindow();
		}
	};

	private final ImageIcon CLEAR_FILTER_HOVERED_ICON = SwingTools.createIcon("16/x-mark_orange.png");

	private final FilterTextField filterField = new FilterTextField(12);
	private String currentFilter = "";
	private final RepositoryTree tree;

	public RepositoryBrowser() {
		this(null);
	}

	/**
	 * @param dragListener
	 *            registers a dragListener at the repository tree transferhandler. The listener is
	 *            informed when a drag starts and a drag ends.
	 */
	public RepositoryBrowser(DragListener dragListener) {
		tree = new RepositoryTree();
		if (dragListener != null) {
			((AbstractPatchedTransferHandler) tree.getTransferHandler()).addDragListener(dragListener);
		}
		tree.addRepositorySelectionListener(new RepositorySelectionListener() {

			@Override
			public void repositoryLocationSelected(RepositorySelectionEvent e) {
				Entry entry = e.getEntry();
				if (entry instanceof ProcessEntry) {
					RepositoryTree.openProcess((ProcessEntry) entry);
				} else if (entry instanceof IOObjectEntry) {
					OpenAction.showAsResult((IOObjectEntry) entry);
				}
			}
		});

		setLayout(new BorderLayout());

		final JPopupMenu furtherActionsMenu = new JPopupMenu();
		furtherActionsMenu.add(ADD_REPOSITORY_ACTION);
		furtherActionsMenu.add(tree.CREATE_FOLDER_ACTION);
		furtherActionsMenu.add(tree.REFRESH_ACTION);
		furtherActionsMenu.add(tree.SHOW_PROCESS_IN_REPOSITORY_ACTION);

		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.insets = new Insets(2, 2, 2, 2);

		JButton addDataButton = new JButton(new ImportDataAction(true));
		addDataButton.setPreferredSize(new Dimension(100, 30));
		northPanel.add(addDataButton, c);

		DropDownPopupButton furtherActionsButton = new DropDownPopupButton("gui.action.further_repository_actions",
				new PopupMenuProvider() {

					@Override
					public JPopupMenu getPopupMenu() {
						return furtherActionsMenu;
					}

				});
		furtherActionsButton.setPreferredSize(new Dimension(50, 30));

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		northPanel.add(furtherActionsButton, c);

		// search bar
		filterField.setToolTipText(I18N.getMessage(I18N.getGUIBundle(), "gui.field.filter_repo.tip"));
		filterField.addFilterListener(this);
		filterField.setDefaultFilterText(I18N.getMessage(I18N.getGUIBundle(), "gui.field.filter_repo.prompt"));

		JPanel headerBar = new JPanel(new BorderLayout());
		TextFieldWithAction tf = new TextFieldWithAction(filterField, CLEAR_FILTER_ACTION, CLEAR_FILTER_HOVERED_ICON) {

			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, PropertyPanel.VALUE_CELL_EDITOR_HEIGHT);
			}
		};
		headerBar.add(tf, BorderLayout.CENTER);
		headerBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		northPanel.add(headerBar, c);

		add(northPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new ExtendedJScrollPane(tree);
		scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.TEXTFIELD_BORDER));
		add(scrollPane, BorderLayout.CENTER);
	}

	private static void addRepository() {
		NewRepositoryDialog.createNew();
	}

	/**
	 * Returns the {@link RepositoryTree} managed by this browser.
	 *
	 * @return the repository tree
	 * @since 7.0.0
	 */
	public RepositoryTree getRepositoryTree() {
		return tree;
	}

	public static final String REPOSITORY_BROWSER_DOCK_KEY = "repository_browser";
	private final DockKey DOCK_KEY = new ResourceDockKey(REPOSITORY_BROWSER_DOCK_KEY);

	{
		DOCK_KEY.setDockGroup(MainFrame.DOCK_GROUP_ROOT);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public DockKey getDockKey() {
		return DOCK_KEY;
	}

	/**
	 * @param storedRepositoryLocation
	 */
	public void expandToRepositoryLocation(RepositoryLocation storedRepositoryLocation) {
		tree.expandAndSelectIfExists(storedRepositoryLocation);
	}

	@Override
	public void valueChanged(String value) {
		if (!isFilterChanged(value)) {
			return;
		}
		currentFilter = value;
		RepositoryTreeModel model = (RepositoryTreeModel) tree.getModel();
		model.applyFilter(value);

		if (value.trim().length() != 0) {
			for ( int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
		} else {
			for ( int i = 0; i < tree.getRowCount(); i++) {
				tree.collapseRow(i);
			}
		}
	}

	private boolean isFilterChanged(String value) {
		return !currentFilter.trim().equals(value.trim());
	}
}
