package com.rapidminer.gui.license;

/**
 * Created by mk on 3/9/16.
 */

import com.rapidminer.Process;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.UpgradeLicenseAction;
import com.rapidminer.gui.processeditor.ExtendedProcessEditor;
import com.rapidminer.gui.tools.NotificationPopup;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.operator.Operator;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.usagestats.ActionStatisticsCollector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ConstraintValidationProcessEditor implements ExtendedProcessEditor {
    private static final Icon NOTIFICATION_ICON = SwingTools.createIcon("48/" + I18N.getMessage(I18N.getGUIBundle(), "gui.notifaction.unsupported_operator.icon", new Object[0]));
    private final List<Operator> alreadyCheckedOperators = Collections.synchronizedList(new LinkedList());

    public ConstraintValidationProcessEditor() {
    }

    public void processChanged(com.rapidminer.Process process) {
        this.alreadyCheckedOperators.clear();
        this.checkConstraint();
    }

    public void setSelection(List<Operator> selection) {
    }

    public void processUpdated(Process process) {
        this.checkConstraint();
    }

    private void checkConstraint() {
        Runnable constraintCheckRunnable = new Runnable() {
            public void run() {
                Process currentProcess = RapidMinerGUI.getMainFrame().getProcess();
                Iterator var2 = currentProcess.getRootOperator().getAllInnerOperators().iterator();

                while(var2.hasNext()) {
                    final Operator op = (Operator)var2.next();
                    if(op.isEnabled() && !ConstraintValidationProcessEditor.this.alreadyCheckedOperators.contains(op)) {
                        List checkConstraintCauses = ProductConstraintManager.INSTANCE.checkAnnotationViolations(op, false);
                        if(!checkConstraintCauses.isEmpty()) {
                            ActionStatisticsCollector.getInstance().log("constraint", "data_sources", ProductConstraintManager.INSTANCE.getActiveLicense().getProductEdition());
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    ConstraintValidationProcessEditor.this.showNotification(op);
                                }
                            });
                        }

                        ConstraintValidationProcessEditor.this.alreadyCheckedOperators.add(op);
                    }
                }

            }
        };
        (new Thread(constraintCheckRunnable)).start();
    }

    private void showNotification(final Operator op) {
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel notifactionLabel = new JLabel(I18N.getMessage(I18N.getGUIBundle(), "gui.notifaction.unsupported_operator.label", new Object[]{op.getName()}));
        notifactionLabel.setIcon(NOTIFICATION_ICON);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 0, 10);
        notificationPanel.add(notifactionLabel, gbc);
        LinkLocalButton linkButton = new LinkLocalButton(new ResourceAction("show_offending_operator", new Object[]{op.getName()}) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                MainFrame mainFrame = RapidMinerGUI.getMainFrame();
                mainFrame.selectOperator(mainFrame.getProcess().getOperator(op.getName()));
            }
        });
        ++gbc.gridy;
        gbc.insets = new Insets(0, NOTIFICATION_ICON.getIconWidth() + 14, 0, 10);
        gbc.anchor = 17;
        notificationPanel.add(linkButton, gbc);
        JButton upgradeButton = new JButton(new UpgradeLicenseAction());
        upgradeButton.setFont(upgradeButton.getFont().deriveFont(1));
        ++gbc.gridy;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = 2;
        gbc.weightx = 1.0D;
        notificationPanel.add(upgradeButton, gbc);
        NotificationPopup.showFadingPopup(notificationPanel, RapidMinerGUI.getMainFrame().getProcessPanel().getProcessRenderer(), NotificationPopup.PopupLocation.LOWER_RIGHT, 20000, 45, 30, BorderFactory.createLineBorder(Color.GRAY, 1, false));
    }

    public void processViewChanged(Process process) {
    }
}

