package com.rapidminer.gui.license;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseStatus;
import com.rapidminer.license.LicenseValidationException;
import com.rapidminer.license.UnknownProductException;
import com.rapidminer.license.product.Product;
import com.rapidminer.license.utils.Pair;
import com.rapidminer.tools.I18N;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior;

public class LicenseEnteringDialog extends ButtonDialog {
    private static final long serialVersionUID = -5825873580778775409L;
    private JTextArea textArea;
    private JScrollPane textPane;
    private JPanel mainPanel;
    private JPanel detailsPanel;
    private JLabel statusLabel;
    private JLabel startDateLabel;
    private JLabel expirationDateLabel;
    private JLabel registeredToLabel;
    private JLabel productLabel;
    private JLabel editionLabel;
    private License license;
    private Product product;
    private LicenseStatus licenseStatus;
    private boolean parseError;
    private JButton installButton;
    private String productName;
    private static final String NA = "-";
    private static final int BUTTON_GAP = 6;

    /** @deprecated */
    @Deprecated
    public LicenseEnteringDialog(Object... arguments) {
        this(ApplicationFrame.getApplicationFrame(), arguments);
    }

    public LicenseEnteringDialog(Window owner, Object... arguments) {
        this(owner, "input.license_string", arguments);
    }

    /** @deprecated */
    @Deprecated
    public LicenseEnteringDialog(String key, Object... arguments) {
        this(ApplicationFrame.getApplicationFrame(), key, arguments);
    }

