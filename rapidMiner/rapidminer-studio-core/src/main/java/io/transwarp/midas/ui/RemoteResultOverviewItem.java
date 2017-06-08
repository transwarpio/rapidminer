package io.transwarp.midas.ui;

import com.rapidminer.Process;
import com.rapidminer.ProcessLocation;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.ExtendedMouseClickedAdapter;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.look.RapidLookAndFeel;
import com.rapidminer.gui.look.borders.TextFieldBorder;
import com.rapidminer.gui.look.ui.ExtensionButtonUI;
import com.rapidminer.gui.processeditor.results.SingleResultOverview;
import com.rapidminer.gui.renderer.RendererService;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.ResultObject;
import com.rapidminer.operator.SimpleResultObject;
import io.transwarp.midas.adaptor.IIOObject;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.client.MidasClientFactory;
import io.transwarp.midas.client.MidasHTTPException;
import io.transwarp.midas.result.JobStatus;
import io.transwarp.midas.result.ResultHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static io.transwarp.midas.result.JobStatus.*;

public class RemoteResultOverviewItem extends JPanel {

    private static final int MAX_PROCESS_NAME_LENGTH = 60;

    /** arrow icon with an arrow pointing up */
    private static final ImageIcon ICON_ARROW_UP = SwingTools.createIcon("16/" + "navigate_up.png");

    /** arrow icon with an arrow pointing down */
    private static final ImageIcon ICON_ARROW_DOWN = SwingTools.createIcon("16/" + "navigate_down.png");

    private static final long serialVersionUID = 1L;

