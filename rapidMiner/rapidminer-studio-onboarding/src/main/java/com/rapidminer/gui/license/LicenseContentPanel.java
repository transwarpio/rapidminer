package com.rapidminer.gui.license;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.tools.Ionicon;
import com.rapidminer.license.License;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Tools;
import java.awt.Dimension;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JEditorPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class LicenseContentPanel extends JEditorPane {
    private static final long serialVersionUID = 1L;
    private static final int PANEL_HEIGHT = 330;
    private static final int PANEL_WIDTH = 550;
    private static String HTML_TEMPLATE;
    private List<License> licenses;

    public LicenseContentPanel(List<License> licenses) {
        super("text/html", "");
        this.setPreferredSize(new Dimension(550, 330));
        this.setEditable(false);
        this.licenses = new ArrayList(licenses);
        this.setBorder((Border)null);
        this.updateContent();
    }

    public void generateHTML() throws BadLocationException, IOException {
        StringReader stringReader = new StringReader(HTML_TEMPLATE);
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument html = (HTMLDocument)htmlKit.createDefaultDocument();
        htmlKit.read(stringReader, html, 0);
        Element licenseList = html.getElement("licenses");
        Element intro = html.getElement("my-account");
        String introText = I18N.getGUILabel("manage_licenses.intro", new Object[0]);
        html.setInnerHTML(intro, introText);
        DateFormat format = DateFormat.getDateTimeInstance(2, 3);
        Iterator css = this.licenses.iterator();

        while(css.hasNext()) {
            License backgroundURL = (License)css.next();
            String productName = LicenseTools.translateProductName(backgroundURL);
            String productEdition = LicenseTools.translateProductEdition(backgroundURL);
            String expiration = I18N.getGUILabel("manage_licenses.perpetual_license", new Object[0]);
            if(backgroundURL.getExpirationDate() != null) {
                expiration = I18N.getGUILabel("manage_licenses.valid_until", new Object[]{format.format(backgroundURL.getExpirationDate())});
            }

            String annotations = null;
            if(backgroundURL.getAnnotations() != null && !backgroundURL.getAnnotations().trim().isEmpty()) {
                annotations = I18N.getGUILabel("manage_licenses.annotations", new Object[]{backgroundURL.getAnnotations()});
            }

            String licenseUser = null;
            if(backgroundURL.getLicenseUser().getName() != null && !backgroundURL.getLicenseUser().getName().trim().isEmpty()) {
                licenseUser = I18N.getGUILabel("manage_licenses.registered_to", new Object[]{backgroundURL.getLicenseUser().getName()});
            }

            StringBuffer entry = new StringBuffer();
            entry.append("<tr><td>" + Ionicon.ARROW_RIGHT_B.getHtml() + "</td><td>");
            entry.append(String.format("<strong>%s %s</strong>", new Object[]{productName, productEdition}));
            if(licenseUser != null) {
                entry.append(String.format("<br />%s", new Object[]{licenseUser}));
            }

            if(annotations != null) {
                entry.append(String.format("<br />%s", new Object[]{annotations}));
            }

            entry.append(String.format("<br />%s", new Object[]{expiration}));
            entry.append("</td></tr>");
            html.insertBeforeEnd(licenseList, entry.toString());
        }

        StyleSheet css1 = html.getStyleSheet();
        String backgroundURL1 = Tools.getResource("license/dialog/license_dialog_background.png").toString();
        css1.addRule("body {background-image: url(\'" + backgroundURL1 + "\');}");
        this.setDocument(html);
    }

    public void updateContent() {
        try {
            this.generateHTML();
        } catch (IOException | BadLocationException var2) {
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.tools.dialogs.LicenseContentPanel.generating_html_error");
        }

    }

    public void setLicenses(List<License> licenses) {
        this.licenses = new ArrayList(licenses);
    }

    static {
        try {
            HTML_TEMPLATE = Tools.readTextFile(Tools.getResourceInputStream("license/dialog/license_dialog_template.html"));
        } catch (RepositoryException | IOException var1) {
            HTML_TEMPLATE = "";
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.tools.dialogs.LicenseContentPanel.loading_template_error");
        }

    }
}
