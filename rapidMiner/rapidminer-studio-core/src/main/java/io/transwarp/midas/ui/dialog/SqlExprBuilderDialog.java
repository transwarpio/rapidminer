package io.transwarp.midas.ui.dialog;

import com.rapidminer.gui.properties.PropertyDialog;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.SQLEditor;
import io.transwarp.midas.client.MidasClientFactory;
import io.transwarp.midas.thrift.message.FunctionMsg;
import io.transwarp.midas.ui.property.ParameterTypeSqlExpr;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SqlExprBuilderDialog extends PropertyDialog {
    private Map<String, FunctionMsg[]> functions = new HashMap<>();
    private Map<String, FunctionMsg> func2desc = new HashMap<>();
    private java.util.List<String> columns = new ArrayList<>();

    private final JList<String> functionList;
    private final JList<String> attributeList;

    private final JComboBox<String> functionGroups;
    private final JLabel usageTextField;
    private final SQLEditor exprTextArea;
    private JPanel gridPanel;

    public SqlExprBuilderDialog(final ParameterTypeSqlExpr type, String text) {
        super(type, "text");
        DefaultListModel<String> columnModel = new DefaultListModel<>();

        for (String col : type.getAttributeNames()) {
            columnModel.addElement(col);
        }

        try {
            MidasClientFactory.getClientInstance().ensureSession();
            functions = scala.collection.JavaConverters
                    .mapAsJavaMapConverter(
                            MidasClientFactory.getClientInstance().getFunctions()
                    ).asJava();

            for (String group : functions.keySet()) {
                for (FunctionMsg desc : functions.get(group)) {
                    func2desc.put(desc.getName(), desc);
                }
            }
        } catch (Exception e) {

        }
        this.functionList = new JList();
        this.attributeList = new JList(columnModel);

        this.functionGroups = new JComboBox<String>(functions.keySet().toArray(new String[10]));
        this.exprTextArea = new SQLEditor();
        this.exprTextArea.setText(text);
        this.usageTextField = new JLabel();
        this.gridPanel = new JPanel(createGridLayout(1, 2));

        this.layoutDefault(makeQueryBuilderPanel(), NORMAL, makeOkButton(), makeCancelButton());
    }

    public JPanel makeQueryBuilderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.PAGE_AXIS));

        this.functionGroups.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String group = (String)cb.getSelectedItem();
                DefaultListModel<String> functionModel = new DefaultListModel<>();

                for (FunctionMsg desc : functions.get(group)) {
                    functionModel.addElement(desc.getName());
                }
                functionList.setModel(functionModel);
            }
        });
        this.functionGroups.setSelectedItem("string");
        this.functionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.functionList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                JList<String> list = (JList<String>)evt.getSource();
                String func = list.getSelectedValue();
                FunctionMsg desc = func2desc.get(func);
                String text = String.format("<html><p>%s</p><p>%s</p></html", desc.getUsage(), desc.getExample());
                usageTextField.setText(text);
            }
        });
        this.functionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList<String> list = (JList<String>)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    String func = list.getSelectedValue();
                    int p = exprTextArea.getCaretPosition();
                    exprTextArea.insert(func + "()", p);
                    exprTextArea.setCaretPosition(exprTextArea.getCaretPosition()-1);
                }
            }
        });

        ExtendedJScrollPane functionPane = new ExtendedJScrollPane(this.functionList);
        functionPane.setBorder(createTitledBorder("functions"));

        functionPanel.add(this.functionGroups);
        functionPanel.add(functionPane);
        this.gridPanel.add(functionPanel);
        this.attributeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList<String> list = (JList<String>)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    String col = list.getSelectedValue();
                    int p = exprTextArea.getCaretPosition();
                    exprTextArea.insert(col, p);
                }
            }
        });


        ExtendedJScrollPane attributePane = new ExtendedJScrollPane(this.attributeList);
        attributePane.setBorder(createTitledBorder("columns"));
        this.gridPanel.add(attributePane);

        JLayer layer = new JLayer(this.gridPanel);
        JPanel glassPane = new JPanel(new GridBagLayout());

        glassPane.setOpaque(false);
        layer.setGlassPane(glassPane);
        glassPane.setVisible(true);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = 1;
        c1.weightx = 1.0D;
        c1.weighty = 0.3D;
        c1.gridwidth = 0;
        panel.add(layer, c1);
        c1.weighty = 1.0D;
        this.exprTextArea.setBorder(createTitledBorder("SQL Expression"));
        this.exprTextArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {

            }
        });
        RTextScrollPane textScrollPane = new RTextScrollPane(this.exprTextArea);
        textScrollPane.setLineNumbersEnabled(true);
        textScrollPane.setVerticalScrollBarPolicy(20);
        panel.add(textScrollPane, c1);

        JScrollPane usagePanel = new JScrollPane(this.usageTextField);
        panel.add(usagePanel, c1);
        return panel;
    }

    public String getExpr() {
        return exprTextArea.getText();
    }
}
