package com.rapidminer.extension.jdbc.gui.tools.dialogs;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;
import com.rapidminer.extension.jdbc.tools.jdbc.JDBCProperties;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.XMLException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.Driver;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class ManageDatabaseDriversDialog extends ButtonDialog {
    private static final long serialVersionUID = 1L;
    private final JList<JDBCProperties> availableDrivers;
    public static final Action SHOW_DIALOG_ACTION = new ResourceAction("manage_database_drivers", new Object[0]) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            (new ManageDatabaseDriversDialog()).setVisible(true);
        }
    };
    private final ManageDatabaseDriversDialog.DriverPane driverPane = new ManageDatabaseDriversDialog.DriverPane();
    private final AbstractButton deleteButton = new JButton(new ResourceAction("manage_database_drivers.delete", new Object[0]) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            JDBCProperties props = ManageDatabaseDriversDialog.this.driverPane.properties;
            if(props != null && props.isUserDefined()) {
                ((DefaultListModel)ManageDatabaseDriversDialog.this.availableDrivers.getModel()).removeElement(props);
                DatabaseService.removeJDBCProperties(props);
            }

        }
    });

    public ManageDatabaseDriversDialog() {
        super(ApplicationFrame.getApplicationFrame(), "manage_database_drivers", ModalityType.APPLICATION_MODAL, new Object[0]);
        JPanel main = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = c.ipady = 5;
        c.insets = new Insets(5, 5, 5, 5);
        DefaultListModel model = new DefaultListModel();
        Iterator sp = DatabaseService.getJDBCProperties().iterator();

        while(sp.hasNext()) {
            JDBCProperties addButton = (JDBCProperties)sp.next();
            model.addElement(addButton);
        }

        this.availableDrivers = new JList(model);
        this.availableDrivers.setSelectionMode(0);
        this.availableDrivers.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                JDBCProperties selected = (JDBCProperties)ManageDatabaseDriversDialog.this.availableDrivers.getSelectedValue();
                ManageDatabaseDriversDialog.this.driverPane.setProperties(selected);
            }
        });
        c.anchor = 23;
        c.weightx = 0.5D;
        c.weighty = 1.0D;
        c.fill = 1;
        c.gridheight = 1;
        c.gridwidth = -1;
        ExtendedJScrollPane sp1 = new ExtendedJScrollPane(this.availableDrivers);
        sp1.setBorder(BorderFactory.createLineBorder(Colors.TEXTFIELD_BORDER));
        main.add(sp1, c);
        c.gridwidth = 0;
        main.add(this.driverPane, c);
        JButton addButton1 = new JButton(new ResourceAction("manage_database_drivers.add", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                JDBCProperties newProps = new JDBCProperties(true);
                ((DefaultListModel)ManageDatabaseDriversDialog.this.availableDrivers.getModel()).addElement(newProps);
                ManageDatabaseDriversDialog.this.availableDrivers.setSelectedValue(newProps, true);
                DatabaseService.addJDBCProperties(newProps);
            }
        });
        JButton saveButton = new JButton(new ResourceAction("manage_database_drivers.save", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    ManageDatabaseDriversDialog.this.driverPane.save();
                    DatabaseService.saveUserDefinedProperties();
                    for (JDBCProperties prop: DatabaseService.getJDBCProperties()) {
                        prop.registerDrivers();
                    }
                    ManageDatabaseDriversDialog.this.dispose();
                } catch (XMLException var3) {
                    SwingTools.showSimpleErrorMessage("manage_database_drivers.error_saving", var3, new Object[]{var3.getMessage()});
                }

            }
        });
        this.layoutDefault(main, new AbstractButton[]{addButton1, this.deleteButton, saveButton, this.makeCancelButton()});
        this.driverPane.setProperties((JDBCProperties)null);
    }

    private List<String> findDrivers(final File file) {
        final LinkedList driverNames = new LinkedList();
        (new ProgressThread("manage_database_drivers.scan_jar", true) {
            public void run() {
                try {
                    ClassLoader e = (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        public ClassLoader run() throws Exception {
                            try {
                                return new URLClassLoader(new URL[]{file.toURI().toURL()});
                            } catch (MalformedURLException var2) {
                                throw new RuntimeException("Cannot create class loader for file \'" + file + "\': " + var2.getMessage(), var2);
                            }
                        }
                    });

                    try {
                        JarFile e1 = new JarFile(file);
                        Tools.findImplementationsInJar(e, e1, Driver.class, driverNames);
                    } catch (Exception var3) {
                        LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.gui.tools.dialogs.ManageDatabaseDriversDialog.scanning_jar_file_error", new Object[]{file, var3.getMessage()}), var3);
                    }

                } catch (PrivilegedActionException var4) {
                    throw new RuntimeException("Cannot create class loader for file \'" + file + "\': " + var4.getMessage(), var4);
                }
            }
        }).startAndWait();
        return driverNames;
    }

    public void setVisible(boolean b) {
        if(DatabaseService.checkCommercialDatabaseConstraint("jdbc.manage_drivers") == null) {
            super.setVisible(b);
        }

    }

    private class DriverPane extends JPanel {
        private static final long serialVersionUID = 1L;
        private JDBCProperties properties;
        private final JTextField nameField = new JTextField(20);
        private final JTextField urlprefixField = new JTextField(20);
        private final JTextField portField = new JTextField(20);
        private final JTextField jarFileField = new JTextField(20);
        private final JTextField dbseparatorField = new JTextField(20);
        private final JComboBox<String> classNameCombo = new JComboBox();

        public DriverPane() {
            this.setLayout(new GridBagLayout());
            this.classNameCombo.setEditable(true);
            JButton fileButton = new JButton(new ResourceAction(true, "manage_database_drivers.jarfile", new Object[0]) {
                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    File file = SwingTools.chooseFile(DriverPane.this, (File)null, true, "jar", "JDBC driver jar file");
                    if(file != null) {
                        DriverPane.this.jarFileField.setText(file.getAbsolutePath());
                        ((DefaultComboBoxModel)DriverPane.this.classNameCombo.getModel()).removeAllElements();
                        Iterator var3 = ManageDatabaseDriversDialog.this.findDrivers(file).iterator();

                        while(var3.hasNext()) {
                            String driver = (String)var3.next();
                            ((DefaultComboBoxModel)DriverPane.this.classNameCombo.getModel()).addElement(driver);
                        }
                    }

                }
            });
            this.add("name", this.nameField, (JComponent)null);
            this.add("urlprefix", this.urlprefixField, (JComponent)null);
            this.add("port", this.portField, (JComponent)null);
            this.add("dbseparator", this.dbseparatorField, (JComponent)null);
            this.add("jarfile", this.jarFileField, fileButton);
            this.add("classname", this.classNameCombo, (JComponent)null);
        }

        private void add(String labelKey, JComponent component, JComponent button) {
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = 23;
            c.weightx = 0.5D;
            c.weighty = 1.0D;
            c.fill = 1;
            c.gridheight = 1;
            ResourceLabel label = new ResourceLabel("manage_database_drivers." + labelKey, new Object[0]);
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

        private void setProperties(JDBCProperties props) {
            if(this.properties != props) {
                this.save();
            }

            this.properties = props;
            ((DefaultComboBoxModel)this.classNameCombo.getModel()).removeAllElements();
            if(props == null) {
                SwingTools.setEnabledRecursive(this, false);
                this.nameField.setText("");
                this.urlprefixField.setText("");
                this.portField.setText("");
                this.classNameCombo.setSelectedItem("");
                this.jarFileField.setText("");
                this.dbseparatorField.setText("/");
            } else {
                this.nameField.setText(props.getName());
                this.urlprefixField.setText(props.getUrlPrefix());
                this.portField.setText(props.getDefaultPort());
                this.classNameCombo.setSelectedItem(Tools.toString(props.getDriverClasses(), ","));
                this.jarFileField.setText(props.getDriverJarFile());
                this.dbseparatorField.setText(props.getDbNameSeperator());
                if(props.isUserDefined()) {
                    SwingTools.setEnabledRecursive(this, true);
                } else {
                    SwingTools.setEnabledRecursive(this, false);
                }
            }

            ManageDatabaseDriversDialog.this.deleteButton.setEnabled(props != null && props.isUserDefined());
        }

        private void save() {
            if(this.properties != null && this.properties.isUserDefined()) {
                this.properties.setName(this.nameField.getText());
                this.properties.setUrlPrefix(this.urlprefixField.getText());
                this.properties.setDefaultPort(this.portField.getText());
                this.properties.setDriverJarFile(this.jarFileField.getText());
                this.properties.setDbNameSeperator(this.dbseparatorField.getText());
                String className = (String)this.classNameCombo.getSelectedItem();
                if(className != null) {
                    this.properties.setDriverClasses(className);
                }
            }

        }
    }
}
