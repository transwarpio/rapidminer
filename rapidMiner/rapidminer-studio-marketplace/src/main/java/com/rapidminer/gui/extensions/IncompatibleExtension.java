package com.rapidminer.gui.extensions;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.gui.tools.VersionNumber;
import com.rapidminer.gui.tools.VersionNumber.VersionNumberExcpetion;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.PlatformUtilities;
import com.rapidminer.tools.plugin.ManagedExtension;
import com.rapidminer.tools.plugin.Plugin;
import com.rapidminer.tools.update.internal.UpdateManagerRegistry;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

public class IncompatibleExtension extends Observable {
    private final Plugin plugin;
    private final boolean managed;
    private IncompatibleExtension.Fix fix;
    private List<IncompatibleExtension.Fix> availableFixes;

    public IncompatibleExtension(Plugin plugin) {
        this.plugin = plugin;
        this.managed = ManagedExtension.get(plugin.getExtensionId()) != null;
        this.fix = IncompatibleExtension.Fix.IGNORE;
        this.availableFixes = Collections.emptyList();
    }

    public void updateFixes() throws IOException, URISyntaxException {
        String currentVersionString = this.getPlugin().getVersion();
        String latestVersionString = null;
        latestVersionString = UpdateManagerRegistry.INSTANCE.get().getLatestVersion(this.getId(), "ANY", PlatformUtilities.getReleaseVersion());
        VersionNumber currentVersion = null;
        VersionNumber latestVersion = null;
        if(currentVersionString != null && latestVersionString != null) {
            try {
                currentVersion = new VersionNumber(currentVersionString);
                latestVersion = new VersionNumber(latestVersionString);
            } catch (VersionNumberExcpetion var6) {
                ;
            }
        }

        if(currentVersion != null && latestVersion != null && latestVersion.isAbove(currentVersion)) {
            if(this.isManaged()) {
                this.availableFixes = Arrays.asList(new IncompatibleExtension.Fix[]{IncompatibleExtension.Fix.UPDATE, IncompatibleExtension.Fix.REMOVE, IncompatibleExtension.Fix.IGNORE});
            } else {
                this.availableFixes = Arrays.asList(new IncompatibleExtension.Fix[]{IncompatibleExtension.Fix.UPDATE, IncompatibleExtension.Fix.IGNORE});
            }
        } else if(this.isManaged()) {
            this.availableFixes = Arrays.asList(new IncompatibleExtension.Fix[]{IncompatibleExtension.Fix.REMOVE, IncompatibleExtension.Fix.IGNORE});
        } else {
            this.availableFixes = Arrays.asList(new IncompatibleExtension.Fix[]{IncompatibleExtension.Fix.IGNORE});
        }

        this.fix = (IncompatibleExtension.Fix)this.availableFixes.get(0);
        this.setChanged();
        this.notifyObservers(IncompatibleExtension.ExtensionEvent.CHECK_COMPLETED);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String getId() {
        return this.plugin.getExtensionId();
    }

    public boolean isManaged() {
        return this.managed;
    }

    public IncompatibleExtension.Fix getSelectedFix() {
        return this.fix;
    }

    public List<IncompatibleExtension.Fix> getAvailableFixes() {
        return Collections.unmodifiableList(this.availableFixes);
    }

    public void setFix(IncompatibleExtension.Fix fix) {
        if(this.fix != fix) {
            this.fix = fix;
            this.setChanged();
            this.notifyObservers(IncompatibleExtension.ExtensionEvent.FIX_CHANGED);
        }

    }

    public static enum ExtensionEvent {
        CHECK_COMPLETED,
        FIX_CHANGED;

        private ExtensionEvent() {
        }
    }

    public static enum Fix {
        UPDATE("update"),
        REMOVE("remove"),
        IGNORE("ignore");

        private final String action;

        private Fix(String key) {
            this.action = I18N.getGUILabel("incompatible_extension.action." + key, new Object[0]);
        }

        public String toString() {
            return this.action;
        }
    }
}