package io.transwarp.midas.ui.property;

import com.rapidminer.gui.properties.celleditors.value.PropertyValueCellEditor;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.operator.Operator;
import io.transwarp.midas.ui.dialog.SqlExprBuilderDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class SqlExprCellEditor extends AbstractCellEditor implements PropertyValueCellEditor {

    private static final long serialVersionUID = -8235047960089702819L;

    private final JPanel panel = new JPanel();

    private final JTextArea textArea = new JTextArea(1,12);

    private final ParameterTypeSqlExpr type;

    private final GridBagLayout gridBagLayout = new GridBagLayout();

    private Operator operator;

    public SqlExprCellEditor(ParameterTypeSqlExpr type) {
        this.type = type;
        panel.setLayout(gridBagLayout);
        panel.setToolTipText(type.getDescription());
        textArea.setToolTipText(type.getDescription());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        gridBagLayout.setConstraints(textArea, c);
        panel.add(textArea);

        addButton(createFileChooserButton(), GridBagConstraints.REMAINDER);
    }

    protected JButton createFileChooserButton() {
        JButton button = new JButton(new ResourceAction(true, "sql_expr") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed();
            }
        });
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    protected void addButton(JButton button, int gridwidth) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = gridwidth;
        c.weightx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 0, 0);
        gridBagLayout.setConstraints(button, c);
        panel.add(button);
    }

    private void buttonPressed() {
        String value = (String) getCellEditorValue();
        SqlExprBuilderDialog dialog = new SqlExprBuilderDialog(type, value);
        dialog.setVisible(true);
        if (dialog.isOk()) {
            String text = dialog.getExpr();
            textArea.setText(text);
            fireEditingStopped();
        } else {
            fireEditingCanceled();
        }
    }

    @Override
    public Object getCellEditorValue() {
        return textArea.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
        textArea.setText(value == null ? "" : value.toString());
        return panel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        return getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public boolean useEditorAsRenderer() {
        return true;
    }

    @Override
    public boolean rendersLabel() {
        return false;
    }

}