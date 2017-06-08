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
package com.rapidminer.gui.processeditor.results;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.rapidminer.gui.renderer.DefaultTextRenderer;
import com.rapidminer.gui.renderer.Renderer;
import com.rapidminer.gui.renderer.RendererService;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.ButtonBarCardPanel;
import com.rapidminer.gui.tools.components.CardSelectionEvent;
import com.rapidminer.gui.tools.components.CardSelectionListener;
import com.rapidminer.gui.tools.components.ResourceCard;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.ResultObject;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.usagestats.ActionStatisticsCollector;


/**
 * Static methods to generate result visualization components etc.
 *
 * @author Simon Fischer
 * */
public class ResultDisplayTools {

	public static final String IOOBJECT_USER_DATA_KEY_RENDERER = ResultDisplayTools.class.getName() + ".renderer";

	static final String CLIENT_PROPERTY_RAPIDMINER_RESULT_NAME_HTML = "rapidminer.result.name.html";
	static final String CLIENT_PROPERTY_RAPIDMINER_RESULT_ICON = "rapidminer.result.icon";
	static final String CLIENT_PROPERTY_RAPIDMINER_RESULT_NAME = "rapidminer.result.name";

	private static final String DEFAULT_RESULT_ICON_NAME = "presentation_chart.png";

	/** the icon shown for "under construction" tabs */
	private static final ImageIcon WAIT_ICON = SwingTools.createIcon("24/hourglass.png");
	private static final ImageIcon ERROR_ICON = SwingTools.createIcon("24/error.png");

	private static Icon defaultResultIcon = null;

	/**
	 * In these cases the unnecessary additional panel is suppressed
	 */
	private static final Set<String> NO_CARD_KEYS = new HashSet<>(Arrays.asList(new String[] { "collection", "metamodel",
			"delegation_model" }));

	static {
		defaultResultIcon = SwingTools.createIcon("16/" + DEFAULT_RESULT_ICON_NAME);
	}

	public static JPanel createVisualizationComponent(IOObject resultObject, IOContainer resultContainer,
			String usedResultName) {
		return createVisualizationComponent(resultObject, resultContainer, usedResultName, true);
	}

