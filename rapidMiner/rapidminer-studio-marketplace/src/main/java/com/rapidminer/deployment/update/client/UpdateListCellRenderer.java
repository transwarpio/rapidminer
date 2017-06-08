package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.AbstractPackageDescriptorListCellRenderer;
import com.rapidminer.deployment.update.client.UpdatePackagesModel;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.plugin.ManagedExtension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.Border;

final class UpdateListCellRenderer extends AbstractPackageDescriptorListCellRenderer {
    private final UpdatePackagesModel updateModel;
    private static String MARKED_FOR_INSTALL_COLOR = "#0066CC";
    private static String MARKED_FOR_UPDATE__COLOR = "#3399FF";
    private static String NOT_INSTALLED_COLOR = "#666666";
    private static String UP_TO_DATE_COLOR = "#006600";
    private static String UPDATES_AVAILABLE_COLOR = "#CC9900";
    private static final Border BOTTOM_BORDER;

    public UpdateListCellRenderer(UpdatePackagesModel updateModel) {
        this.updateModel = updateModel;
    }

    public UpdateListCellRenderer(boolean allPurchased) {
        this.updateModel = null;
    }

    private String getFirstSentence(String text) {
        if(text != null && text.contains(".")) {
            String[] sentences = text.split("\\.");
            return sentences[0].trim() + ".";
        } else {
            return text;
        }
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 6409307403021306689L;

            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                if(d == null) {
                    return d;
                } else {
                    d.width = 10;
                    return d;
                }
            }
        };
        JLabel label = new JLabel();
        panel.setLayout(new FlowLayout(0));
        panel.add(label);
        panel.setOpaque(true);
        if(isSelected && value instanceof PackageDescriptor) {
            panel.setBackground(Colors.TEXT_HIGHLIGHT_BACKGROUND);
        } else {
            panel.setBackground(Colors.TEXTFIELD_BACKGROUND);
        }

        String text = "";
        if(value instanceof PackageDescriptor) {
            PackageDescriptor desc = (PackageDescriptor)value;
            boolean selectedForInstallation = this.updateModel != null?this.updateModel.isSelectedForInstallation(desc):true;
            Icon packageIcon = this.getResizedIcon(this.getIcon(desc));
            text = "<html><body style=\'width: " + (packageIcon != null?300 - packageIcon.getIconWidth():314) + ";" + (packageIcon == null?"margin-left:40px;":"") + "\'>";
            text = text + "<div><strong>" + desc.getName() + "</strong> " + desc.getVersion();
            if(desc.isRestricted()) {
                text = text + "&nbsp;&nbsp;<img src=\'icon:///16/currency_euro.png\' style=\'vertical-align:middle;\'/>";
            }

            text = text + "</div>";
            text = text + "<div>" + this.getFirstSentence(desc.getDescription()) + "</div>";
            ManagedExtension ext = ManagedExtension.get(desc.getPackageId());
            boolean upToDate = false;
            String myVersion;
            if(desc.getPackageTypeName().equals("RAPIDMINER_PLUGIN")) {
                if(ext == null) {
                    if(selectedForInstallation) {
                        text = text + this.getMarkedForInstallationHtml();
                    } else {
                        text = text + this.getNotInstalledHtml();
                    }
                } else {
                    myVersion = ext.getLatestInstalledVersion();
                    if(myVersion != null) {
                        upToDate = myVersion.compareTo(desc.getVersion()) >= 0;
                        if(upToDate) {
                            text = text + this.getUpToDateHtml();
                        } else if(selectedForInstallation) {
                            text = text + this.getMarkedForUpdateHtml();
                        } else {
                            text = text + this.getUpdatesAvailableHtml(ext.getLatestInstalledVersion());
                        }
                    } else if(selectedForInstallation) {
                        text = text + this.getMarkedForInstallationHtml();
                    } else {
                        text = text + this.getNotInstalledHtml();
                    }
                }
            } else if(desc.getPackageTypeName().equals("STAND_ALONE")) {
                myVersion = RapidMiner.getLongVersion();
                upToDate = ManagedExtension.normalizeVersion(myVersion).compareTo(ManagedExtension.normalizeVersion(desc.getVersion())) >= 0;
                if(selectedForInstallation) {
                    text = text + this.getMarkedForUpdateHtml();
                } else if(upToDate) {
                    text = text + this.getUpToDateHtml();
                } else {
                    text = text + this.getUpdatesAvailableHtml(myVersion);
                }
            }

            text = text + "</body></html>";
            label.setIcon(packageIcon);
            label.setVerticalTextPosition(1);
            label.setForeground(Color.BLACK);
            if(index < list.getModel().getSize() - 1) {
                panel.setBorder(BOTTOM_BORDER);
            }
        } else {
            text = "<html><div style=\"width:250px;\">" + value.toString() + "</div></html>";
        }

        label.setText(text);
        return panel;
    }

    private String getMarkedForInstallationHtml() {
        return "<div style=\'" + this.getActionStyle(MARKED_FOR_INSTALL_COLOR) + "\'><img src=\'icon:///16/nav_down.png\'/>&nbsp;" + I18N.getGUILabel("marked.for.installation", new Object[0]) + "</div>";
    }

    private String getUpToDateHtml() {
        return "<div style=\'" + this.getActionStyle(UP_TO_DATE_COLOR) + "\'><img src=\"icon:///16/navigate_check.png\"/>&nbsp;" + I18N.getGUILabel("package.up.to.date", new Object[0]) + "</div>";
    }

    private String getNotInstalledHtml() {
        return "<div style=\'" + this.getActionStyle(NOT_INSTALLED_COLOR) + "\'>" + I18N.getGUILabel("not.installed", new Object[0]) + "</div>";
    }

    private String getMarkedForUpdateHtml() {
        return "<div style=\'" + this.getActionStyle(MARKED_FOR_UPDATE__COLOR) + "\'><img src=\"icon:///16/nav_refresh.png\"/>&nbsp;" + I18N.getGUILabel("marked.for.update", new Object[0]) + "</div>";
    }

    private String getUpdatesAvailableHtml(String installedVersion) {
        return "<div style=\'" + this.getActionStyle(UPDATES_AVAILABLE_COLOR) + "\'><img src=\"icon:///16/nav_up.png\"/>&nbsp;" + I18N.getGUILabel("installed.version", new Object[]{installedVersion}) + "</div>";
    }

    private String getActionStyle(String color) {
        return "height:18px;min-height:18px;line-height:18px;vertical-align:middle;color:" + color + ";margin-top:3px;";
    }

    static {
        BOTTOM_BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.TEXTFIELD_BORDER);
    }
}