    private final Action RESTORE_PROCESS = new ResourceAction(true, "resulthistory.restore_process") {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (RapidMinerGUI.getMainFrame().close()) {
                    Process process = new Process(RemoteResultOverviewItem.this.process);
                    process.setProcessLocation(processLocation);
                    RapidMinerGUI.getMainFrame().setProcess(process, true);
                }
            } catch (Exception e1) {
                SwingTools.showSimpleErrorMessage("cannot_restore_history_process", e1);
            }
        }
    };

    private final Action REMOVE_FROM_HISTORY = new ResourceAction(true, "resulthistory.remove") {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            removeResult();
        }
    };

    private final Action REFRESH_STATUS = new ResourceAction(true, "resulthistory.remote_refresh") {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            performRefresh();
            showResult();
        }
    };

    private final ExtendedMouseClickedAdapter MOUSE_CLICK = new ExtendedMouseClickedAdapter() {
        @Override
        public void click(MouseEvent e) {
            performRefresh();
            setExpanded(!expanded);
        }

        @Override
        public void showContextMenu(Point point) {
            JPopupMenu menu = new JPopupMenu();
            menu.add(RESTORE_PROCESS);
            menu.add(REMOVE_FROM_HISTORY);
            menu.add(REFRESH_STATUS);
            menu.addSeparator();
            menu.add(RemoteResultOverviewItem.this.parent.CLEAR_HISTORY_ACTION);
            menu.show(RemoteResultOverviewItem.this, (int) point.getX(), (int) point.getY());
        }
    };

    private final JButton removeButton = new JButton(REMOVE_FROM_HISTORY);
    private final JButton restoreButton = new JButton(RESTORE_PROCESS);
    private final JButton refreshButton = new JButton(REFRESH_STATUS);

    private MidasClient client;
    private JobStatus status;
    private final String process;

    public String getProcessName() {
        return processName;
    }

    private String processName;
    private String prunedProcessName;
    private Process inputProcess;

    private final ProcessLocation processLocation;

    private boolean expanded = false;

    private final List<SingleResultOverview> results = new LinkedList<>();

    private final Collection<IOObject> resultObjects = new ArrayList<>();

    private final RemoteResultOverview parent;

    private final JLabel labelExp;

    private final JLabel headerLabel;

    private JPanel resultPanel;

    public RemoteResultOverviewItem(final RemoteResultOverview parent, Process process,
                                    final JobStatus status, String statusMessage) {
        this.parent = parent;
        this.client = MidasClientFactory.getClientInstance();
        this.status = status;
        this.process = process.getRootOperator().getXML(false);
        this.inputProcess = process;
        this.processLocation = process.getProcessLocation();

        setOpaque(false);
        setBackground(Colors.WHITE);
        setBorder(new TextFieldBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(true);
        mainPanel.setBackground(null);
        labelExp = new JLabel(ICON_ARROW_DOWN, SwingConstants.RIGHT);
        labelExp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        labelExp.setOpaque(true);
        labelExp.setBackground(null);
        add(labelExp, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        processName = process.getProcessLocation() == null ?
                process.getRootOperator().getName() :
                process.getProcessLocation().getShortName();

        prunedProcessName = processName.length() > 20 ?
                processName.substring(0, 13) + "..." : processName;

        processName += "(" + status.id() + ")";
        prunedProcessName += "(" + status.id() + ")";

        String headerText = getHeaderText(processName, status);

        headerLabel = new JLabel(headerText);
        headerLabel.setFont(headerLabel.getFont().deriveFont(14f));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(headerLabel, gbc);

        restoreButton.setText(null);
        restoreButton.setContentAreaFilled(false);
        restoreButton.setUI(new ExtensionButtonUI());
        gbc.gridx += 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(restoreButton, gbc);

        removeButton.setText(null);
        removeButton.setContentAreaFilled(false);
        removeButton.setUI(new ExtensionButtonUI());
        gbc.gridx += 1;
        mainPanel.add(removeButton, gbc);

        refreshButton.setText(null);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setUI(new ExtensionButtonUI());
        gbc.gridx += 1;
        mainPanel.add(refreshButton, gbc);

        resultPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        resultPanel.setOpaque(true);
        resultPanel.setBackground(null);
        resultPanel.setCursor(Cursor.getDefaultCursor());
        gbc.gridx = 0;
        gbc.gridy += 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 3;
        mainPanel.add(resultPanel, gbc);

        addMouseListener(MOUSE_CLICK);
    }

    public void setResult(Collection<IOObject> results) {
        int i = 0;
        for (IOObject result : results) {
            String resultName = result instanceof ExampleSet ?
                    ((ResultObject) result).getName() :
                    RendererService.getName(result.getClass());
            String tabName = prunedProcessName + " - " + resultName;
            SingleResultOverview singleOverview = new SingleResultOverview(
                    result, inputProcess, tabName, i);
            singleOverview.setRemote(true);
            this.results.add(singleOverview);
            i++;
        }
    }

    public void updateStatus(JobStatus status) {
        checkStatus(status);
        this.status = status;
        String text = getHeaderText(processName, status);
        headerLabel.setText(text);
    }

    public JobStatus getStatus() {
        return this.status;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Colors.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RapidLookAndFeel.CORNER_DEFAULT_RADIUS,
                RapidLookAndFeel.CORNER_DEFAULT_RADIUS);

        g2.dispose();
    }

    /**
     * Pull result and show in-item or full result.
     */
    void pullResult() {
        if (status.isDone()) return;
        // Pull the result if status is finished.
        try {
            if (status.isSucceeded()) {
                ResultHolder resultHolder = client.getJobResult(status);
                for (IIOObject obj : resultHolder.getResults()) {
                    resultObjects.add((IOObject) obj);
                }
                status.setDone();
            } else if (status.isFailed() || status.error() != null) {
                resultObjects.add(makeErrorInfo(status.error()));
                status.setDone();
            } else {
                SwingTools.showMessageDialog("refreshed", status.session().id(), status.id());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultObjects.add(makeErrorInfo(e.toString()));
        } finally {
            setResult(resultObjects);
        }
    }

    /**
     * Show result in a new tab.
     */
    void showResult() {
        if (resultObjects == null) return;
        int i = 1;
        for(IOObject result: resultObjects) {
            if(result instanceof ResultObject) {
                String resultName = result instanceof ExampleSet ?
                        ((ResultObject) result).getName() :
                        RendererService.getName(result.getClass());
                String tabName = prunedProcessName + " - " + resultName;
                RapidMinerGUI.getMainFrame()
                        .getRemoteResultDisplay()
                        .showResultWithName((ResultObject) result, tabName);
                i ++;
            }
        }
    }


    // Private methods

    /**
     * Check http connection, unsuccessful try toggles an error dialog.
     *
     * @return false if cannot connect to server;
     */
    private boolean checkConnection() {
        if (!status.isDone()) {
            try {
                client.getSessionState(status.session());
            } catch (Exception error) {
                SwingTools.showVerySimpleErrorMessage(error.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Check if status matches specified id.
     *
     * @param status specified job;
     */
    private void checkStatus(JobStatus status) {
        if (this.status.session().id() != status.session().id()) {
            throw new RuntimeException("can't update job status with different session");
        }
        if (this.status.id() != status.id()) {
            throw new RuntimeException("can't update job status with different id");
        }
    }

    /**
     * Give a color to a job status respect to its job state.
     *
     * @param status a JobStatus;
     * @return color string respect to job status;
     */
    private String getStatusColor(JobStatus status) {
        String Gold = "#FFD700";
        String LightCoral = "#F08080";
        String LightStateGray = "#778899";
        String MediumSeaGreen = "#3CB371";

        if (status.isSent()) {
            return LightStateGray;
        }
        if (status.isSucceeded()) {
            return MediumSeaGreen;
        }
        if (status.isFailed()) {
            return LightCoral;
        }
        return Gold;
    }

    /**
     * Create a colored status tag which could be shown in HTML.
     *
     * @param status a JobStatus;
     * @return a colored job status tag in HTML format;
     */
    private String getColoredStatus(JobStatus status) {
        return new StringBuilder("<strong><font color=\"")
                .append(getStatusColor(status))
                .append("\">")
                .append(status.state())
                .append("</font></strong>")
                .toString();
    }

    /**
     * Get header text, this method might be used
     * in initializing or updating header status.
     *
     * @param process the process name would be displayed;
     * @param status the job status would be displayed;
     * @return a String of new header text;
     */
    private String getHeaderText(String process, JobStatus status) {
        StringBuilder b = new StringBuilder();
        b.append("<html><strong>");
        b.append(SwingTools.getShortenedDisplayName(process, MAX_PROCESS_NAME_LENGTH));
        b.append("</strong>");
        b.append("<br/> state: ").append(getColoredStatus(status));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = null;
        Date end = null;
        if (status.startTime() > 0) {
            b.append("<br/> start: ");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(status.startTime());
            start = c.getTime();
            b.append(format.format(start));
        }
        if (status.endTime() > 0) {
            b.append("<br/> end: ");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(status.endTime());
            end = c.getTime();
            b.append(format.format(end));
        }

        if (start != null && end != null) {
            b.append("<br/> duration: ");
            long d = status.endTime() - status.startTime();
            long seconds = d / 1000;
            long mins = seconds / 60;
            long hours = mins / 60;
            String s = String.format("%d:%02d:%02d", hours, mins % 60, seconds % 60);
            b.append(s);
        }
        b.append("<br/> session: ").append(status.session().id());
        b.append("</html>");

        return b.toString();
    }

    /**
     * Create an error IOObject based on a String.
     *
     * @param error error String;
     * @return error IOObject;
     */
    private IOObject makeErrorInfo(String error) {
        return new SimpleResultObject("ERROR", error);
    }

    /**
     * This is a combo of checking connection, updating status,
     * pulling result and showing result.
     */
    private void performRefresh() {
        if (!checkConnection()) return;
        try {
            refreshStatus();
            pullResult();
        } catch (MidasHTTPException error) {
            SwingTools.showVerySimpleErrorMessage("http_error", error.statusCode(), error.getMessage());
        }
    }

    /**
     * Removes and adds the single result blocks according to the current width.
     */
    private void redoLayout() {
        resultPanel.removeAll();
        if (expanded) {
            int curWidth = parent.getSize().width;
            int relevantWidth = SingleResultOverview.MIN_WIDTH + 25;
            int xCount = curWidth / relevantWidth;
            int yCount = (int) Math.ceil((double) results.size() / xCount);
            resultPanel.setLayout(new GridLayout(yCount, xCount));
            for (SingleResultOverview overview : results) {
                resultPanel.add(overview);
            }
        }
        revalidate();
    }

    /**
     * Refresh local status.
     */
    private void refreshStatus() throws MidasHTTPException {
        // Pull the status if not finished.
        if (!status.isDone()) {
            status = client.updateJobStatus(status);
            String text = getHeaderText(processName, status);
            headerLabel.setText(text);
        }
    }

    /**
     * Remove current item.
     */
    private void removeResult() {
        try {
            client.stopJob(status);
        } catch (Exception e) {
            // Temporarily do nothing with exception.
        }
        parent.removeProcessOverview(RemoteResultOverviewItem.this);
    }

    /**
     * Toggle the expansion state.
     *
     * @param expanded
     */
    private void setExpanded(boolean expanded) {
        if (!status.isFinished()) return;
        if (expanded != this.expanded) {
            this.expanded = expanded;
            if (expanded) {
                labelExp.setIcon(ICON_ARROW_UP);
            } else {
                labelExp.setIcon(ICON_ARROW_DOWN);
            }

            redoLayout();
        }
    }
}
