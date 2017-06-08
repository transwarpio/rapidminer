package com.rapidminer.core.user;

import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.config.ConfigurationManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by fan on 17-3-11.
 */
public class ConfigurablePasswordDialog extends ButtonDialog {
    /**
     * text field with the user name (which is already set to admin user)
     */
    private JTextField userField = new JTextField(20);

    /**
     * label describing userField
     */
    private JLabel userLabel = new ResourceLabel("configurable_dialog.password_dialog.user");

    /**
     * field containing the password
     */
    private JPasswordField passwordField = new JPasswordField(20);

    /**
     * label describing passwordField
     */
    private JLabel passwordLabel = new ResourceLabel("configurable_dialog.password_dialog.password");

    /** color of {@link #checkLabel} */
    private static final Color FAILURE_STATUS_COLOR = Color.RED;

    /** label indicating that the connection could not be established */
    private JLabel checkLabel = new JLabel();

    public ConfigurablePasswordDialog(Window owner) {
        super(owner, "configurable_dialog.password_dialog", ModalityType.MODELESS, "");
        JButton okButton = makeOkButton("configurable_dialog.password_dialog.ok");
        JButton cancelButton = makeCancelButton("configurable_dialog.password_dialog.cancel");
        setModal(true);

        JPanel mainPanel = makeMainPanel();

        layoutDefault(mainPanel, MESSAGE_BIT_EXTENDED, okButton, cancelButton);
        userField.requestFocusInWindow();
    }

    /**
     * @return the password of the admin (given by user)
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /**
     * @return the user name of the admin (not given by user)
     */
    public String getUserName() {
        return userField.getText();
    }

    /**
     * checks whether the given url, user name and password can create a connection to the server
     * and displays an error, if one occurs, otherwise the window is closed
     */
    private void checkConnection() {
        wasConfirmed = false;
        if (passwordField.getPassword().length == 0) {
            checkLabel.setText(I18N.getGUILabel("error.configurable_dialog.password_dialog.password.label"));
            return;
        }

        ProgressThread pt = new ProgressThread("check_configuration") {

            @Override
            public void run() {
                String tmp_e;
                try {
                    tmp_e = RemoteAuthentication.checkConfiguration(getUserName(), new String(getPassword()));
                } catch (Exception e) {
                    tmp_e = I18N.getGUILabel("error.configurable_dialog.midas_remote_repo_factory_not_available.label");
                }
                final String error = tmp_e;

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (error != null) {
                            checkLabel.setText(error);
                        } else {
                            wasConfirmed = true;
                            showLogout();
                            dispose();
                        }
                    }

                    private void showLogout() {
                        if (wasConfirmed()) {
                            ButtonManager.setLoginVisible(false);
                            ButtonManager.setLogoutVisible(true);
                        }
                    }
                });
            }
        };
        pt.start();
    }

    @Override
    protected void ok() {
        checkConnection();
    }

    private JPanel makeMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.weightx = .5;
        c.gridy = 0;
        c.insets = new Insets(4, 4, 4, 4);

        // User name
        c.gridx = 0;
        c.gridy += 1;
        mainPanel.add(userLabel, c);
        c.gridx += 1;
        userField.setMinimumSize(userField.getPreferredSize());
        mainPanel.add(userField, c);
        c.gridx += 1;
        mainPanel.add(Box.createHorizontalGlue(), c);

        // Password
        c.gridx = 0;
        c.gridy += 1;
        mainPanel.add(passwordLabel, c);
        c.gridx += 1;
        passwordField.setMinimumSize(passwordField.getPreferredSize());
        mainPanel.add(passwordField, c);
        c.gridx += 1;
        mainPanel.add(Box.createHorizontalGlue(), c);

        // check label
        c.gridx = 0;
        c.gridy += 1;
        c.gridwidth = 4;
        c.weighty = 1;
        checkLabel.setForeground(FAILURE_STATUS_COLOR);
        mainPanel.add(checkLabel, c);

        return mainPanel;
    }
}
