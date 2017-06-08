package io.transwarp.midas.ui;

import com.rapidminer.Process;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.tools.ParameterService;
import io.transwarp.midas.client.MidasClientFactory;
import io.transwarp.midas.constant.rapidminer.ServiceConf;
import io.transwarp.midas.result.JobStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.rapidminer.RapidMiner.PROPERTY_RAPIDMINER_GENERAL_RAPIDTESTMODE;

public class RemoteResultOverview extends JPanel {

    private static final int HISTORY_LENGTH =
            Integer.parseInt(ParameterService.getParameterValue(ServiceConf.MAX_NUM_OF_PROCESSES()));

    private static final long serialVersionUID = 1L;

    private final ConcurrentLinkedDeque<RemoteResultOverviewItem> processOverviews = new ConcurrentLinkedDeque<>();

    protected final Action CLEAR_HISTORY_ACTION = new ResourceAction("resulthistory.clear_history") {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Iterator<RemoteResultOverviewItem> i = processOverviews.iterator();
            while (i.hasNext()) {
                RemoteResultOverviewItem o = i.next();
                i.remove();
                RemoteResultOverview.this.remove(o);
            }
            MidasClientFactory.getClientInstance().stopJobs();
            RemoteResultOverview.this.repaint();
        }
    };

    protected final Action EXPORT_RESULT = new ResourceAction("resulthistory.export_result") {

        private static final long serialVersionUID = 123L;

        @Override
        public void actionPerformed(ActionEvent e) {
            RemoteResultOverviewItemExport.export(processOverviews);
        }
    };

    private GridBagConstraints gbc;

    public RemoteResultOverview() {
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = HISTORY_LENGTH + 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        // add a filler at the bottom so results start at top
        add(new JLabel(), gbc);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                showContextMenu(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showContextMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showContextMenu(e);
            }

            private void showContextMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu m = new JPopupMenu();
                    m.add(CLEAR_HISTORY_ACTION);
                    if (Boolean.parseBoolean(ParameterService
                            .getParameterValue(PROPERTY_RAPIDMINER_GENERAL_RAPIDTESTMODE)) == true) {
                        m.add(EXPORT_RESULT);
                    }
                    m.show(RemoteResultOverview.this, e.getX(), e.getY());
                }
            }
        });

        // reset y grid
        gbc.gridy = 0;
    }

    public void addStatus(Process process, JobStatus status, String statusMessage) {
        if (process.getProcessState() != Process.PROCESS_STATE_PAUSED
                || "true".equals(ParameterService
                .getParameterValue(RapidMinerGUI.PROPERTY_ADD_BREAKPOINT_RESULTS_TO_HISTORY))) {
            // Return if item is already existed.
            if (status.isFinished()) {
                for (RemoteResultOverviewItem item : processOverviews) {
                    JobStatus itemStatus = item.getStatus();
                    if (itemStatus.equals(status)) {
                        return;
                    }
                }
            }
            // Create a new item.
            final RemoteResultOverviewItem newOverview =
                    new RemoteResultOverviewItem(this, process, status,
                    statusMessage);

            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Swing calls need to be done in EDT to avoid freezing up of the Result Overview
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    processOverviews.add(newOverview);
                    gbc.gridy += 1;
                    add(newOverview, gbc);

                    while (processOverviews.size() > HISTORY_LENGTH) {
                        RemoteResultOverviewItem first = processOverviews.removeFirst();
                        remove(first);
                    }
                }

            });
        }
    }

    public boolean updateStatus(JobStatus status) {
        boolean changed = false;
        boolean finished = false;
        for (RemoteResultOverviewItem item : processOverviews) {
            JobStatus old = item.getStatus();
            if (old.equals(status) && !old.isFinished()) {
                changed = ! old.state().equals(status.state());
                finished = status.isFinished();
                item.updateStatus(status);
            }
        }
        return changed && finished;
    }

    /**
     * Given a job status, show its result;
     *
     * @param status job status;
     */
    public void showStatusResult(JobStatus status) {
        for (RemoteResultOverviewItem item : processOverviews) {
            JobStatus itemStatus = item.getStatus();
            if(itemStatus.session().id() == status.session().id() &&
                    itemStatus.session().timestamp() == status.session().timestamp() &&
                    itemStatus.id() == status.id()) {
                item.pullResult();
                item.showResult();
                return;
            }
        }
    }

    /**
     * Remove the given result from the overview.
     *
     * @param item
     */
    void removeProcessOverview(RemoteResultOverviewItem item) {
        remove(item);
        processOverviews.remove(item);

        revalidate();
        repaint();
    }
}
