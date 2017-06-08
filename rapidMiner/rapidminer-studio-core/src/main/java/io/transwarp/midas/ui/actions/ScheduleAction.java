package io.transwarp.midas.ui.actions;

import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import io.transwarp.midas.client.MidasClientFactory;

import java.awt.event.ActionEvent;

public class ScheduleAction extends ResourceAction {

    private final MainFrame mainFrame;

    public ScheduleAction(MainFrame mainFrame) {
        super("schedule");
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String json = this.mainFrame.getProcess()
                    .getRootOperator()
                    .getSubprocess(0)
                    .getIEnclosingOperator()
                    .getMidasJson();

            MidasClientFactory.getClientInstance().scheduleJob(json);
        } catch (Exception exception) {
            SwingTools.showVerySimpleErrorMessage("simple_error", exception.getMessage());
        }
    }
}
