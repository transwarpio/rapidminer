package com.rapidminer.tools.update.internal;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMinerVersion;
import com.rapidminer.deployment.update.client.ExtensionDialog;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.UpdateDialog;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.extensions.IncompatibleExtensionsDialog;
import com.rapidminer.gui.internal.GUIStartupListener;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceMenu;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.plugin.Plugin;
import com.rapidminer.tools.update.internal.UpdateManager;
import com.rapidminer.tools.update.internal.UpdateManagerFactory;
import com.rapidminer.tools.update.internal.UpdateManagerRegistry;
import com.vlsolutions.swing.docking.Dockable;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MarketplaceGUIStartupListener implements GUIStartupListener {
    public MarketplaceGUIStartupListener() {
    }

    public void splashWillBeShown() {
        UpdateManagerRegistry.INSTANCE.register(new UpdateManagerFactory() {
            public UpdateManager create() throws MalformedURLException, URISyntaxException, IOException {
                return new MarketplaceUpdateManager(MarketplaceUpdateManager.getService());
            }
        });
        RapidMiner.registerParameter(new ParameterTypeBoolean("rapidminer.update.incremental", "", true));
        RapidMiner.registerParameter(new ParameterTypeString("rapidminer.update.url", "", "https://marketplace.rapidminer.com/UpdateServer"));
        RapidMiner.registerParameter(new ParameterTypeBoolean("rapidminer.update.to_home", "", true));
    }

    public void mainFrameInitialized(final MainFrame mainFrame) {
        mainFrame.getExtensionsMenu().add(UpdateDialog.UPDATE_ACTION);
        mainFrame.getExtensionsMenu().add(ExtensionDialog.MANAGE_EXTENSIONS);
        List allPlugins = Plugin.getAllPlugins();
        if(allPlugins.size() > 0) {
            ResourceMenu operatorDockable = new ResourceMenu("about_extensions");
            Iterator marketplacePanel = allPlugins.iterator();

            while(marketplacePanel.hasNext()) {
                final Plugin gbc = (Plugin)marketplacePanel.next();
                if(gbc.showAboutBox()) {
                    operatorDockable.add(new ResourceAction("about_extension", new Object[]{gbc.getName()}) {
                        private static final long serialVersionUID = 1L;

                        public void actionPerformed(ActionEvent e) {
                            gbc.createAboutBox(mainFrame).setVisible(true);
                        }
                    });
                }
            }

            mainFrame.getExtensionsMenu().add(operatorDockable);
        }

        Dockable operatorDockable1 = mainFrame.getDockingDesktop().getContext().getDockableByKey("new_operator");
        if(operatorDockable1 != null && JComponent.class.isAssignableFrom(operatorDockable1.getComponent().getClass())) {
            JPanel marketplacePanel1 = new JPanel(new GridBagLayout());
            marketplacePanel1.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.TAB_BORDER));
            GridBagConstraints gbc1 = new GridBagConstraints();
            LinkLocalButton openMarketplace = new LinkLocalButton(new ResourceAction("operators.open_marketplace", new Object[0]) {
                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    UpdateDialog.showUpdateDialog(false, new String[0]);
                }
            });
            gbc1.gridx = 0;
            gbc1.gridy = 0;
            gbc1.anchor = 17;
            marketplacePanel1.add(openMarketplace, gbc1);
            ++gbc1.gridx;
            gbc1.weightx = 1.0D;
            gbc1.fill = 2;
            marketplacePanel1.add(new JLabel(), gbc1);
            JComponent parent = (JComponent)operatorDockable1.getComponent();
            parent.add(marketplacePanel1, "South");
        }

    }

    public void splashWasHidden() {
    }

    public void startupCompleted() {
        if(!(new RapidMinerVersion()).isDevelopmentBuild()) {
            if(!Plugin.getIncompatiblePlugins().isEmpty()) {
                IncompatibleExtensionsDialog dialog = new IncompatibleExtensionsDialog();
                dialog.setVisible(true);
            } else {
                MarketplaceUpdateManager.checkForUpdates();
                MarketplaceUpdateManager.checkForPurchasedNotInstalled();
            }

        }
    }
}
