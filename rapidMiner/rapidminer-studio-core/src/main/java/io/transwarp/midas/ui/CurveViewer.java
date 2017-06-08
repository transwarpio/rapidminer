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
package io.transwarp.midas.ui;

import com.rapidminer.gui.look.Colors;
import io.transwarp.midas.model.CurveCollection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.rapidminer.gui.actions.export.PrintableComponent;
import com.rapidminer.report.Renderable;
import com.rapidminer.tools.I18N;


/**
 * Created by tianming on 5/17/16.
 * since ROCViewer does not fit our need, we create a new curve viewer
 */
public class CurveViewer extends JPanel implements Renderable, PrintableComponent {
    private CurveChartPlotter plotter;

    private String criterionName;

    public CurveViewer(CurveCollection curves) {
       setLayout(new BorderLayout());

        String message = curves.toString();

        criterionName = curves.getName();

        // info string
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(true);
        infoPanel.setBackground(Colors.WHITE);
        JTextPane infoText = new JTextPane();
        infoText.setEditable(false);
        infoText.setBackground(infoPanel.getBackground());
        infoText.setFont(infoText.getFont().deriveFont(Font.BOLD));
        infoText.setText(message);
        infoPanel.add(infoText);
        add(infoPanel, BorderLayout.NORTH);

        // plot panel
        plotter = new CurveChartPlotter();
        plotter.setCurve(curves);

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(plotter, BorderLayout.CENTER);
        innerPanel.setBorder(BorderFactory.createMatteBorder(5, 0, 10, 10, Colors.WHITE));
        add(innerPanel, BorderLayout.CENTER);
    }

    @Override
    public void prepareRendering() {
        plotter.prepareRendering();
    }

    @Override
    public void finishRendering() {
        plotter.finishRendering();
    }

    @Override
    public int getRenderHeight(int preferredHeight) {
        return plotter.getRenderHeight(preferredHeight);
    }

    @Override
    public int getRenderWidth(int preferredWidth) {
        return plotter.getRenderWidth(preferredWidth);
    }

    @Override
    public void render(Graphics graphics, int width, int height) {
        plotter.render(graphics, width, height);
    }

    @Override
    public Component getExportComponent() {
        return plotter;
    }

    @Override
    public String getExportName() {
        return I18N.getGUIMessage("gui.cards.result_view.roc_curve.title");
    }

    @Override
    public String getIdentifier() {
        return criterionName;
    }

    @Override
    public String getExportIconName() {
        return I18N.getGUIMessage("gui.cards.result_view.roc_curve.icon");
    }
}

