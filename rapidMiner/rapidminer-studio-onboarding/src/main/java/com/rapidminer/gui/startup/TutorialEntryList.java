package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.tools.Ionicon;
import com.rapidminer.gui.tools.ListHoverHelper;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tutorial.TutorialGroup;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

class TutorialEntryList extends JList<TutorialGroup> {
    private static final long serialVersionUID = 1L;
    private static final String TUTORIAL_NAME_TEMPLATE = "<html><span style=\"font-family: \'Open Sans\';font-size: 13; color: #424242;\">%s</html>";
    private static final String TUTORIAL_NAME_SELECTED_TEMPLATE = "<html><span style=\"font-family: \'Open Sans Bold\';font-size: 13; color: #424242;\">%s</html>";
    private static final String TUTORIAL_PROGRESS_TEMPLATE;
    private static final String HEX_COLOR_COMPLETED = "#00BB58";
    private static final String HEX_COLOR_PENDING = "#797979";

    TutorialEntryList(List<TutorialGroup> tutorialGroups) {
        super(tutorialGroups.toArray(new TutorialGroup[tutorialGroups.size()]));
        this.setupGUI();
    }

    private void setupGUI() {
        this.setLayoutOrientation(0);
        this.setFixedCellHeight(50);
        this.setVisibleRowCount(-1);
        ListHoverHelper.install(this);
        this.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                boolean hover = index == ListHoverHelper.index(list);
                TutorialGroup tutorialGroup = (TutorialGroup)value;
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
                panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.TAB_BORDER));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weighty = 1.0D;
                gbc.weightx = 1.0D;
                gbc.fill = 1;
                gbc.anchor = 17;
                gbc.insets = new Insets(5, 5, 5, 5);
                String format;
                if(isSelected) {
                    format = "<html><span style=\"font-family: \'Open Sans Bold\';font-size: 13; color: #424242;\">%s</html>";
                } else {
                    format = "<html><span style=\"font-family: \'Open Sans\';font-size: 13; color: #424242;\">%s</html>";
                }

                panel.add(new JLabel(String.format(format, new Object[]{tutorialGroup.getTitle()})), gbc);
                gbc.weightx = 0.0D;
                ++gbc.gridx;
                gbc.anchor = 13;
                panel.add(new JLabel(String.format(TutorialEntryList.TUTORIAL_PROGRESS_TEMPLATE, new Object[]{tutorialGroup.hasCompleted()?"#00BB58":"#797979", String.valueOf(tutorialGroup.getNumberOfCompletedTutorials()), String.valueOf(tutorialGroup.getNumberOfTutorials())})), gbc);
                if(hover) {
                    panel.setBackground(SwingTools.brightenColor(GettingStartedDialog.ITEM_HIGHLIGHT));
                }

                if(isSelected) {
                    panel.setBackground(GettingStartedDialog.ITEM_SELECTED_HIGHLIGHT);
                }

                return panel;
            }
        });
        this.setSelectionMode(0);
    }

    static {
        TUTORIAL_PROGRESS_TEMPLATE = "<html><span style=\"font-family: \'Open Sans\';font-size: 13; color: %s;\">" + Ionicon.CHECKMARK_ROUND.getHtml() + "%s/%s</html>";
    }
}
