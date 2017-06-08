package com.rapidminer.extension.jdbc.gui.tools;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.gui.tools.JDBCDriverTableModel;
import com.rapidminer.extension.jdbc.tools.jdbc.DriverInfo;
import javax.swing.DefaultRowSorter;
import javax.swing.JTable;

public class JDBCDriverTable extends JTable {
    private static final long serialVersionUID = -2762178074014243751L;

    public JDBCDriverTable(DriverInfo[] driverInfos) {
        this.setModel(new JDBCDriverTableModel(driverInfos));
        this.setRowHeight(this.getRowHeight() + 4 + 4);
        this.setAutoCreateRowSorter(true);
        ((DefaultRowSorter)this.getRowSorter()).setMaxSortKeys(1);
    }

    public Class<?> getColumnClass(int column) {
        return this.getValueAt(0, column).getClass();
    }
}
