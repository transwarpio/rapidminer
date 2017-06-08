package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.UpdatePackagesModel;
import com.rapidminer.gui.tools.PasswordDialog;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.tools.GlobalAuthenticator;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.PasswordInputCanceledException;
import com.rapidminer.tools.GlobalAuthenticator.URLAuthenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.logging.Level;

public class UpdateServerAccount extends Observable {
    private static PasswordAuthentication upateServerPA = null;
    private boolean loggedIn = false;

    public UpdateServerAccount() {
    }

    public void forceNotifyObservers() {
        this.setChanged();
        this.notifyObservers();
    }

    public static void setPasswordAuthentication(PasswordAuthentication pa) {
        upateServerPA = pa;
    }

    public void login(UpdatePackagesModel updateModel) {
        this.login(updateModel, false);
    }

    public void login(UpdatePackagesModel updateModel, boolean showInForeground) {
        this.login(updateModel, showInForeground, (Runnable)null, (Runnable)null);
    }

    public void login(final UpdatePackagesModel updateModel, final boolean showInForeground, final Runnable successCallback, final Runnable failCallback) {
        ProgressThread loginProgressThread = new ProgressThread("log_in_to_updateserver", showInForeground) {
            public void run() {
                try {
                    while(!UpdateServerAccount.this.loggedIn) {
                        MarketplaceUpdateManager.clearAccountSerive();
                        boolean e = true;
                        PasswordAuthentication pa = null;

                        try {
                            pa = PasswordDialog.getPasswordAuthentication("Marketplace", MarketplaceUpdateManager.getUpdateServerURI("").toString(), false, false, "authentication.marketplace", new Object[0]);
                        } catch (PasswordInputCanceledException var5) {
                            e = false;
                        }

                        e &= pa != null;
                        if(e) {
                            UpdateServerAccount.upateServerPA = pa;
                            this.getProgressListener().setCompleted(10);

                            try {
                                MarketplaceUpdateManager.getAccountService();
                            } catch (Exception var4) {
                                LogService.getRoot().log(Level.INFO, "Failed to login: " + var4.getLocalizedMessage());
                                continue;
                            }

                            this.getProgressListener().setCompleted(50);
                            UpdateServerAccount.this.loggedIn = true;
                            updateModel.updatePurchasedPackages();
                            this.getProgressListener().setCompleted(90);
                            UpdateServerAccount.this.setChanged();
                            UpdateServerAccount.this.notifyObservers((Object)null);
                            this.getProgressListener().setCompleted(100);
                            if(successCallback != null) {
                                successCallback.run();
                            }

                            return;
                        }

                        UpdateServerAccount.upateServerPA = null;
                        UpdateServerAccount.this.setChanged();
                        UpdateServerAccount.this.notifyObservers((Object)null);
                        if(failCallback != null) {
                            failCallback.run();
                        }

                        return;
                    }

                } catch (URISyntaxException var6) {
                    if(failCallback != null) {
                        failCallback.run();
                    }

                }
            }
        };
        loginProgressThread.start();
    }

    public void logout(final UpdatePackagesModel updateModel) {
        (new ProgressThread("log_out_frm_updateserver", false) {
            public void run() {
                MarketplaceUpdateManager.clearAccountSerive();
                UpdateServerAccount.upateServerPA = null;
                UpdateServerAccount.this.loggedIn = false;
                updateModel.clearPurchasedPackages();
                UpdateServerAccount.this.setChanged();
                UpdateServerAccount.this.notifyObservers((Object)null);
            }
        }).start();
    }

    public void updatePurchasedPackages(final UpdatePackagesModel updateModel) {
        (new ProgressThread("fetching_updates", false) {
            public void run() {
                this.getProgressListener().setCompleted(10);
                updateModel.updatePurchasedPackages();
                this.getProgressListener().setCompleted(75);
                UpdateServerAccount.this.setChanged();
                UpdateServerAccount.this.notifyObservers((Object)null);
                this.getProgressListener().setCompleted(100);
            }
        }).start();
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public String getUserName() {
        return upateServerPA != null?upateServerPA.getUserName():null;
    }

    public char[] getPassword() {
        return upateServerPA.getPassword();
    }

    static {
        GlobalAuthenticator.registerServerAuthenticator(new URLAuthenticator() {
            public PasswordAuthentication getAuthentication(URL url) {
                try {
                    return url.toString().startsWith(MarketplaceUpdateManager.getUpdateServerURI("").toString())?(UpdateServerAccount.upateServerPA != null?UpdateServerAccount.upateServerPA:new PasswordAuthentication("", new char[0])):null;
                } catch (URISyntaxException var3) {
                    return null;
                }
            }

            public String getName() {
                return "UpdateService authenticator.";
            }

            public String toString() {
                return this.getName();
            }
        });
    }
}
