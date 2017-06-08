package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.Process;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.ExtendedMouseClickedAdapter;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.tools.ListHoverHelper;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.template.Template;
import com.rapidminer.tools.XMLException;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

class NewProcessEntryList extends JList<Template> {
    private static final long serialVersionUID = 1L;
    private static final String TEXT_TEMPLATE = "<html><div style=\"font-family: \'Open Sans Semibold\'; font-size: 14; margin-bottom:5px\">%s</div><div style=\"font-family: \'Open Sans Light\'; font-size: 13\">%s</div><html>";
    private final Window owner;

    NewProcessEntryList(List<Template> templates, Window owner) {
        super(templates.toArray(new Template[templates.size()]));
        this.owner = owner;
        this.setupGUI();
    }

    private void setupGUI() {
        this.setLayoutOrientation(2);
        this.setFixedCellWidth(313);
        this.setFixedCellHeight(150);
        this.setVisibleRowCount(-1);
        ListHoverHelper.install(this);
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                boolean hover = index == ListHoverHelper.index(list);
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, hover || isSelected, hover || cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                label.setVerticalAlignment(1);
                Template template = (Template)value;
                label.setText(String.format("<html><div style=\"font-family: \'Open Sans Semibold\'; font-size: 14; margin-bottom:5px\">%s</div><div style=\"font-family: \'Open Sans Light\'; font-size: 13\">%s</div><html>", new Object[]{template.getTitle(), template.getDescription()}));
                JPanel innerPanel = new JPanel(new GridBagLayout());
                innerPanel.setBackground(label.getBackground());
                label.setBackground((Color)null);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(5, 5, 5, 5);
                innerPanel.add(new JLabel(template.getIcon()), gbc);
                ++gbc.gridx;
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.weightx = 1.0D;
                gbc.weighty = 1.0D;
                gbc.fill = 1;
                innerPanel.add(label, gbc);
                if(hover && !isSelected) {
                    innerPanel.setBackground(SwingTools.saturateColor(label.getBackground(), 0.5F));
                }

                if(!cellHasFocus && !hover) {
                    innerPanel.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
                }

                JPanel outerPanel = new JPanel(new GridBagLayout());
                outerPanel.setOpaque(false);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(5, 0, 0, 10);
                gbc.weightx = 1.0D;
                gbc.weighty = 1.0D;
                gbc.fill = 1;
                outerPanel.add(innerPanel, gbc);
                return outerPanel;
            }
        };
        renderer.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Colors.LINKBUTTON_LOCAL));
        this.setCellRenderer(renderer);
        this.setSelectionMode(0);
        this.addMouseListener(new ExtendedMouseClickedAdapter() {
            public void click(MouseEvent e) {
                NewProcessEntryList list = (NewProcessEntryList)e.getSource();
                if(list.locationToIndex(e.getPoint()) == -1 && !e.isShiftDown()) {
                    NewProcessEntryList.this.clearSelection();
                } else {
                    NewProcessEntryList.this.openTemplate();
                }

            }
        });
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 10) {
                    NewProcessEntryList.this.openTemplate();
                }

            }
        });
    }

    public int locationToIndex(Point location) {
        int index = super.locationToIndex(location);
        return index != -1 && !this.getCellBounds(index, index).contains(location)?-1:index;
    }

    private void openTemplate() {
        this.owner.dispose();
        final Template template = (Template)this.getSelectedValue();
        if(template == Template.BLANK_PROCESS_TEMPLATE) {
            GettingStartedDialog.logStats("open_process", "new_process");
            RapidMinerGUI.getMainFrame().newProcess();
        } else {
            if(!RapidMinerGUI.getMainFrame().close()) {
                return;
            }

            (new ProgressThread("open_template") {
                public void run() {
                    try {
                        Process e = template.makeProcess();
                        GettingStartedDialog.logStats("open_template", template.getName());
                        RapidMinerGUI.getMainFrame().setOpenedProcess(e, false, (String)null);
                    } catch (MalformedRepositoryLocationException | IOException | XMLException | RuntimeException var2) {
                        SwingTools.showSimpleErrorMessage("cannot_open_template", var2, new Object[]{template.getTitle(), var2.getMessage()});
                    }

                }
            }).start();
        }

    }
}
