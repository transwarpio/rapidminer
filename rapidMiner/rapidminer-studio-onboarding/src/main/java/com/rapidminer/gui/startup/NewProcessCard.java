package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.startup.CardHeaderPanel;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.startup.NewProcessEntryList;
import com.rapidminer.template.TemplateManager;
import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

class NewProcessCard extends JPanel {
    private static final long serialVersionUID = 1L;
    private NewProcessEntryList processEntryList;

    NewProcessCard(Window owner) {
        this.setLayout(new BorderLayout());
        this.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        CardHeaderPanel header = new CardHeaderPanel("getting_started.header.templates");
        this.add(header, "North");
        this.processEntryList = new NewProcessEntryList(TemplateManager.INSTANCE.getAllTemplates(), owner);
        JScrollPane centerPanel = new JScrollPane(this.processEntryList, 20, 31);
        centerPanel.setBorder(null);
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        this.add(centerPanel, "Center");
    }

    public boolean requestFocusInWindow() {
        if(this.processEntryList.getModel().getSize() > 0) {
            this.processEntryList.setSelectedIndex(0);
        }

        return this.processEntryList.requestFocusInWindow();
    }
}