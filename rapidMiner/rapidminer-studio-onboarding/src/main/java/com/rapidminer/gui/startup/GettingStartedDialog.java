package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.license.onboarding.OnboardingManager;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.startup.CardButton;
import com.rapidminer.gui.startup.GettingStartedCard;
import com.rapidminer.gui.startup.NewProcessCard;
import com.rapidminer.gui.startup.NewsItem;
import com.rapidminer.gui.startup.NewsPanel;
import com.rapidminer.gui.startup.NewsService;
import com.rapidminer.gui.startup.OpenProcessCard;
import com.rapidminer.gui.startup.TutorialCard;
import com.rapidminer.gui.tools.Ionicon;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.studio.internal.StartupDialogProvider.ToolbarButton;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.Observable;
import com.rapidminer.tools.Observer;
import com.rapidminer.tools.usagestats.ActionStatisticsCollector;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GettingStartedDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    public static final Font OPEN_SANS_LIGHT_14 = new Font("Open Sans Light", 0, 14);
    public static final Font OPEN_SANS_SEMIBOLD_14 = new Font("Open Sans Semibold", 0, 14);
    public static final Color BACKGROUND_COLOR;
    public static final Color VERY_LIGHT_GRAY;
    private static final String KEY_STARTUP_BUTTON = "startup_selected";
    private static final Dimension NEWS_PANEL_SIZE;
    private static final String HEADING_TEMPLATE = "<html><div style=\"width: 250; height: 125;\"><span style=\"font-family: \'Open Sans Light\'; font-size: 28; color: white;\">%s</span></div></html>";
    private static final String CARD_GETTING_STARTED = "getting-started";
    private static final String CARD_TUTORIAL = "tutorial";
    private static final String CARD_NEW_PROCESS = "new-process";
    private static final String CARD_OPEN_PROCESS = "open-process";
    private static final Map<String, String> HEADINGS;
    public static final Color MENU_BACKGROUND;
    public static final Color ITEM_HIGHLIGHT;
    public static final Color ITEM_SELECTED_HIGHLIGHT;
    private JPanel contentPanel;
    private Map<CardButton, String> buttonMap;
    private Map<String, Component> componentMap;
    private Map<CardButton, ToolbarButton> selectedButtonMap;
    private ToolbarButton selectedButton;
    private transient Observer<List<NewsItem>> newsObserver;

    public GettingStartedDialog() {
        this(null);
    }

    public GettingStartedDialog(ToolbarButton startButton) {
        super(ApplicationFrame.getApplicationFrame(), ModalityType.APPLICATION_MODAL);
        this.setPreferredSize(new Dimension(1000, 700));
        this.setResizable(false);
        this.setDefaultCloseOperation(2);
        this.buttonMap = new HashMap(4);
        this.selectedButtonMap = new HashMap(4);
        this.componentMap = new HashMap(4);
        if(startButton == null) {
            this.selectedButton = this.recoverLastSelectedButton();
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    OnboardingManager.INSTANCE.putProperty("startup_selected", GettingStartedDialog.this.selectedButton.name());
                }
            });
        } else {
            this.selectedButton = startButton;
        }

        this.setLayout(new BorderLayout());
        final ImageIcon logo = SwingTools.createIcon("onboarding/rm_logo_white.png");
        JPanel menuPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D)graphics.create();
                g.setComposite(AlphaComposite.getInstance(3, 0.075F));
                g.drawImage(logo.getImage(), -60, this.getHeight() - 330, 410, 410, (ImageObserver)null);
                g.dispose();
            }
        };
        menuPanel.setBackground(MENU_BACKGROUND);
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.anchor = 18;
        constraints.insets = new Insets(18, 28, 12, 28);
        ActionListener cardChooser = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout)GettingStartedDialog.this.contentPanel.getLayout();
                String cardKey = GettingStartedDialog.this.buttonMap.get(e.getSource());
                GettingStartedDialog.logStats("card_selected", cardKey);
                layout.show(GettingStartedDialog.this.contentPanel, cardKey);

                for(Iterator var4 = GettingStartedDialog.this.buttonMap.keySet().iterator(); var4.hasNext(); ((Component)GettingStartedDialog.this.componentMap.get(cardKey)).requestFocusInWindow()) {
                    CardButton button = (CardButton)var4.next();
                    if(button != e.getSource() && button.isSelected()) {
                        button.setSelected(false);
                    } else if(button == e.getSource() && !button.isSelected()) {
                        button.setSelected(true);
                    }

                    if(button.isSelected()) {
                        GettingStartedDialog.this.selectedButton = GettingStartedDialog.this.selectedButtonMap.get(button);
                    }
                }

            }
        };
        ++constraints.gridy;
        constraints.weighty = 0.0D;
        constraints.fill = 2;
        constraints.insets = new Insets(30, 28, 12, 28);
        CardButton gettingStartedButton = new CardButton(Ionicon.FLASH, "getting_started.button.welcome");
        gettingStartedButton.addActionListener(cardChooser);
        this.buttonMap.put(gettingStartedButton, "getting-started");
        this.selectedButtonMap.put(gettingStartedButton, ToolbarButton.GETTING_STARTED);
        menuPanel.add(gettingStartedButton, constraints);
        ++constraints.gridy;
        constraints.insets = new Insets(4, 28, 12, 28);
        CardButton tutorialButton = new CardButton(Ionicon.UNIVERSITY, "getting_started.button.tutorial");
        tutorialButton.addActionListener(cardChooser);
        this.buttonMap.put(tutorialButton, "tutorial");
        this.selectedButtonMap.put(tutorialButton, ToolbarButton.TUTORIAL);
        menuPanel.add(tutorialButton, constraints);
        ++constraints.gridy;
        CardButton newButton = new CardButton(Ionicon.ANDROID_ADD_CIRCLE, "getting_started.button.new_process");
        newButton.addActionListener(cardChooser);
        this.buttonMap.put(newButton, "new-process");
        this.selectedButtonMap.put(newButton, ToolbarButton.NEW_PROCESS);
        menuPanel.add(newButton, constraints);
        ++constraints.gridy;
        CardButton openButton = new CardButton(Ionicon.ANDROID_FOLDER, "getting_started.button.open_process");
        openButton.addActionListener(cardChooser);
        this.buttonMap.put(openButton, "open-process");
        this.selectedButtonMap.put(openButton, ToolbarButton.OPEN_PROCESS);
        menuPanel.add(openButton, constraints);
        constraints.weighty = 1.0D;
        ++constraints.gridy;
        constraints.fill = 1;
        menuPanel.add(new JLabel(), constraints);
        ++constraints.gridy;
        constraints.weighty = 0.0D;
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.fill = 2;
        final NewsPanel openCard = new NewsPanel();
        openCard.setMinimumSize(NEWS_PANEL_SIZE);
        openCard.setPreferredSize(NEWS_PANEL_SIZE);

        this.newsObserver = new Observer<List<NewsItem>>()
        {
            public void update(Observable<List<NewsItem>> observable, List<NewsItem> news) {
                openCard.updateNews(news);
            }
        };
        NewsService.INSTANCE.addNewsItemObserver(this.newsObserver);
        openCard.updateNews(NewsService.INSTANCE.getNewsItems());
        menuPanel.add(openCard, constraints);
        this.add(menuPanel, "West");
        this.contentPanel = new JPanel(new CardLayout());
        GettingStartedCard openCard1 = new GettingStartedCard(this);
        this.contentPanel.add(openCard1, "getting-started");
        this.componentMap.put("getting-started", openCard1);
        NewProcessCard openCard2 = new NewProcessCard(this);
        this.contentPanel.add(openCard2, "new-process");
        this.componentMap.put("new-process", openCard2);
        TutorialCard openCard3 = new TutorialCard(this);
        this.contentPanel.add(openCard3, "tutorial");
        this.componentMap.put("tutorial", openCard3);
        OpenProcessCard openCard4 = new OpenProcessCard(this);
        this.contentPanel.add(openCard4, "open-process");
        this.componentMap.put("open-process", openCard4);
        if(openCard4.shouldBeShown() && startButton == null) {
            this.selectedButton = ToolbarButton.OPEN_PROCESS;
        }

        this.add(this.contentPanel, "Center");
        this.showCard(this.selectedButton);
        this.pack();
        this.setLocationRelativeTo(this.getOwner());
        this.getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0, false), "WINDOW_CLOSING");
        this.getRootPane().getActionMap().put("WINDOW_CLOSING", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                GettingStartedDialog.logStats("dialog", "closed_via_window_event");
                GettingStartedDialog.this.dispatchEvent(new WindowEvent(GettingStartedDialog.this, 201));
            }
        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                GettingStartedDialog.this.removeObservers();
            }
        });
    }

    void showCard(ToolbarButton card) {
        Iterator var2 = this.selectedButtonMap.entrySet().iterator();

        while(var2.hasNext()) {
            Entry entry = (Entry)var2.next();
            if(entry.getValue() == card) {
                ((CardButton)entry.getKey()).doClick();
                String cardKey = this.buttonMap.get(entry.getKey());
                (this.componentMap.get(cardKey)).requestFocus();
                break;
            }
        }

    }

    private void removeObservers() {
        if(this.newsObserver != null) {
            NewsService.INSTANCE.removeNewsItemObserver(this.newsObserver);
            this.newsObserver = null;
        }

    }

    private ToolbarButton recoverLastSelectedButton() {
        String selected = OnboardingManager.INSTANCE.getProperty("startup_selected");
        logStats("first_selected_button", selected);
        ToolbarButton selectedButton = ToolbarButton.GETTING_STARTED;
        if(ToolbarButton.NEW_PROCESS.name().equals(selected)) {
            selectedButton = ToolbarButton.NEW_PROCESS;
        } else if(ToolbarButton.OPEN_PROCESS.name().equals(selected)) {
            selectedButton = ToolbarButton.OPEN_PROCESS;
        }

        return selectedButton;
    }

    public static void logStats(String type, String value) {
        ActionStatisticsCollector.INSTANCE.log("getting_started", type, value);
    }

    static {
        BACKGROUND_COLOR = Color.WHITE;
        VERY_LIGHT_GRAY = new Color(225, 225, 225);
        NEWS_PANEL_SIZE = new Dimension(300, 155);
        HEADINGS = new HashMap(3);
        HEADINGS.put("getting-started", String.format("<html><div style=\"width: 250; height: 125;\"><span style=\"font-family: \'Open Sans Light\'; font-size: 28; color: white;\">%s</span></div></html>", new Object[]{I18N.getGUILabel("getting_started.heading.welcome", new Object[0])}));
        HEADINGS.put("tutorial", String.format("<html><div style=\"width: 250; height: 125;\"><span style=\"font-family: \'Open Sans Light\'; font-size: 28; color: white;\">%s</span></div></html>", new Object[]{I18N.getGUILabel("getting_started.heading.tutorial", new Object[0])}));
        HEADINGS.put("new-process", String.format("<html><div style=\"width: 250; height: 125;\"><span style=\"font-family: \'Open Sans Light\'; font-size: 28; color: white;\">%s</span></div></html>", new Object[]{I18N.getGUILabel("getting_started.heading.new_process", new Object[0])}));
        HEADINGS.put("open-process", String.format("<html><div style=\"width: 250; height: 125;\"><span style=\"font-family: \'Open Sans Light\'; font-size: 28; color: white;\">%s</span></div></html>", new Object[]{I18N.getGUILabel("getting_started.heading.open_process", new Object[0])}));
        MENU_BACKGROUND = new Color(243, 112, 24);
        ITEM_HIGHLIGHT = new Color(217, 235, 255);
        ITEM_SELECTED_HIGHLIGHT = Colors.TEXT_HIGHLIGHT_BACKGROUND;
    }
}
