package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.UpdatePackagesModel;
import com.rapidminer.deployment.update.client.UpdatePanelTab;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.deployment.update.client.listmodels.SearchPackageListModel;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.tools.I18N;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior;

public class UpdatePanelSearchTab extends UpdatePanelTab {
    private static final long serialVersionUID = 1L;
    private JTextField searchField;
    private JButton searchButton;
    private SearchPackageListModel searchModel;
    public final Action searchAction;

    public UpdatePanelSearchTab(UpdatePackagesModel updateModel, PackageDescriptorCache packageDescriptorCache, UpdateServerAccount usAccount) {
        this(updateModel, new SearchPackageListModel(packageDescriptorCache), usAccount);
    }

    private UpdatePanelSearchTab(UpdatePackagesModel updateModel, SearchPackageListModel model, UpdateServerAccount usAccount) {
        super(updateModel, model, usAccount);
        this.searchAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;
            private String oldSearch = "";

            public void actionPerformed(ActionEvent e) {
                String value = UpdatePanelSearchTab.this.searchField.getText();
                if(value != null && !value.equals(this.oldSearch)) {
                    UpdatePanelSearchTab.this.searchModel.search(value);
                    this.oldSearch = value;
                    UpdatePanelSearchTab.this.getPackageList().clearSelection();
                }

            }
        };
        this.searchModel = model;
    }

    protected JComponent makeTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.TAB_BORDER), BorderFactory.createEmptyBorder(5, 2, 5, 2)));
        this.searchField = new JTextField(12);
        this.searchField.setToolTipText(I18N.getMessage(I18N.getGUIBundle(), "gui.field.update.search.tip", new Object[0]));
        PromptSupport.setForeground(Color.LIGHT_GRAY, this.searchField);
        PromptSupport.setPrompt(I18N.getMessage(I18N.getGUIBundle(), "gui.filter_text_field.label", new Object[0]), this.searchField);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.searchField);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.searchField);
        this.searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if(key == 10) {
                    UpdatePanelSearchTab.this.searchAction.actionPerformed((ActionEvent)null);
                    e.consume();
                }

            }
        });
        this.searchButton = new JButton(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update.tab.search.search_button", new Object[0]));
        this.searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UpdatePanelSearchTab.this.searchAction.actionPerformed((ActionEvent)null);
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = 1;
        gbc.weightx = 1.0D;
        gbc.weighty = 1.0D;
        panel.add(this.searchField, gbc);
        ++gbc.gridx;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.fill = 3;
        gbc.weightx = 0.0D;
        panel.add(this.searchButton, gbc);
        return panel;
    }
}
