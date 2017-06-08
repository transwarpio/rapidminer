package io.transwarp.midas.ui.dialog;

import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.tools.*;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.FileSystemService;
import io.transwarp.midas.client.CustomOpManager;
import io.transwarp.midas.client.CustomOpProperty;
import io.transwarp.midas.constant.midas.params.CustomParams;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ManageCustomOpDialog extends ButtonDialog {
    private static final long serialVersionUID = 1L;
    private final JList<CustomOpProperty> ops;
    private final CustomOpPane customOpPane = new CustomOpPane();
    private final AbstractButton deleteButton = new JButton(new ResourceAction("manage_custom_op.delete", new Object[0]) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            CustomOpProperty props = ManageCustomOpDialog.this.customOpPane.property;
            if(props != null) {
                ((DefaultListModel)ManageCustomOpDialog.this.ops.getModel()).removeElement(props);
                CustomOpManager.deleteOp(props);
            }

        }
    });

    public ManageCustomOpDialog() {
        super(ApplicationFrame.getApplicationFrame(), "manage_custom_op", ModalityType.APPLICATION_MODAL, new Object[0]);
        JPanel main = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = c.ipady = 5;
        c.insets = new Insets(5, 5, 5, 5);
        DefaultListModel model = new DefaultListModel();
        scala.collection.Iterator<CustomOpProperty> iter = CustomOpManager.getOps().toIterator();
        while (iter.hasNext()) {
            CustomOpProperty p = iter.next();
            model.addElement(p);
        }
        this.ops = new JList(model);
        this.ops.setSelectionMode(0);
        this.ops.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                CustomOpProperty selected = ManageCustomOpDialog.this.ops.getSelectedValue();
                ManageCustomOpDialog.this.customOpPane.setProperty(selected);
            }
        });

        c.anchor = 23;
        c.weightx = 0.5D;
        c.weighty = 1.0D;
        c.fill = 1;
        c.gridheight = 1;
        c.gridwidth = -1;
        ExtendedJScrollPane sp1 = new ExtendedJScrollPane(this.ops);
        sp1.setBorder(BorderFactory.createLineBorder(Colors.TEXTFIELD_BORDER));
        main.add(sp1, c);
        c.gridwidth = 0;
        main.add(this.customOpPane, c);
        JButton addButton1 = new JButton(new ResourceAction("manage_custom_op.add", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                CustomOpProperty newProps = new CustomOpProperty();
                newProps.setName("custom_op");
                ((DefaultListModel)ManageCustomOpDialog.this.ops.getModel()).addElement(newProps);
                ManageCustomOpDialog.this.ops.setSelectedValue(newProps, true);
                CustomOpManager.addOp(newProps);
            }
        });
        JButton saveButton = new JButton(new ResourceAction("manage_custom_op.save", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ManageCustomOpDialog.this.customOpPane.save();
                CustomOpManager.save(FileSystemService.getUserConfigFile("custom_op.xml"));
                ManageCustomOpDialog.this.dispose();
            }
        });
        this.layoutDefault(main, new AbstractButton[]{addButton1, this.deleteButton, saveButton, this.makeCancelButton()});
    }

    private class CustomOpPane extends JPanel {
        private static final long serialVersionUID = 1L;
        private CustomOpProperty property;
        private final JTextField nameField = new JTextField(20);
        private final String[] types = {CustomParams.Java(), CustomParams.Python()};
        private final JComboBox<String> typeField = new JComboBox<>(types);
        private final JTextField fileField = new JTextField(20);
        private final JTextField classNameField = new JTextField(20);

        public CustomOpPane() {
            this.setLayout(new GridBagLayout());
            this.classNameField.setEditable(true);
            JButton fileButton = new JButton(new ResourceAction(true, "manage_custom_op.file", new Object[0]) {
                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    String[] exts = {"py", "zip", "jar"};
                    String[] extDescs = {
                            "python script",
                            "zip file",
                            "jar file"};
                    File file = SwingTools.chooseFile(CustomOpPane.this, (File)null, true, false, exts, extDescs);
                    if(file != null) {
                        CustomOpPane.this.fileField.setText(file.getAbsolutePath());
                    }
                }
            });
            this.add("name", this.nameField, (JComponent)null);
            this.add("type", this.typeField, (JComponent)null);
            this.add("file", this.fileField, fileButton);
            this.add("classname", this.classNameField, (JComponent)null);
        }

        private void setProperty(CustomOpProperty op) {
            if (property != null) {
                this.save();
            }
            property = op;
            if (property == null) {
                nameField.setText("");
                fileField.setText("");
                classNameField.setText("");
                typeField.setSelectedIndex(0);
            } else {
                nameField.setText(op.getName());
                fileField.setText(op.getFile());
                classNameField.setText(op.getClazz());
                typeField.setSelectedItem(op.getType());
            }
        }

        private void add(String labelKey, JComponent component, JComponent button) {
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = 23;
            c.weightx = 0.5D;
            c.weighty = 1.0D;
            c.fill = 1;
            c.gridheight = 1;
            ResourceLabel label = new ResourceLabel("manage_custom_op." + labelKey, new Object[0]);
            label.setLabelFor(component);
            c.gridwidth = 0;
            this.add(label, c);
            c.insets = new Insets(0, 0, 5, 0);
            if(button == null) {
                c.gridwidth = 0;
                this.add(component, c);
            } else {
                c.gridwidth = -1;
                c.weightx = 1.0D;
                this.add(component, c);
                c.gridwidth = 0;
                c.weightx = 0.0D;
                c.insets = new Insets(0, 5, 5, 0);
                this.add(button, c);
            }

        }

        private void save() {
            if (this.property != null) {
                this.property.setName(nameField.getText());
                this.property.setFile(fileField.getText());
                this.property.setClazz(classNameField.getText());
                this.property.setType(typeField.getSelectedItem().toString());
            }
        }
    }
}
