package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.license.onboarding.AbstractCard;
import com.rapidminer.gui.license.onboarding.ActivationSuccessfulCard;
import com.rapidminer.gui.license.onboarding.ConnectToServiceCard;
import com.rapidminer.gui.license.onboarding.EmailVerificationCard;
import com.rapidminer.gui.license.onboarding.EnterLicenseCard;
import com.rapidminer.gui.license.onboarding.ExpirationReminderCard;
import com.rapidminer.gui.license.onboarding.InitialConnectToServiceCard;
import com.rapidminer.gui.license.onboarding.SignUpCard;
import com.rapidminer.gui.license.onboarding.TrialReminderCard;
import com.rapidminer.gui.license.onboarding.WelcomeCard;
import com.rapidminer.gui.license.onboarding.OnboardingManager.ReminderType;
import com.rapidminer.gui.license.onboarding.OnboardingManager.WelcomeType;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.LinkButton;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.RMUrlHandler;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import org.apache.commons.collections15.map.HashedMap;

public class OnboardingDialog extends ButtonDialog {
    private static final long serialVersionUID = 6139135884802892879L;
    private static final Font OPEN_SANS_LIGHT_14 = new Font("Open Sans", 0, 14);
    private static final Font OPEN_SANS_LIGHT_28 = new Font("Open Sans Light", 0, 28);
    private static final Font OPEN_SANS_SEMIBOLD_14 = new Font("Open Sans Semibold", 1, 14);
    private static final Font OPEN_SANS_SEMIBOLD_16 = new Font("Open Sans Semibold", 1, 16);
    public static final Color LIGHTER_GRAY = new Color(220, 220, 220);
    public static final Color COLOR_HEADER_PANEL = new Color(236, 236, 236);
    public static final Color COLOR_CONTENT_PANEL = new Color(247, 247, 247);
    private static final Icon HEADER_ICON = getIcon("onboarding.header");
    private final Map<String, Object> sharedObjects;
    private final Map<String, AbstractCard> cardPool;
    private Runnable doAfterSuccess;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private WelcomeType welcomeType;
    private ReminderType reminderType;
    private AbstractCard currentCard;

    public OnboardingDialog() {
        this(WelcomeType.FIRST_WELCOME);
    }

    public OnboardingDialog(WelcomeType welcomeType) {
        super(ApplicationFrame.getApplicationFrame(), "onboarding", ModalityType.APPLICATION_MODAL, new Object[0]);
        this.sharedObjects = new HashedMap();
        this.cardPool = new HashedMap();
        this.welcomeType = WelcomeType.FIRST_WELCOME;
        this.reminderType = ReminderType.REMINDER;
        this.welcomeType = welcomeType;
        this.initGUI();
    }

    public OnboardingDialog(ReminderType reminderType) {
        super(ApplicationFrame.getApplicationFrame(), "onboarding", ModalityType.APPLICATION_MODAL, new Object[0]);
        this.sharedObjects = new HashedMap();
        this.cardPool = new HashedMap();
        this.welcomeType = WelcomeType.FIRST_WELCOME;
        this.reminderType = ReminderType.REMINDER;
        this.welcomeType = WelcomeType.WELCOME_REMINDER;
        this.reminderType = reminderType;
        this.initGUI();
    }

    public WelcomeType getWelcomeType() {
        return this.welcomeType;
    }

    public ReminderType getReminderType() {
        return this.reminderType;
    }

    public void showActivationSuccessfulCard(String userName, String productEdition, String expirationDate) {
        this.showActivationSuccessfulCard(userName, productEdition, expirationDate, false, (String)null);
    }

    public void showActivationSuccessfulCard(String userName, String productEdition, String expirationDate, boolean forceRestart, String addtionalInformation) {
        this.putSharedObject("com.rapidminer.onboarding.license.owner", userName);
        this.putSharedObject("com.rapidminer.onboarding.license.edition", productEdition);
        this.putSharedObject("com.rapidminer.onboarding.license.expiration", expirationDate);
        this.putSharedObject("com.rapidminer.onboarding.force_restart", Boolean.valueOf(forceRestart));
        this.putSharedObject("com.rapidminer.onboarding.license.additional_info", addtionalInformation);
        this.showCard("activation_successful");
    }

    public void showWelcomeCard() {
        this.showWelcomeCard((String)null);
    }

    public void showWelcomeCard(String errorMsg) {
        this.putSharedObject("com.rapidminer.onboarding.error", errorMsg);
        this.showCard("welcome");
    }

    public void showInitialConnectToServiceCard() {
        this.showCard("initialConnectToService");
    }

    public void showInitialConnectToServiceCard(String key) {
        this.putSharedObject("com.rapidminer.onboarding.back_button", key);
        this.showInitialConnectToServiceCard();
    }

    public void showEmailVerificationCard(String userName, char[] password, String backKey) {
        this.showEmailVerificationCard((String)null, userName, password, backKey);
    }

