package com.rapidminer.extension.jdbc.gui.tools.dialogs;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.gui.tools.dialogs.SQLQueryBuilder;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeSQLQuery;
import com.rapidminer.extension.jdbc.tools.jdbc.TableMetaDataCache;
import com.rapidminer.gui.properties.PropertyDialog;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.tools.I18N;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.JButton;

public class SQLQueryPropertyDialog extends PropertyDialog {
    private static final long serialVersionUID = -5224113818406394872L;
    private JButton resizeButton;
    private JButton clearMetaDataCacheButton;

    public SQLQueryPropertyDialog(ParameterTypeSQLQuery type, final SQLQueryBuilder queryBuilder) {
        super(type, "sql");
        ResourceAction resizeAction = new ResourceAction(false, "text_dialog.enlarge", new Object[0]) {
            private static final long serialVersionUID = 8857840715142145951L;

            public void actionPerformed(ActionEvent event) {
                JButton button = (JButton)event.getSource();
                Point relativeLocation = button.getLocationOnScreen();
                GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] devices = e.getScreenDevices();
                Rectangle displayBounds = null;
                GraphicsDevice[] screenDim = devices;
                int dim = devices.length;

                for(int currentSize = 0; currentSize < dim; ++currentSize) {
                    GraphicsDevice y = screenDim[currentSize];
                    GraphicsConfiguration[] x = y.getConfigurations();
                    GraphicsConfiguration[] var12 = x;
                    int var13 = x.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        GraphicsConfiguration config = var12[var14];
                        Rectangle gcBounds = config.getBounds();
                        if(gcBounds.contains(relativeLocation)) {
                            displayBounds = gcBounds;
                        }
                    }
                }

                Dimension var17;
                if(displayBounds != null) {
                    var17 = new Dimension((int)displayBounds.getWidth(), (int)displayBounds.getHeight());
                } else {
                    var17 = Toolkit.getDefaultToolkit().getScreenSize();
                }

                Dimension var18 = new Dimension((int)((double)var17.width * 0.9D), (int)((double)var17.height * 0.9D));
                Dimension var19 = SQLQueryPropertyDialog.this.getSize();
                if(var19.getHeight() != var18.getHeight() && var19.getWidth() != var18.getWidth()) {
                    SQLQueryPropertyDialog.this.setSize(var18);
                    if(displayBounds != null) {
                        int var21 = displayBounds.y + (var17.height - var18.height) / 2;
                        int var20 = displayBounds.x + (var17.width - var18.width) / 2;
                        SQLQueryPropertyDialog.this.setLocation(var20, var21);
                    } else {
                        SQLQueryPropertyDialog.this.setLocationRelativeTo((Component)null);
                    }

                    SQLQueryPropertyDialog.this.resizeButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.shrink.label", new Object[0]));
                    SQLQueryPropertyDialog.this.resizeButton.setToolTipText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.shrink.tip", new Object[0]));
                    SQLQueryPropertyDialog.this.resizeButton.setMnemonic(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.shrink.mne", new Object[0]).charAt(0));
                } else {
                    SQLQueryPropertyDialog.this.setSize(SQLQueryPropertyDialog.this.getDefaultSize(9));
                    SQLQueryPropertyDialog.this.setDefaultLocation();
                    SQLQueryPropertyDialog.this.resizeButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.enlarge.label", new Object[0]));
                    SQLQueryPropertyDialog.this.resizeButton.setToolTipText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.enlarge.tip", new Object[0]));
                    SQLQueryPropertyDialog.this.resizeButton.setMnemonic(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.enlarge.mne", new Object[0]).charAt(0));
                }

            }
        };
        this.resizeButton = new JButton(resizeAction);
        ResourceAction clearMetaDataCacheAction = new ResourceAction("clear_db_cache", new Object[0]) {
            private static final long serialVersionUID = 8510147303889637712L;

            public void actionPerformed(ActionEvent e) {
                ProgressThread t = new ProgressThread("db_clear_cache") {
                    public void run() {
                        TableMetaDataCache.getInstance().clearCache();
                        queryBuilder.updateAll();
                    }
                };
                t.start();
            }
        };
        this.clearMetaDataCacheButton = new JButton(clearMetaDataCacheAction);
        this.layoutDefault(queryBuilder.makeQueryBuilderPanel(), 9, new AbstractButton[]{this.clearMetaDataCacheButton, this.resizeButton, this.makeOkButton(), this.makeCancelButton()});
    }
}