	/**
	 * Creates a panel which centers an error message.
	 *
	 * @param error
	 *            the message to display
	 * @return
	 */
	public static JPanel createErrorComponent(String error) {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(error, ERROR_ICON, SwingConstants.CENTER);
		panel.add(label, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * @param showCards
	 *            if <code>false</code> the cards on the left side of the visualization component
	 *            will not be shown
	 */
	public static JPanel createVisualizationComponent(final IOObject resultObject, final IOContainer resultContainer,
			String usedResultName, final boolean showCards) {
		final String resultName = RendererService.getName(resultObject.getClass());
		ButtonBarCardPanel visualisationComponent;
		Collection<Renderer> renderers = RendererService.getRenderers(resultName);
		renderers = RendererService.blackListFilter(resultName, renderers);
		// fallback to default toString method!
		if (resultName == null) {
			renderers.add(new DefaultTextRenderer());
		}

		// constructing panel of renderers
		visualisationComponent = new ButtonBarCardPanel(NO_CARD_KEYS, showCards);
		final ButtonBarCardPanel cardPanel = visualisationComponent;
		for (final Renderer renderer : renderers) {
			String cardKey = toCardName(renderer.getName());
			final ResourceCard card = new ResourceCard(cardKey, "result_view." + cardKey);

			// create a placeholder panel which shows "under construction" so the results can be
			// displayed immediately
			final JPanel inConstructionPanel = new JPanel(new BorderLayout());
			String humanName = I18N.getGUIMessageOrNull("gui.cards.result_view." + cardKey + ".title");
			if (humanName == null) {
				humanName = cardKey;
			}
			JLabel waitLabel = new JLabel(I18N.getGUILabel("result_construction", humanName));
			waitLabel.setIcon(WAIT_ICON);
			waitLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
			waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
			inConstructionPanel.add(waitLabel, BorderLayout.CENTER);
			cardPanel.addCard(card, inConstructionPanel);
			try {
				ProgressThread resultThread = new ProgressThread("creating_result_tab", false, humanName) {

					@Override
					public void run() {
						getProgressListener().setTotal(100);
						getProgressListener().setCompleted(1);
						final Component rendererComponent = renderer
								.getVisualizationComponent(resultObject, resultContainer);
						getProgressListener().setCompleted(60);

						if (rendererComponent != null) {

							if (rendererComponent instanceof JComponent) {
								((JComponent) rendererComponent).setBorder(null);
							}
							getProgressListener().setCompleted(80);

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									// update container
									// renderer is finished, remove placeholder
									inConstructionPanel.removeAll();

									// add real renderer
									inConstructionPanel.add(rendererComponent, BorderLayout.CENTER);

									inConstructionPanel.revalidate();
									inConstructionPanel.repaint();
								}

							});
							getProgressListener().complete();
						}
					}
				};

				// start result calculation progress thread
				resultThread.start();
			} catch (Exception e) {
				LogService
						.getRoot()
						.log(Level.WARNING,
								I18N.getMessage(
										LogService.getRoot().getResourceBundle(),
										"com.rapidminer.gui.processeditor.results.ResultDisplayTools.error_creating_renderer",
										e), e);
				String errorMsg = I18N.getMessage(I18N.getErrorBundle(), "result_display.error_creating_renderer",
						renderer.getName());
				visualisationComponent.addCard(card, new JLabel(errorMsg));
			}
		}
		if (resultObject.getUserData(IOOBJECT_USER_DATA_KEY_RENDERER) != null) { // check for user
			// specified
			// settings
			visualisationComponent
					.selectCard(toCardName((String) resultObject.getUserData(IOOBJECT_USER_DATA_KEY_RENDERER)));
		}
		// report statistics
		visualisationComponent.addCardSelectionListener(new CardSelectionListener() {

			private String lastKey = "";

			@Override
			public void cardSelected(CardSelectionEvent e) {
				if (e != null) {
					String key = e.getCardKey();
					if (key != null && !key.equals(lastKey)) {
						ActionStatisticsCollector.getInstance().log(ActionStatisticsCollector.TYPE_RENDERER, key, "select");
						this.lastKey = key;
					}
				}
			}
		});
		// result panel
		final JPanel resultPanel = new JPanel(new BorderLayout());
		resultPanel.putClientProperty("main.component", visualisationComponent);
		resultPanel.add(visualisationComponent, BorderLayout.CENTER);

		if (resultObject instanceof ResultObject) {
			if (((ResultObject) resultObject).getResultIcon() != null) {
				resultPanel.putClientProperty(ResultDisplayTools.CLIENT_PROPERTY_RAPIDMINER_RESULT_ICON,
						((ResultObject) resultObject).getResultIcon());
			} else {
				resultPanel.putClientProperty(ResultDisplayTools.CLIENT_PROPERTY_RAPIDMINER_RESULT_ICON, defaultResultIcon);
			}
		}
		resultPanel.putClientProperty(ResultDisplayTools.CLIENT_PROPERTY_RAPIDMINER_RESULT_NAME, usedResultName);
		String source = resultObject.getSource() != null ? resultObject.getSource() : "";
		resultPanel.putClientProperty(ResultDisplayTools.CLIENT_PROPERTY_RAPIDMINER_RESULT_NAME_HTML, "<html>"
				+ usedResultName + "<br/><small>" + source + "</small></html>");
		return resultPanel;
	}

	private static String toCardName(String name) {
		return name.toLowerCase().replace(' ', '_');
	}

	public static ResultDisplay makeResultDisplay() {
		return new DockableResultDisplay();
	}

}