    public void showEmailVerificationCard(String errorMsg, String userName, char[] password, String backKey) {
        this.putSharedObject("com.rapidminer.onboarding.error", errorMsg);
        this.putSharedObject("com.rapidminer.onboarding.user", userName);
        this.putSharedObject("com.rapidminer.onboarding.password", password);
        this.putSharedObject("com.rapidminer.onboarding.back_button", backKey);
        this.showCard("emailVerification");
    }

    public void showTrialReminderCard(Date expirationDate) {
        this.putSharedObject("com.rapidminer.onboarding.license.expiration", expirationDate);
        this.showTrialReminderCard();
    }

    public void showTrialReminderCard() {
        this.showCard("trial_reminder");
    }

    public void showExpirationReminderCard(Date expirationDate, String previousLicenseEdition, String nextLicenseEdition) {
        this.putSharedObject("com.rapidminer.onboarding.license.expiration", expirationDate);
        this.showExpirationReminderCard(previousLicenseEdition, nextLicenseEdition);
    }

    public void showExpirationReminderCard(String previousLicenseEdition, String nextLicenseEdition) {
        this.putSharedObject("com.rapidminer.onboarding.license.old.edition", previousLicenseEdition);
        this.putSharedObject("com.rapidminer.onboarding.license.upcoming.edition", nextLicenseEdition);
        this.showCard("expiration_reminder");
    }

    public void showConnectToServiceCard() {
        this.showConnectToService((String)null);
    }

    public void showConnectToService(String errorMsg) {
        this.putSharedObject("com.rapidminer.onboarding.error", errorMsg);
        this.showCard("connectToService");
    }

    public void showSignupCard(String key) {
        this.showCard("signup");
        this.putSharedObject("com.rapidminer.onboarding.back_button", key);
    }

    public void showEnterLicenseCard() {
        this.showCard("enter_license");
    }

    public void showActivationSuccessfulCard() {
        this.showCard("activation_successful");
    }

    public void setAfterSuccessAction(Runnable doAfterSuccess) {
        this.doAfterSuccess = doAfterSuccess;
    }

    Runnable getAfterSuccessAction() {
        return this.doAfterSuccess;
    }

    public static void openURL(String urlString) {
        try {
            RMUrlHandler.browse(new URI(urlString));
        } catch (Exception var2) {
            SwingTools.showSimpleErrorMessage("onboarding.open_browser_fail", var2.getMessage(), new Object[0]);
        }

    }

    static JPanel createButtonPanel(JButton... buttons) {
        return createButtonPanel(true, buttons);
    }

    static JPanel createButtonPanel(boolean right, JButton... buttons) {
        JPanel btPanel = new JPanel(new FlowLayout(right?2:0, 5, 5));
        btPanel.setOpaque(false);
        JButton[] var3 = buttons;
        int var4 = buttons.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            JButton button = var3[var5];
            btPanel.add(button);
        }

