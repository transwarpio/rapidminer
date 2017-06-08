package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.license.GUILicenseManagerListener;
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.license.onboarding.AbstractCard;
import com.rapidminer.gui.license.onboarding.ActivationSuccessfulCard;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.LinkRemoteButton;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseStatus;
import com.rapidminer.license.LicenseValidationException;
import com.rapidminer.license.UnknownProductException;
import com.rapidminer.license.location.LicenseStoringException;
import com.rapidminer.license.product.Product;
import com.rapidminer.license.utils.Pair;
import com.rapidminer.tools.I18N;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior;

public class EnterLicenseCard extends AbstractCard {
    public static final String CARD_ID = "enter_license";
    private static final String GET_A_LICENSE_URL = I18N.getGUILabel("onboarding.get_a_license.url", new Object[0]);
    private static final String NA = "-";
    private JTextArea textArea;
    private JLabel lbStatusResult;
    private JLabel lbRegisteredToResult;
    private JLabel lbProductResult;
    private JLabel lbEditionResult;
    private JLabel lbStartsResult;
    private JLabel lbExpiresResult;
    private JButton btSubmit;
    private License license;
    private Product product;
    private LicenseStatus licenseStatus;
    private boolean parseError = true;

    public EnterLicenseCard(OnboardingDialog owner) {
        super("enter_license", owner);
    }

    public JPanel getHeader() {
        return OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.enter_license", new Object[0]));
    }

