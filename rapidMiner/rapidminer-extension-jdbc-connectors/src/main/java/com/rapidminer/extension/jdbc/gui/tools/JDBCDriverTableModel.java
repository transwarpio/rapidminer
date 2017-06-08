package com.rapidminer.extension.jdbc.gui.tools;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DriverInfo;
import com.rapidminer.extension.jdbc.tools.jdbc.JDBCProperties;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.Tools;
import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;

public class JDBCDriverTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 9211315720113090453L;
    private static final String[] COLUMN_NAMES = new String[]{"Name", "Driver", "Available"};
    private static final String OK_ICON_NAME = "ok.png";
    private static final String ERROR_ICON_NAME = "error.png";
    private static Icon OK_ICON = null;
    private static Icon ERROR_ICON = null;
    private transient DriverInfo[] driverInfos;

    public JDBCDriverTableModel(DriverInfo[] driverInfos) {
        this.driverInfos = driverInfos;
    }

    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int getRowCount() {
        return this.driverInfos != null?this.driverInfos.length:0;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        DriverInfo info = this.driverInfos[rowIndex];
        switch(columnIndex) {
            case 0:
                return info.getShortName();
            case 1:
                if(info.getClassName() != null) {
                    return info.getClassName();
                } else {
                    JDBCProperties props = info.getProperties();
                    if(props == null) {
                        return "Unknown";
                    }

                    return Tools.toString(props.getDriverClasses());
                }
            case 2:
                if(info.getClassName() != null) {
                    if(OK_ICON != null) {
                        return OK_ICON;
                    }

                    return "Ok";
                } else {
                    if(ERROR_ICON != null) {
                        return ERROR_ICON;
                    }

                    return "No Driver Available";
                }
            default:
                return null;
        }
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    static {
        OK_ICON = SwingTools.createIcon("16/ok.png");
        ERROR_ICON = SwingTools.createIcon("16/error.png");
    }
}
