package io.transwarp.midas.ui;

import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.internal.GUIStartupListener;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ConfirmDialog;
import com.rapidminer.tools.OperatorService;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.client.MidasClientFactory;
import io.transwarp.midas.result.JobStatus;
import scala.Function1;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

import javax.swing.*;
import java.awt.*;

public class StartupSessionListener implements GUIStartupListener {
    @Override
    public void splashWillBeShown() {

    }

    @Override
    public void mainFrameInitialized(MainFrame mainFrame) {

    }

    @Override
    public void splashWasHidden() {

    }

    @Override
    public void startupCompleted() {
        // Build both client and sharable client.
        if (OperatorService.isRemoteMode()) {
            addCallback(MidasClientFactory.getClientInstance());
            // Restore remote result.
            RapidMinerGUI.getMainFrame().getRemoteResultDisplay().pullResults();
        }
    }

    /**
     * Build a midas client, then bind session file
     * and callbacks.
     *
     * @param client a midas client need to be constructed,
     *               could be a normal client and a sharable
     *               client;
     */
    private void addCallback(MidasClient client) {
        final RemoteResultDisplay display = RapidMinerGUI.getMainFrame().getRemoteResultDisplay();
        // add callbacks
        Function1<JobStatus, BoxedUnit> fn = new AbstractFunction1<JobStatus, BoxedUnit>() {
            @Override
            public BoxedUnit apply(JobStatus v1) {
                final RemoteResultOverview overview = display.getOverview();
                final JobStatus status = v1;
                boolean msg = overview.updateStatus(status);
                if (msg) {

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            // Scroll to the bottom when job finished.
                            Rectangle visibleRect = overview.getVisibleRect();
                            visibleRect.y = overview.getHeight() - visibleRect.height;
                            overview.scrollRectToVisible(visibleRect);

                            if (!OperatorService.isRapidGUITestMode()) {
                                // User should be toggled to result page when click `ok` in finish panel.
                                switch(SwingTools.showConfirmDialog("job_finished",
                                        ConfirmDialog.OK_CANCEL_OPTION, status.session().id(), status.id())) {

                                    case ConfirmDialog.OK_OPTION:
                                        overview.showStatusResult(status);
                                        break;
                                }
                            }
                        }
                    });
                }
                return BoxedUnit.UNIT;
            }
        };
        client.addCompleteCallback(fn);
    }
}