    public JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 5, 5);
        gbc.weighty = 1.0D;
        gbc.weightx = 1.0D;
        gbc.gridwidth = 3;
        gbc.fill = 1;
        panel.add(this.createLicensePanel(), gbc);
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.weighty = 0.0D;
        panel.add(Box.createRigidArea(new Dimension(0, 40)), gbc);
        JButton btBack = new JButton(new ResourceAction("onboarding.back", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                String key = EnterLicenseCard.this.getContainer().getSharedObject("com.rapidminer.onboarding.back_button").toString();
                EnterLicenseCard.this.getContainer().showInitialConnectToServiceCard(key);
            }
        });
        btBack.setFocusable(false);
        btBack.putClientProperty("com.rapidminer.ui.button.type", "normal");
        this.btSubmit = new JButton(new ResourceAction("onboarding.submit_and_activate", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                EnterLicenseCard.this.installNewLicense(EnterLicenseCard.this.getLicenseText());
                if(EnterLicenseCard.this.licenseStatus == LicenseStatus.EXPIRED) {
                    EnterLicenseCard.this.getContainer().showActivationSuccessfulCard(EnterLicenseCard.this.lbRegisteredToResult.getText(), I18N.getGUILabel("onboarding.expired_edition", new Object[]{LicenseTools.translateProductEdition(EnterLicenseCard.this.license)}), I18N.getGUILabel("license.no_end_date", new Object[0]));
                } else {
                    String additionalInfo = null;
                    if(EnterLicenseCard.this.license.getPrecedence() == 30) {
                        additionalInfo = ActivationSuccessfulCard.getRemaingTrialDaysMessage(EnterLicenseCard.this.license);
                    }

                    EnterLicenseCard.this.getContainer().showActivationSuccessfulCard(EnterLicenseCard.this.lbRegisteredToResult.getText(), EnterLicenseCard.this.lbEditionResult.getText(), EnterLicenseCard.this.lbExpiresResult.getText(), false, additionalInfo);
                }

            }
        });
        this.btSubmit.setFocusable(false);
        this.btSubmit.putClientProperty("com.rapidminer.ui.button.type", "normal");
        ++gbc.gridy;
        gbc.weighty = 0.0D;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = 17;
        panel.add(OnboardingDialog.createButtonPanel(false, new JButton[]{btBack}), gbc);
        ++gbc.gridx;
        JPanel gapPanel = new JPanel();
        gapPanel.setOpaque(false);
        gbc.anchor = 10;
        gbc.fill = 2;
        panel.add(gapPanel, gbc);
        ++gbc.gridx;
        gbc.fill = 0;
        gbc.anchor = 13;
        panel.add(OnboardingDialog.createButtonPanel(new JButton[]{this.btSubmit}), gbc);
        return panel;
    }

    public void showCard() {
        this.textArea.setText(this.getLicenseKeyFromClipboard());
        this.validateLicenseString();
        this.btSubmit.requestFocusInWindow();
        this.getContainer().getRootPane().setDefaultButton(this.btSubmit);
    }

    public Runnable getCloseAction() {
        return this.standardAskAndCloseAction;
    }

    private JPanel createLicensePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        panel.setOpaque(false);
        JPanel scrollPane = this.createDetailPanel();
        panel.add(scrollPane);
        this.createTextArea();
        JScrollPane scrollPane1 = new JScrollPane(this.textArea);
        scrollPane1.setBorder((Border)null);
        panel.add(scrollPane1);
        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = 1;
        double firstColumnWeight = 0.0D;
        double secondColumnWeight = 1.0D;
        gbc.weightx = firstColumnWeight;
        panel.add(OnboardingDialog.createLabel("onboarding.status", "bold"), gbc);
        ++gbc.gridx;
        gbc.weightx = secondColumnWeight;
        this.lbStatusResult = new JLabel("-");
        this.lbStatusResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbStatusResult, gbc);
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.weightx = firstColumnWeight;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(OnboardingDialog.createLabel("onboarding.registered_to", "bold"), gbc);
        ++gbc.gridx;
        gbc.weightx = secondColumnWeight;
        this.lbRegisteredToResult = new JLabel("-");
        this.lbRegisteredToResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbRegisteredToResult, gbc);
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.weightx = firstColumnWeight;
        panel.add(OnboardingDialog.createLabel("onboarding.product", "bold"), gbc);
        ++gbc.gridx;
        gbc.weightx = secondColumnWeight;
        this.lbProductResult = new JLabel("-");
        this.lbProductResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbProductResult, gbc);
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.weightx = firstColumnWeight;
        panel.add(OnboardingDialog.createLabel("onboarding.edition", "bold"), gbc);
        ++gbc.gridx;
        gbc.weightx = secondColumnWeight;
        this.lbEditionResult = new JLabel("-");
        this.lbEditionResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbEditionResult, gbc);
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.weightx = firstColumnWeight;
        panel.add(OnboardingDialog.createLabel("onboarding.starts", "bold"), gbc);
        ++gbc.gridx;
        gbc.weightx = secondColumnWeight;
        this.lbStartsResult = new JLabel("-");
        this.lbStartsResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbStartsResult, gbc);
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.weightx = firstColumnWeight;
        panel.add(OnboardingDialog.createLabel("onboarding.expires", "bold"), gbc);
        ++gbc.gridx;
        gbc.weightx = secondColumnWeight;
        this.lbExpiresResult = new JLabel("-");
        this.lbExpiresResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbExpiresResult, gbc);
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0D;
        gbc.weightx = 0.0D;
        gbc.fill = 2;
        gbc.anchor = 16;
        LinkRemoteButton btGetLicense = new LinkRemoteButton(new ResourceAction(false, "how_to_get_a_license", new Object[0]) {
            private static final long serialVersionUID = 4660265922480909610L;

            public void actionPerformed(ActionEvent e) {
                OnboardingDialog.openURL(EnterLicenseCard.GET_A_LICENSE_URL);
            }
        });
        btGetLicense.putClientProperty("com.rapidmniner.ui.link_button.id", "how_to_get_a_license");
        panel.add(btGetLicense, gbc);
        return panel;
    }

    private void createTextArea() {
        this.textArea = new JTextArea();
        this.textArea.setBorder(BorderFactory.createEtchedBorder());
        this.textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void removeUpdate(DocumentEvent e) {
                EnterLicenseCard.this.validateLicenseString();
            }

            public void insertUpdate(DocumentEvent e) {
                EnterLicenseCard.this.validateLicenseString();
            }

            public void changedUpdate(DocumentEvent e) {
                EnterLicenseCard.this.validateLicenseString();
            }
        });
        this.textArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                EnterLicenseCard.this.textArea.selectAll();
            }
        });
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(true);
        PromptSupport.setForeground(Color.GRAY, this.textArea);
        PromptSupport.setPrompt(I18N.getGUILabel("license.paste_here", new Object[0]), this.textArea);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.textArea);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.textArea);
    }

    private void validateLicenseString() {
        String enteredLicenseKey = this.getLicenseText();
        if(enteredLicenseKey != null && !enteredLicenseKey.isEmpty() && enteredLicenseKey.length() > 3) {
            try {
                Pair temp = ProductConstraintManager.INSTANCE.validateLicense(enteredLicenseKey);
                if(((License)temp.getSecond()).getStatus() != LicenseStatus.VALID && ((License)temp.getSecond()).getStatus() != LicenseStatus.STARTS_IN_FUTURE && ((License)temp.getSecond()).getStatus() != LicenseStatus.EXPIRED) {
                    this.license = null;
                    this.parseError = true;
                } else {
                    this.license = (License)temp.getSecond();
                    this.product = (Product)temp.getFirst();
                    this.parseError = false;
                    if(!this.product.getProductId().equals("rapidminer-studio")) {
                        this.license = null;
                        this.parseError = true;
                    }
                }
            } catch (UnknownProductException | LicenseValidationException var4) {
                this.license = null;
                this.parseError = true;
            }
        } else {
            this.license = null;
            this.parseError = true;
        }

        this.setValuesForDetailsPanel();
    }

    private void setValuesForDetailsPanel() {
        if(this.license != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = this.license.getStartDate();
            if(startDate == null) {
                this.lbStartsResult.setText(I18N.getGUILabel("license.no_start_date", new Object[0]));
            } else {
                this.lbStartsResult.setText(df.format(startDate));
            }

            Date expirationDate = this.license.getExpirationDate();
            if(expirationDate == null) {
                this.lbExpiresResult.setText(I18N.getGUILabel("license.no_end_date", new Object[0]));
            } else {
                this.lbExpiresResult.setText(df.format(expirationDate));
            }

            if(this.product != null) {
                this.lbProductResult.setText(LicenseTools.translateProductName(this.license));
                this.lbEditionResult.setText(LicenseTools.translateProductEdition(this.license));
            }

            this.lbRegisteredToResult.setText(this.license.getLicenseUser().getName());
            this.licenseStatus = this.license.validate(new Date());
            switch(this.licenseStatus.ordinal()) {
                case 1:
                    this.lbStatusResult.setForeground(SwingTools.DARK_GREEN);
                    this.lbStatusResult.setText(I18N.getGUILabel("license.status_valid", new Object[0]));
                    this.btSubmit.setEnabled(true);
                    break;
                case 2:
                    this.lbStatusResult.setForeground(SwingTools.DARK_GREEN);
                    this.lbStatusResult.setText(I18N.getGUILabel("license.status_starts_in_future", new Object[0]));
                    this.btSubmit.setEnabled(true);
                    break;
                case 3:
                    this.lbStatusResult.setText(I18N.getGUILabel("license.status_product_version_invalid", new Object[0]));
                    this.lbStatusResult.setForeground(Color.RED);
                    this.btSubmit.setEnabled(false);
                    break;
                case 4:
                    this.lbStatusResult.setText(I18N.getGUILabel("license.status_expired", new Object[0]));
                    this.lbStatusResult.setForeground(Color.RED);
                    this.btSubmit.setEnabled(false);
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    this.lbStatusResult.setForeground(Color.RED);
                    this.lbStatusResult.setText(this.licenseStatus.toString());
                    this.btSubmit.setEnabled(false);
            }
        } else {
            this.btSubmit.setEnabled(!this.parseError);
            this.lbStartsResult.setText("-");
            this.lbExpiresResult.setText("-");
            this.lbEditionResult.setText("-");
            this.lbRegisteredToResult.setText("-");
            this.lbProductResult.setText("-");
            if(this.parseError) {
                this.lbStatusResult.setText(I18N.getGUILabel("license.parse_error", new Object[0]));
            } else {
                this.lbStatusResult.setText(I18N.getGUILabel("license.status_invalid", new Object[0]));
            }

            this.lbStatusResult.setForeground(Color.RED);
        }

    }

    private String getLicenseKeyFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        try {
            String licenseFromClipboard = (String)clipboard.getData(DataFlavor.stringFlavor);
            if(ProductConstraintManager.INSTANCE.isLicenseValid(licenseFromClipboard)) {
                return licenseFromClipboard;
            }
        } catch (Exception var3) {
            ;
        }

        return null;
    }

    private String getLicenseText() {
        return this.textArea != null?this.textArea.getText():null;
    }

    private void installNewLicense(String licenseString) {
        if(licenseString != null && !licenseString.isEmpty()) {
            try {
                GUILicenseManagerListener.INSTANCE.disableLicenseStoredNotification();
                ProductConstraintManager.INSTANCE.installNewLicense(licenseString);
                GUILicenseManagerListener.INSTANCE.enableLicenseStoredNotification();
            } catch (UnknownProductException | LicenseValidationException | LicenseStoringException var3) {
                SwingTools.showSimpleErrorMessage("license.storing_failed", var3, new Object[0]);
            }

        }
    }
}
