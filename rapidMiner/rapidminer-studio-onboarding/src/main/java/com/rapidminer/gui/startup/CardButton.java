package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.tools.Ionicon;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.I18N;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class CardButton extends JToggleButton {
    private static final long serialVersionUID = 1L;
    private static final int BORDER_RADIUS = 10;
    private static final String TEMPLATE_LABEL = "<html><span style=\"font-family: \'Open Sans Semibold\'; font-size: %d; color: %s;\">%s %s</span>";
    private static final String TEMPLATE_INVERTED_LABEL = "<html><span style=\"font-family: \'Open Sans Semibold\'; font-size: %d; color: %s;\">%s %s</span>";
    private static final String TEMPLATE_MNE = "<u>%s</u>";
    private String label;
    private String invertedLabel;
    private final Color colorHover;
    private final Color colorBorder;

    public CardButton(Ionicon icon, String i18nKey) {
        this(icon, i18nKey, Color.WHITE, GettingStartedDialog.MENU_BACKGROUND, 19, true);
    }

    public CardButton(Ionicon icon, String i18nKey, Color colorLabel, Color colorBackground, int size, boolean invert) {
        this.setFocusable(false);
        String text = this.getMessage(i18nKey + ".label");
        String mne = this.getMessageOrNull(i18nKey + ".mne");
        if(mne != null) {
            int indexLowerCase = text.indexOf(mne.toLowerCase(Locale.ENGLISH).charAt(0));
            int indexUpperCase = text.indexOf(mne.toUpperCase(Locale.ENGLISH).charAt(0));
            if(indexLowerCase == -1) {
                indexLowerCase = 2147483647;
            }

            if(indexUpperCase == -1) {
                indexUpperCase = 2147483647;
            }

            text = text.replaceFirst("(?i)" + Pattern.quote(mne), String.format("<u>%s</u>", new Object[]{indexLowerCase < indexUpperCase?mne.toLowerCase(Locale.ENGLISH):mne.toUpperCase(Locale.ENGLISH)}));
        }

        this.label = String.format("<html><span style=\"font-family: \'Open Sans Semibold\'; font-size: %d; color: %s;\">%s %s</span>", new Object[]{Integer.valueOf(size), this.getHexColor(colorLabel), icon.getHtml(), text});
        if(invert) {
            this.invertedLabel = String.format("<html><span style=\"font-family: \'Open Sans Semibold\'; font-size: %d; color: %s;\">%s %s</span>", new Object[]{Integer.valueOf(size), this.getHexColor(colorBackground), icon.getHtml(), text});
            this.colorHover = colorLabel;
        } else {
            this.invertedLabel = this.label;
            this.colorHover = colorBackground;
        }

        this.colorBorder = colorLabel;
        this.setText(this.label);
        if(mne != null) {
            this.setMnemonic(KeyStroke.getKeyStroke(mne.charAt(0), 0).getKeyCode());
            this.setDisplayedMnemonicIndex(0);
        }

        this.setContentAreaFilled(false);
        this.setHorizontalAlignment(2);
        this.setBorder(new EmptyBorder(8, 16, 8, 16));
        this.getModel().addChangeListener(new ChangeListener() {
            private boolean currentlyInverted = false;
            private ButtonModel model = CardButton.this.getModel();

            public void stateChanged(ChangeEvent e) {
                boolean hover = this.model.isEnabled() && (this.model.isRollover() || this.model.isSelected());
                if(this.currentlyInverted != hover) {
                    if(hover) {
                        CardButton.this.setText(CardButton.this.invertedLabel);
                    } else {
                        CardButton.this.setText(CardButton.this.label);
                    }

                    this.currentlyInverted = hover;
                }

            }
        });
    }

    protected void paintComponent(Graphics graphics) {
        SwingTools.disableClearType(this);
        ((Graphics2D)graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g = (Graphics2D)graphics.create();
        ButtonModel model = this.getModel();
        if(model.isEnabled() && (model.isRollover() || model.isSelected())) {
            g.setColor(this.colorHover);
            g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
        }

        g.dispose();
        super.paintComponent(graphics);
    }

    protected void paintBorder(Graphics graphics) {
        Graphics2D g = (Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(this.colorBorder);
        g.setStroke(new BasicStroke(2.0F));
        g.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 10, 10);
    }

    private String getHexColor(Color color) {
        return String.format("#%06x", new Object[]{Integer.valueOf(16777215 & color.getRGB())});
    }

    private String getMessage(String key) {
        return I18N.getMessage(I18N.getGUIBundle(), "gui.label." + key, new Object[0]);
    }

    private String getMessageOrNull(String key) {
        return I18N.getMessageOrNull(I18N.getGUIBundle(), "gui.label." + key, new Object[0]);
    }
}
