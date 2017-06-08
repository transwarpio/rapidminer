package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.AbstractPackageDescriptorListCellRenderer;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.tools.I18N;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class PackageListCellRenderer extends AbstractPackageDescriptorListCellRenderer {
    private int textPixelSize = 0;
    private HashMap<PackageDescriptor, HashSet<PackageDescriptor>> dependecyMap = null;

    public PackageListCellRenderer(HashMap<PackageDescriptor, HashSet<PackageDescriptor>> dependecyMap) {
        this.dependecyMap = dependecyMap;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 1L;

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
            Icon packageIcon = this.getResizedIcon(this.getIcon(desc));
            text = "<html><body style=\'width: " + (packageIcon != null?300 - packageIcon.getIconWidth():314) + (this.textPixelSize > 0?";font-size:":"") + this.textPixelSize + "px;" + (packageIcon == null?"margin-left:40px;":"") + "\'>";
            text = text + "<div><strong>" + desc.getName() + "</strong> " + desc.getVersion();
            if(desc.isRestricted()) {
                text = text + "&nbsp;&nbsp;<img src=\'icon:///16/currency_euro.png\' style=\'vertical-align:middle;\'/>";
            }

            text = text + "</div>";
            text = text + "<div style=\'margin-top:5px;\'>" + this.getLicenseType(desc.getLicenseName()) + "</div>";
            if(this.dependecyMap != null && ((HashSet)this.dependecyMap.get(desc)).size() > 0) {
                text = text + "<div style=\'margin-top:5px;\'>" + this.getSourcePackages(desc) + "</div>";
            }

            text = text + "</body></html>";
            label.setIcon(packageIcon);
            label.setVerticalTextPosition(1);
            label.setForeground(Colors.TEXT_HIGHLIGHT_FOREGROUND);
            label.setText(text);
        }

        return panel;
    }

    private String getSourcePackages(PackageDescriptor desc) {
        StringBuffer text = new StringBuffer("");
        boolean first = true;

        PackageDescriptor dep;
        for(Iterator var4 = ((HashSet)this.dependecyMap.get(desc)).iterator(); var4.hasNext(); text.append(dep.getName())) {
            dep = (PackageDescriptor)var4.next();
            if(!first) {
                text.append(", ");
            } else {
                first = false;
            }
        }

        return I18N.getMessage(I18N.getGUIBundle(), "gui.label.required_by", new Object[]{text.toString()});
    }

    private String getLicenseType(String licenseName) {
        return I18N.getMessage(I18N.getGUIBundle(), "gui.label.license_type", new Object[]{licenseName});
    }
}