    public LicenseEnteringDialog(Window owner, String key, Object... arguments) {
        super(owner, key, ModalityType.APPLICATION_MODAL, arguments);
        this.textArea = new JTextArea();
        this.statusLabel = new JLabel();
        this.startDateLabel = new JLabel();
        this.expirationDateLabel = new JLabel();
        this.registeredToLabel = new JLabel();
        this.productLabel = new JLabel();
        this.editionLabel = new JLabel();
        this.parseError = true;
        if(arguments.length > 0) {
            this.productName = String.valueOf(arguments[0]);
        }

        this.mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.detailsPanel = this.makeDetailsPanel();
        this.mainPanel.add(this.detailsPanel, c);
        this.textPane = this.makeTextPane("");
        c.gridy = 1;
        c.insets = new Insets(10, 0, 0, 0);
        this.mainPanel.add(this.textPane, c);
        this.installButton = this.makeInstallButton();
        PromptSupport.setForeground(Color.GRAY, this.textArea);
        PromptSupport.setPrompt(I18N.getGUILabel("license.paste_here", new Object[0]), this.textArea);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.textArea);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.textArea);
        this.textArea.setText(this.getLicenseKeyFromClipboard());
        this.setValuesForDetailsPanel();
        this.layoutDefault(this.mainPanel, this.makeButtonPanel());
        this.setResizable(false);
    }

    protected String getProductName() {
        return this.productName;
    }

    protected JButton makeInstallButton() {
        JButton button = new JButton(new ResourceAction("install_license", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                LicenseEnteringDialog.this.wasConfirmed = true;
                LicenseEnteringDialog.this.ok();
            }
        });
        this.getRootPane().setDefaultButton(button);
        button.setEnabled(false);
        return button;
    }

    protected JButton getInstallButton() {
        return this.installButton;
    }

    protected JPanel makeButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(2, 6, 6));
        buttonPanel.add(this.installButton);
        buttonPanel.add(this.makeCancelButton());
        return buttonPanel;
    }

    private JScrollPane makeTextPane(String licenseFromClipboard) {
        this.textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void removeUpdate(DocumentEvent e) {
                LicenseEnteringDialog.this.validateLicenseString();
            }

            public void insertUpdate(DocumentEvent e) {
                LicenseEnteringDialog.this.validateLicenseString();
            }

            public void changedUpdate(DocumentEvent e) {
                LicenseEnteringDialog.this.validateLicenseString();
            }
        });
        this.textArea.addMouseListener(new MouseListener() {
            private boolean alreadySelectedAll = false;

            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                if(this.alreadySelectedAll) {
                    LicenseEnteringDialog.this.textArea.select(0, 0);
                } else {
                    LicenseEnteringDialog.this.textArea.selectAll();
                }

            }
        });
        if(licenseFromClipboard != null) {
            this.textArea.setText(licenseFromClipboard);
        }

        this.textArea.setColumns(10);
        this.textArea.setRows(10);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(this.textArea);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    private JPanel makeDetailsPanel() {
        JPanel dPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 5;
        c.ipady = 3;
        c.insets = new Insets(1, 10, 1, 10);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.anchor = 21;
        dPanel.add(this.createLabel("status"), c);
        c.gridx = 1;
        this.statusLabel.setMinimumSize(new Dimension(180, 10));
        this.statusLabel.setPreferredSize(new Dimension(180, 10));
        dPanel.add(this.statusLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = 21;
        dPanel.add(this.createLabel("registered_to"), c);
        c.gridx = 1;
        c.fill = 0;
        this.registeredToLabel.setMinimumSize(new Dimension(300, 10));
        this.registeredToLabel.setPreferredSize(new Dimension(300, 10));
        dPanel.add(this.registeredToLabel, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = 21;
        dPanel.add(this.createLabel("product"), c);
        c.gridx = 1;
        dPanel.add(this.productLabel, c);
        c.gridx = 3;
        c.gridy = 2;
        dPanel.add(this.createLabel("start_date"), c);
        c.gridx = 4;
        this.startDateLabel.setMinimumSize(new Dimension(180, 10));
        this.startDateLabel.setPreferredSize(new Dimension(180, 10));
        dPanel.add(this.startDateLabel, c);
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = 21;
        dPanel.add(this.createLabel("edition"), c);
        c.gridx = 1;
        this.editionLabel.setMinimumSize(new Dimension(180, 10));
        this.editionLabel.setPreferredSize(new Dimension(180, 10));
        dPanel.add(this.editionLabel, c);
        c.gridx = 3;
        c.gridy = 3;
        dPanel.add(this.createLabel("expiration_date"), c);
        c.gridx = 4;
        this.expirationDateLabel.setMinimumSize(new Dimension(180, 10));
        this.expirationDateLabel.setPreferredSize(new Dimension(180, 10));
        dPanel.add(this.expirationDateLabel, c);
        return dPanel;
    }

    private void setValuesForDetailsPanel() {
        if(this.license != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = this.license.getStartDate();
            if(startDate == null) {
                this.startDateLabel.setText(I18N.getGUILabel("license.no_start_date", new Object[0]));
            } else {
                this.startDateLabel.setText(df.format(startDate));
            }

            Date expirationDate = this.license.getExpirationDate();
            if(expirationDate == null) {
                this.expirationDateLabel.setText(I18N.getGUILabel("license.no_end_date", new Object[0]));
            } else {
                this.expirationDateLabel.setText(df.format(expirationDate));
            }

            if(this.product != null) {
                this.productLabel.setText(LicenseTools.translateProductName(this.license));
                this.editionLabel.setText(LicenseTools.translateProductEdition(this.license));
            }

            this.registeredToLabel.setText(this.license.getLicenseUser().getName());
            this.licenseStatus = this.license.validate(new Date());
            switch(this.licenseStatus.ordinal()) {
                case 1:
                    this.statusLabel.setForeground(SwingTools.DARK_GREEN);
                    this.statusLabel.setText(I18N.getGUILabel("license.status_valid", new Object[0]));
                    this.installButton.setEnabled(true);
                    break;
                case 2:
                    this.statusLabel.setForeground(SwingTools.DARK_GREEN);
                    this.statusLabel.setText(I18N.getGUILabel("license.status_starts_in_future", new Object[0]));
                    this.installButton.setEnabled(true);
                    break;
                case 3:
                    this.statusLabel.setText(I18N.getGUILabel("license.status_product_version_invalid", new Object[0]));
                    this.statusLabel.setForeground(Color.RED);
                    this.installButton.setEnabled(false);
                    break;
                case 4:
                    this.statusLabel.setText(I18N.getGUILabel("license.status_expired", new Object[0]));
                    this.statusLabel.setForeground(Color.RED);
                    this.installButton.setEnabled(false);
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    this.statusLabel.setForeground(Color.RED);
                    this.statusLabel.setText(this.licenseStatus.toString());
                    this.installButton.setEnabled(false);
            }
        } else {
            this.installButton.setEnabled(!this.parseError);
            this.startDateLabel.setText("-");
            this.expirationDateLabel.setText("-");
            this.editionLabel.setText("-");
            this.registeredToLabel.setText("-");
            this.productLabel.setText("-");
            if(this.parseError) {
                this.statusLabel.setText(I18N.getGUILabel("license.parse_error", new Object[0]));
            } else {
                this.statusLabel.setText(I18N.getGUILabel("license.status_invalid", new Object[0]));
            }

            this.statusLabel.setForeground(Color.RED);
        }

    }

    private JLabel createLabel(String key) {
        JLabel label = new JLabel(I18N.getGUILabel("license." + key, new Object[0]));
        label.setFont(new Font("SansSerif", 1, 13));
        return label;
    }

    private void validateLicenseString() {
        String enteredLicenseKey = this.getInputText();
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

    public String getInputText() {
        return this.textArea.getText();
    }
}