        btPanel.setMaximumSize(new Dimension(btPanel.getMaximumSize().width, btPanel.getPreferredSize().height));
        return btPanel;
    }

    static JPanel createSimpleHeader(String caption) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0D;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = 17;
        JLabel lbHeadLine = new JLabel(caption);
        lbHeadLine.putClientProperty("com.rapidminer.ui.label.type", "header");
        panel.add(lbHeadLine, gbc);
        ++gbc.gridx;
        gbc.weightx = 0.0D;
        JLabel lbLogoIcon = new JLabel(HEADER_ICON);
        panel.add(lbLogoIcon, gbc);
        return panel;
    }

    static Icon getIcon(String i18nKey) {
        return SwingTools.createIcon("onboarding/" + I18N.getMessage(I18N.getGUIBundle(), "gui.label." + i18nKey + ".icon", new Object[0]));
    }

    static JLabel createLabel(String i18nKey, String labelStyle) {
        return createLabel(i18nKey, labelStyle, false);
    }

    static JLabel createLabel(String i18nKey, String labelStyle, boolean alignCenter) {
        ResourceLabel result = new ResourceLabel(i18nKey, new Object[0]);
        result.putClientProperty("com.rapidminer.ui.label.type", labelStyle);
        result.setAlignmentX(0.5F);
        return result;
    }

    Object getSharedObject(String key) {
        return this.sharedObjects.get(key);
    }

    void putSharedObject(String key, Object value) {
        this.sharedObjects.put(key, value);
    }

    private void initGUI() {
        this.mainPanel = new JPanel(new GridBagLayout());
        this.mainPanel.setBackground(COLOR_CONTENT_PANEL);
        this.headerPanel = new JPanel(new CardLayout());
        this.headerPanel.setBackground(COLOR_HEADER_PANEL);
        this.contentPanel = new JPanel(new CardLayout());
        this.contentPanel.setBackground(COLOR_CONTENT_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 1;
        gbc.weightx = 1.0D;
        gbc.weighty = 0.0D;
        gbc.gridy = 0;
        this.mainPanel.add(this.headerPanel, gbc);
        ++gbc.gridy;
        gbc.weighty = 0.0D;
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.LIGHT_GRAY.darker());
        separator.setBackground(Color.LIGHT_GRAY);
        this.mainPanel.add(separator, gbc);
        ++gbc.gridy;
        gbc.weighty = 1.0D;
        this.mainPanel.add(this.contentPanel, gbc);
        this.addCards(new AbstractCard[]{new WelcomeCard(this, this.welcomeType), new SignUpCard(this), new EmailVerificationCard(this), new TrialReminderCard(this, this.reminderType), new ExpirationReminderCard(this, this.reminderType), new InitialConnectToServiceCard(this), new EnterLicenseCard(this), new ActivationSuccessfulCard(this), new ConnectToServiceCard(this)});
        this.decorateAllComponentsIn(this.mainPanel);
        this.add(this.mainPanel);
        this.pack();
        this.setLocationRelativeTo(RapidMinerGUI.getMainFrame());
        this.setResizable(false);
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if(OnboardingDialog.this.currentCard != null) {
                    OnboardingDialog.this.currentCard.getCloseAction().run();
                } else {
                    OnboardingDialog.this.dispose();
                }

            }
        });
        this.getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0, false), "WINDOW_CLOSING");
        this.getRootPane().getActionMap().put("WINDOW_CLOSING", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                OnboardingDialog.this.dispatchEvent(new WindowEvent(OnboardingDialog.this, 201));
            }
        });
    }

    private void addCards(AbstractCard... cards) {
        AbstractCard[] var2 = cards;
        int var3 = cards.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            AbstractCard card = var2[var4];
            this.cardPool.put(card.getId(), card);
            this.headerPanel.add(card.getHeader(), card.getId());
            this.contentPanel.add(card.getContent(), card.getId());
        }

    }

    private void showCard(String cardId) {
        CardLayout cl = (CardLayout)this.headerPanel.getLayout();
        cl.show(this.headerPanel, cardId);
        this.headerPanel.revalidate();
        this.headerPanel.repaint();
        cl = (CardLayout)this.contentPanel.getLayout();
        cl.show(this.contentPanel, cardId);
        this.contentPanel.revalidate();
        this.contentPanel.repaint();
        this.currentCard = (AbstractCard)this.cardPool.get(cardId);
        if(this.currentCard != null) {
            this.currentCard.showCard();
        }

    }

    private void decorateAllComponentsIn(Container parent) {
        Component[] var2 = parent.getComponents();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Component c = var2[var4];
            if(c instanceof JComponent) {
                this.decorateJComponent((JComponent)c);
            }

            if(c instanceof Container) {
                this.decorateAllComponentsIn((Container)c);
            }
        }

    }

    private void decorateJComponent(JComponent component) {
        String prop = (String)component.getClientProperty("com.rapidminer.ui.label.type");
        if("header".equals(prop)) {
            component.setFont(OPEN_SANS_LIGHT_28);
            component.setForeground(SwingTools.RAPIDMINER_ORANGE);
        } else if("bold".equals(prop)) {
            component.setFont(OPEN_SANS_SEMIBOLD_14);
            component.setForeground(Color.DARK_GRAY);
        } else if("normal".equals(prop)) {
            component.setFont(OPEN_SANS_LIGHT_14);
            component.setForeground(Color.DARK_GRAY);
        } else if("large".equals(prop)) {
            component.setFont(OPEN_SANS_SEMIBOLD_16);
            component.setForeground(Color.DARK_GRAY);
        }

        prop = (String)component.getClientProperty("com.rapidminer.ui.button.type");
        if("cfa".equals(prop)) {
            component.setFont(OPEN_SANS_SEMIBOLD_14);
            component.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        } else if("normal".equals(prop)) {
            component.setFont(OPEN_SANS_LIGHT_14);
            component.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            component.setMinimumSize(new Dimension(175, component.getPreferredSize().height));
            component.setPreferredSize(new Dimension(175, component.getPreferredSize().height));
        }

        prop = (String)component.getClientProperty("com.rapidmniner.ui.link_button.id");
        if(prop != null && component instanceof LinkButton) {
            ((LinkButton)component).setText(this.generateHTML(prop));
        }

        Color foregroundColor = (Color)component.getClientProperty("com.rapidminer.ui.label.foreground");
        if(foregroundColor != null) {
            component.setForeground(foregroundColor);
        }

    }

    private String generateHTML(String i18nKey) {
        return "<html><body><a style=\"font-family: Open Sans Light;font-size: 10px\" href=\"\">" + I18N.getMessage(I18N.getGUIBundle(), "gui.action." + i18nKey + ".label", new Object[0]) + "</a></body></html>";
    }
}
