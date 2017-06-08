package com.rapidminer.gui.license;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.EnterLicenseAction;
import com.rapidminer.gui.license.LicenseContentPanel;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.license.onboarding.OnboardingManager.WelcomeType;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseEvent;
import com.rapidminer.license.LicenseManagerListener;
import com.rapidminer.license.LicenseManagerRegistry;
import com.rapidminer.license.LicenseEvent.LicenseEventType;
import com.rapidminer.tools.RMUrlHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

public class LicenseDialog extends ButtonDialog {
    private static final long serialVersionUID = -2430267437326878776L;
    private static final int BUTTON_GAP = 6;
    private static final Logger LOGGER = Logger.getLogger(LicenseDialog.class.getCanonicalName());
    private LicenseContentPanel contentPanel;
    protected transient List<License> activeLicenses;
    protected transient License upcominglicense;
    protected Properties properties;
    protected transient Image licenseTypeLogo;
    private final transient LicenseManagerListener licenseManagerListener = new LicenseManagerListener() {
        public <S, C> void handleLicenseEvent(LicenseEvent<S, C> e) {
            if(e.getType() == LicenseEventType.ACTIVE_LICENSE_CHANGED || e.getType() == LicenseEventType.LICENSE_STORED) {
                LicenseDialog.this.redrawLicenseContentPanel();
            }

        }
    };
    private final transient WindowListener windowListener = new WindowAdapter() {
        public void windowClosed(WindowEvent e) {
            ProductConstraintManager.INSTANCE.removeLicenseManagerListener(LicenseDialog.this.licenseManagerListener);
        }
    };

    public LicenseDialog(Object... arguments) {
        super(ApplicationFrame.getApplicationFrame(), "license_dialog", ModalityType.APPLICATION_MODAL, arguments);
        this.addWindowListener(this.windowListener);
        ProductConstraintManager.INSTANCE.registerLicenseManagerListener(this.licenseManagerListener);
        this.activeLicenses = LicenseManagerRegistry.INSTANCE.get().getAllActiveLicenses();
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.contentPanel = new LicenseContentPanel(this.activeLicenses);
        this.contentPanel.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if(event.getEventType() == EventType.ACTIVATED && event.getURL() != null) {
                    try {
                        RMUrlHandler.browse(event.getURL().toURI());
                    } catch (IOException | URISyntaxException var3) {
                        LicenseDialog.LOGGER.log(Level.SEVERE, "Failed to parse URL for My Account page.", var3);
                    }
                }

            }
        });
        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(this.contentPanel.getPreferredSize());
        scrollPane.getViewport().add(this.contentPanel);
        this.add(scrollPane, "Center");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        });
        JPanel buttonPanel = this.makeButtonPanel();
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
        this.add(buttonPanel, "South");
        this.pack();
        this.setLocationRelativeTo(RapidMinerGUI.getMainFrame());
    }

    private JPanel makeButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(2, 6, 6));
        buttonPanel.add(makeConnectToLicenseServiceLinkButton());
        EnterLicenseAction importAction = new EnterLicenseAction(this);
        JButton importButton = new JButton(importAction);
        buttonPanel.add(importButton);
        JButton closeButton = this.makeCancelButton("close");
        buttonPanel.add(closeButton);
        this.getRootPane().setDefaultButton(closeButton);
        this.getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0, false), "CANCEL");
        return buttonPanel;
    }

    private void redrawLicenseContentPanel() {
        List newList = LicenseManagerRegistry.INSTANCE.get().getAllActiveLicenses();
        if(this.activeLicenses == null || !this.activeLicenses.equals(newList)) {
            this.contentPanel.setLicenses(newList);
            this.contentPanel.updateContent();
        }

    }

    public static JButton makeConnectToLicenseServiceLinkButton() {
        return new JButton(new ResourceAction("license.login_and_download", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                OnboardingDialog dialog = new OnboardingDialog(WelcomeType.WELCOME_REMINDER);
                dialog.showConnectToServiceCard();
                dialog.setVisible(true);
            }
        });
    }
}
