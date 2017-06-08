package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.I18N;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CardHeaderPanel extends JPanel {
    private static final long serialVersionUID = 5708568467108882727L;
    private static final Font OPEN_SANS_SEMIBOLD_18 = new Font("Open Sans Light", 0, 26);

    public CardHeaderPanel(String i18nKey) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        JLabel label = new JLabel(I18N.getGUILabel(i18nKey, new Object[0])) {
            private static final long serialVersionUID = 1L;

            public void paintComponent(Graphics g) {
                SwingTools.disableClearType(this);
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        label.setFont(OPEN_SANS_SEMIBOLD_18);
        label.setHorizontalTextPosition(2);
        this.add(label, gbc);
        ++gbc.gridx;
        gbc.fill = 2;
        gbc.weightx = 1.0D;
        this.add(new JLabel(), gbc);
        this.setOpaque(false);
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20), BorderFactory.createMatteBorder(0, 0, 1, 0, GettingStartedDialog.VERY_LIGHT_GRAY)));
    }
}
