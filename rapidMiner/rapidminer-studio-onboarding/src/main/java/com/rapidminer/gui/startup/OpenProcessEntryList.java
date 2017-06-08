package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.ProcessLocation;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.OpenAction;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.tools.ListHoverHelper;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.I18N;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;

class OpenProcessEntryList extends JList<ProcessLocation> {
    private static final long serialVersionUID = 1L;
    private static final int LABEL_WIDTH = 590;
    private static final ImageIcon ICON = SwingTools.createIcon("16/" + I18N.getGUILabel("getting_started.open_recent.icon", new Object[0]));
    private static final Font DIALOG_14 = new Font("Dialog", 0, 14);
    private Window owner;

    OpenProcessEntryList(List<ProcessLocation> processLocations, Window owner) {
        super(processLocations.toArray(new ProcessLocation[processLocations.size()]));
        this.owner = owner;
        this.setupGUI();
    }

    private void setupGUI() {
        this.setLayoutOrientation(0);
        this.setFixedCellHeight(35);
        this.setVisibleRowCount(-1);
        ListHoverHelper.install(this);
        this.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                boolean hover = index == ListHoverHelper.index(list);
                ProcessLocation location = (ProcessLocation)value;
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, hover || isSelected, hover || cellHasFocus);
                label.setFont(OpenProcessEntryList.DIALOG_14);
                label.setText(SwingTools.getStrippedJComponentText(label, location.toMenuString(), 590, 35));
                label.setToolTipText(I18N.getGUILabel("getting_started.open_recent.tip", new Object[]{location.toMenuString()}));
                label.setIcon(OpenProcessEntryList.ICON);
                label.setBorder((Border)null);
                if(hover && !isSelected) {
                    label.setBackground(SwingTools.saturateColor(label.getBackground(), 0.5F));
                }

                if(!cellHasFocus && !hover) {
                    label.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
                }

                return label;
            }
        });
        this.setSelectionMode(0);
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                OpenProcessEntryList.this.openProcess();
            }
        });
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 10) {
                    OpenProcessEntryList.this.openProcess();
                }

            }
        });
    }

    private void openProcess() {
        this.owner.dispose();
        ProcessLocation location = (ProcessLocation)this.getSelectedValue();
        if(RapidMinerGUI.getMainFrame().close()) {
            GettingStartedDialog.logStats("open_process_card", "open_previous");
            OpenAction.open(location, true);
        }
    }
}
