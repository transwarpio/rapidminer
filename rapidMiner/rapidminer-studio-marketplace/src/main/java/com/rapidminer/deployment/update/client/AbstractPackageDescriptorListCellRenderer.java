package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.ListCellRenderer;

public abstract class AbstractPackageDescriptorListCellRenderer implements ListCellRenderer {
    private static RenderingHints HI_QUALITY_HINTS = new RenderingHints((Map)null);
    private Map<String, Icon> icons = new HashMap();

    public AbstractPackageDescriptorListCellRenderer() {
    }

    protected Icon getIcon(PackageDescriptor pd) {
        if(pd.getIcon() == null) {
            return null;
        } else {
            Icon result = this.icons.get(pd.getPackageId());
            if(result == null) {
                result = new ImageIcon(pd.getIcon());
                this.icons.put(pd.getPackageId(), result);
            }

            return (Icon)result;
        }
    }

    protected Icon getResizedIcon(Icon originalIcon) {
        if(originalIcon == null) {
            return null;
        } else {
            int width = originalIcon.getIconWidth();
            int height = originalIcon.getIconHeight();
            if(width != 48) {
                double scale = 48.0D / (double)width;
                BufferedImage bi = new BufferedImage((int)(scale * (double)width), (int)(scale * (double)height), 2);
                Graphics2D g = bi.createGraphics();
                g.setRenderingHints(HI_QUALITY_HINTS);
                g.scale(scale, scale);
                originalIcon.paintIcon((Component)null, g, 0, 0);
                g.dispose();
                return new ImageIcon(bi);
            } else {
                return originalIcon;
            }
        }
    }

    static {
        HI_QUALITY_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        HI_QUALITY_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        HI_QUALITY_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
