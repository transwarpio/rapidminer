package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.startup.NewsItem;
import com.rapidminer.gui.tools.Ionicon;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.LinkRemoteButton;
import com.rapidminer.tools.RMUrlHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

class NewsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String NEWS_HEADLINE_TEMPLATE = "<html><span style=\"font-family: \'Open Sans Bold\';font-size: 13; color: white;\">%s</html>";
    private static final String NEWS_CONENT_TEMPLATE = "<html><div style=\"font-family: \'Open Sans\'; font-size: 13; color: white; width: 250;\">%s</div></html>";
    private static final String NEWS_INDICATOR = "<html><span style=\"font-size: 13; color: white;\">%s</html>";
    private static final String CHEVRON_TEMPLATE = "<html><span style=\"font-size: 18; color: white;\">%s</span></html>";
    private static final int CONTENT_MAX_LENGTH = 150;
    private static final int TOPIC_MAX_LENGTH = 35;
    private static final int URL_MAX_LENGTH = 40;
    private static final int INDICATOR_MAX_LENGTH = 8;
    private static final String CUT_INDICATOR = " ...";
    private JLabel headlineLabel;
    private JLabel contentLabel;
    private JPanel contentPanel;
    private JPanel contentLinkPanel;
    private JPanel indicatorPanel;
    private JPanel previousButtonPanel;
    private JPanel nextButtonPanel;
    private LinkRemoteButton contentLinkButton;
    private JButton[] indicatorButtons;
    private JButton previousButton;
    private JButton nextButton;
    private int selectedIndex;
    private List<NewsItem> newsItems;

    public NewsPanel() {
        this.initGui();
        GettingStartedDialog.logStats("news_panel", "created");
    }

    private void initGui() {
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.previousButton = new JButton(String.format("<html><span style=\"font-size: 18; color: white;\">%s</span></html>", new Object[]{Ionicon.CHEVRON_LEFT.getHtml()}));
        this.previousButton.setFocusable(false);
        this.previousButton.setBorderPainted(false);
        this.previousButton.setContentAreaFilled(false);
        this.previousButton.setCursor(new Cursor(12));
        this.previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GettingStartedDialog.logStats("news_panel", "previous_news");
                NewsPanel.this.setSelectedIndex(NewsPanel.this.selectedIndex - 1 < 0?NewsPanel.this.newsItems.size() - 1:NewsPanel.this.selectedIndex - 1);
            }
        });
        this.previousButtonPanel = new JPanel(new GridBagLayout());
        this.previousButtonPanel.setMinimumSize(this.previousButton.getMinimumSize());
        this.previousButtonPanel.setPreferredSize(this.previousButton.getPreferredSize());
        this.previousButtonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.weightx = 1.0D;
        this.previousButtonPanel.add(this.previousButton, gbc);
        this.nextButton = new JButton(String.format("<html><span style=\"font-size: 18; color: white;\">%s</span></html>", new Object[]{Ionicon.CHEVRON_RIGHT.getHtml()}));
        this.nextButton.setFocusable(false);
        this.nextButton.setBorderPainted(false);
        this.nextButton.setContentAreaFilled(false);
        this.nextButton.setCursor(new Cursor(12));
        this.nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GettingStartedDialog.logStats("news_panel", "next_news");
                NewsPanel.this.setSelectedIndex((NewsPanel.this.selectedIndex + 1) % NewsPanel.this.newsItems.size());
            }
        });
        this.nextButtonPanel = new JPanel(new GridBagLayout());
        this.nextButtonPanel.setMinimumSize(this.nextButton.getMinimumSize());
        this.nextButtonPanel.setPreferredSize(this.nextButton.getPreferredSize());
        this.nextButtonPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.weightx = 1.0D;
        this.nextButtonPanel.add(this.nextButton, gbc);
        this.contentPanel = new JPanel();
        this.contentPanel.setOpaque(false);
        this.contentPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.5D;
        gbc.fill = 1;
        this.contentPanel.add(new JLabel(), gbc);
        ++gbc.gridy;
        gbc.weighty = 0.0D;
        gbc.fill = 2;
        this.headlineLabel = new JLabel();
        this.contentPanel.add(this.headlineLabel, gbc);
        ++gbc.gridy;
        this.contentLabel = new JLabel();
        this.contentPanel.add(this.contentLabel, gbc);
        ++gbc.gridy;
        this.contentLinkPanel = new JPanel(new FlowLayout(2, 0, 0));
        this.contentLinkPanel.setOpaque(false);
        this.contentPanel.add(this.contentLinkPanel, gbc);
        ++gbc.gridy;
        gbc.weighty = 0.5D;
        gbc.fill = 1;
        this.contentPanel.add(new JLabel(), gbc);
        this.indicatorPanel = new JPanel(new FlowLayout(1, 0, 0));
        this.indicatorPanel.setMinimumSize(new Dimension(this.indicatorPanel.getMinimumSize().width, 20));
        this.indicatorPanel.setPreferredSize(new Dimension(this.indicatorPanel.getPreferredSize().width, 20));
        this.indicatorPanel.setOpaque(false);
    }

    public void updateNews(List<NewsItem> newNews) {
        this.removeAll();
        this.indicatorPanel.removeAll();
        this.newsItems = newNews;
        if(this.newsItems != null && this.newsItems.size() != 0) {
            boolean pagination = this.newsItems.size() > 1;
            if(pagination) {
                this.indicatorButtons = new JButton[this.newsItems.size() > 8?8:this.newsItems.size()];

                for(int i = 0; i < this.newsItems.size(); ++i) {
                    if(i == 8) {
                        JLabel newIndex = new JLabel(" ...");
                        newIndex.setForeground(Color.WHITE);
                        this.indicatorPanel.add(newIndex);
                        break;
                    }

                    this.indicatorButtons[i] = new JButton(String.format("<html><span style=\"font-size: 13; color: white;\">%s</html>", new Object[]{Ionicon.ANDROID_RADIO_BUTTON_OFF.getHtml()}));
                    this.indicatorButtons[i].setBorderPainted(false);
                    this.indicatorButtons[i].setContentAreaFilled(false);
                    this.indicatorButtons[i].setBorder(BorderFactory.createEmptyBorder());
                    this.indicatorButtons[i].setCursor(new Cursor(12));
                    this.indicatorButtons[i].setFocusable(false);
                    final int fi = i;
                    this.indicatorButtons[i].addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            NewsPanel.this.setSelectedIndex(fi);
                        }
                    });
                    this.indicatorPanel.add(this.indicatorButtons[i]);
                }
            } else {
                this.indicatorButtons = new JButton[0];
            }

            this.nextButton.setVisible(pagination);
            this.previousButton.setVisible(pagination);
            this.add(this.contentPanel, "Center");
            this.add(this.nextButtonPanel, "East");
            this.add(this.previousButtonPanel, "West");
            this.add(this.indicatorPanel, "South");
            this.setSelectedIndex(0);
        } else {
            this.revalidate();
            this.repaint();
        }
    }

    private void setSelectedIndex(int newSelectedIndex) {
        NewsItem item = (NewsItem)this.newsItems.get(newSelectedIndex);
        this.headlineLabel.setText(String.format("<html><span style=\"font-family: \'Open Sans Bold\';font-size: 13; color: white;\">%s</html>", new Object[]{this.stripString(item.getTopic(), 35)}));
        this.contentLabel.setText(String.format("<html><div style=\"font-family: \'Open Sans\'; font-size: 13; color: white; width: 250;\">%s</div></html>", new Object[]{this.stripString(item.getContent(), 150)}));
        if(this.contentLinkButton != null) {
            this.contentLinkPanel.remove(this.contentLinkButton);
            this.contentLinkButton = null;
        }

        if(item.getLink() != null && !item.getLink().trim().isEmpty()) {
            final String i = item.getLink();
            String shownUrl = this.stripString(item.getLinkText() != null?item.getLinkText():i, 40);
            this.contentLinkButton = new LinkRemoteButton(new ResourceAction("news_panel.remote_link", new Object[]{item.getLink(), shownUrl}) {
                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        RMUrlHandler.browse(new URI(i));
                    } catch (Exception var3) {
                        SwingTools.showSimpleErrorMessage("cannot_open_browser_url", var3, new Object[]{i});
                    }

                }
            });
            this.contentLinkButton.setFocusable(false);
            HTMLEditorKit htmlKit = (HTMLEditorKit)this.contentLinkButton.getEditorKit();
            htmlKit.getStyleSheet().addRule("a {color:#ffffff;font-family:\'Open Sans\';font-size:13;}");
            this.contentLinkPanel.add(this.contentLinkButton);
        }

        for(int var6 = 0; var6 < this.indicatorButtons.length; ++var6) {
            if(var6 == newSelectedIndex) {
                this.indicatorButtons[var6].setText(String.format("<html><span style=\"font-size: 13; color: white;\">%s</html>", new Object[]{Ionicon.ANDROID_RADIO_BUTTON_ON.getHtml()}));
            } else {
                this.indicatorButtons[var6].setText(String.format("<html><span style=\"font-size: 13; color: white;\">%s</html>", new Object[]{Ionicon.ANDROID_RADIO_BUTTON_OFF.getHtml()}));
            }
        }

        this.selectedIndex = newSelectedIndex;
        this.revalidate();
        this.repaint();
    }

    private String stripString(String text, int maxLenght) {
        return text == null?"":(text.length() > maxLenght?text.substring(0, maxLenght - 1 - " ...".length()) + " ...":text);
    }
}
