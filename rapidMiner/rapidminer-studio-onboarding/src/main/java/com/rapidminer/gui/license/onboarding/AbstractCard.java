package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.tools.dialogs.ConfirmDialog;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseManagerRegistry;
import java.util.Iterator;
import javax.swing.JPanel;

public abstract class AbstractCard {
    private String cardId;
    private OnboardingDialog onboardingDialog;
    protected Runnable standardCloseAction = new Runnable() {
        public void run() {
            AbstractCard.this.getContainer().dispose();
        }
    };
    protected Runnable standardAskAndCloseAction = new Runnable() {
        public void run() {
            if(AbstractCard.this.isAtLeastCommunityEdition()) {
                AbstractCard.this.getContainer().dispose();
            } else {
                int answer = ConfirmDialog.showConfirmDialogWithOptionalCheckbox(ApplicationFrame.getApplicationFrame(), "free_edition", 0, (String)null, 0, false, new Object[0]);
                if(answer == 0) {
                    AbstractCard.this.getContainer().dispose();
                }
            }

        }
    };

    public AbstractCard(String cardId, OnboardingDialog onboardingDialog) {
        this.cardId = cardId;
        this.onboardingDialog = onboardingDialog;
    }

    public String getId() {
        return this.cardId;
    }

    public void showCard() {
    }

    public abstract JPanel getHeader();

    public abstract JPanel getContent();

    public abstract Runnable getCloseAction();

    protected OnboardingDialog getContainer() {
        return this.onboardingDialog;
    }

    private boolean isAtLeastCommunityEdition() {
        Iterator var1 = LicenseManagerRegistry.INSTANCE.get().getAllActiveLicenses().iterator();

        License l;
        do {
            if(!var1.hasNext()) {
                return false;
            }

            l = (License)var1.next();
        } while(!"rapidminer-studio".equals(l.getProductId()) || 20 > l.getPrecedence());

        return true;
    }
}
