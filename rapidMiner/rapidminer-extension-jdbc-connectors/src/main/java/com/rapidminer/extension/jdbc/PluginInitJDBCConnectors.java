package com.rapidminer.extension.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.core.io.data.source.DataSourceFactoryRegistry;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.extension.jdbc.gui.actions.CheckForJDBCDriversAction;
import com.rapidminer.extension.jdbc.gui.properties.celleditors.value.DatabaseConnectionValueCellEditor;
import com.rapidminer.extension.jdbc.gui.properties.celleditors.value.DatabaseTableValueCellEditor;
import com.rapidminer.extension.jdbc.gui.properties.celleditors.value.SQLQueryValueCellEditor;
import com.rapidminer.extension.jdbc.gui.tools.dialogs.ManageDatabaseConnectionsDialog;
import com.rapidminer.extension.jdbc.gui.tools.dialogs.ManageDatabaseDriversDialog;
import com.rapidminer.extension.jdbc.io.DatabaseDataSourceFactory;
import com.rapidminer.extension.jdbc.operator.io.AccessDataWriter;
import com.rapidminer.extension.jdbc.operator.io.DatabaseExampleSetUpdater;
import com.rapidminer.extension.jdbc.operator.io.DatabaseExampleSetWriter;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseConnection;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseSchema;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseTable;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeSQLQuery;
import com.rapidminer.extension.jdbc.repository.db.DefaultDBRepository;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.properties.PropertyPanel;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResultWarningPreventionRegistry;
import com.rapidminer.license.LicenseManagerRegistry;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.repository.RepositoryAccessor;
import com.rapidminer.repository.RepositoryManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class PluginInitJDBCConnectors {
    public static final String PROPERTY_EVALUATE_MD_FOR_SQL_QUERIES = "rapidminer.gui.evaluate_meta_data_for_sql_queries";
    private static final Action CHECK_FOR_JDBC_DRIVERS_ACTION;
    private static final Action MANAGE_DB_CONNECTIONS_ACTION;
    public static final String MIDAS_LIMIT = "rapidminer.midas.midas.limit";
    private PluginInitJDBCConnectors() {
    }

    public static void initPlugin() {
        DatabaseService.init();
        DatabaseConnectionService.init();
    }

    public static void initGui(MainFrame mainframe) {
        ResultWarningPreventionRegistry.addOperatorClass(AccessDataWriter.class);
        ResultWarningPreventionRegistry.addOperatorClass(DatabaseExampleSetWriter.class);
        ResultWarningPreventionRegistry.addOperatorClass(DatabaseExampleSetUpdater.class);
        JMenu connectionsMenu = mainframe.getConnectionsMenu();
        connectionsMenu.add(CHECK_FOR_JDBC_DRIVERS_ACTION);
        connectionsMenu.add(ManageDatabaseDriversDialog.SHOW_DIALOG_ACTION);
        connectionsMenu.add(MANAGE_DB_CONNECTIONS_ACTION);
        PropertyPanel.registerPropertyValueCellEditor(ParameterTypeDatabaseConnection.class, DatabaseConnectionValueCellEditor.class);
        PropertyPanel.registerPropertyValueCellEditor(ParameterTypeSQLQuery.class, SQLQueryValueCellEditor.class);
        PropertyPanel.registerPropertyValueCellEditor(ParameterTypeDatabaseTable.class, DatabaseTableValueCellEditor.class);
        PropertyPanel.registerPropertyValueCellEditor(ParameterTypeDatabaseSchema.class, DatabaseTableValueCellEditor.class);
        System.setProperty("connections", "connections");
        RapidMiner.registerParameter(new ParameterTypeBoolean("rapidminer.gui.evaluate_meta_data_for_sql_queries", "", true));
        DataSourceFactoryRegistry.INSTANCE.register(new DatabaseDataSourceFactory());
        RepositoryManager.getInstance((RepositoryAccessor)null).addRepository(new DefaultDBRepository());

        RapidMiner.registerParameter(new ParameterTypeInt(MIDAS_LIMIT, "editor view records limit", 1, Integer.MAX_VALUE, 500, false), "midas");
    }

    public static void initFinalChecks() {
    }

    public static void initPluginManager() {
    }

    public static Boolean showAboutBox() {
        return Boolean.valueOf(false);
    }

    public static Boolean useExtensionTreeRoot() {
        return Boolean.valueOf(false);
    }

    public static boolean checkConnectorsConstraint(String connectorType) {
        return LicenseManagerRegistry.INSTANCE.get().checkConstraintViolation(ProductConstraintManager.INSTANCE.getProduct(), ProductConstraintManager.INSTANCE.getConnectorsConstraint(), connectorType, true) == null;
    }

    static {

        CHECK_FOR_JDBC_DRIVERS_ACTION = new CheckForJDBCDriversAction();
        MANAGE_DB_CONNECTIONS_ACTION = new ResourceAction(true, "manage_db_connections", new Object[0]) {
            private static final long serialVersionUID = 2457587046500212869L;
//            private static final long serialVersionUID = 2457587046500345234L;

            public void actionPerformed(ActionEvent e) {
                ManageDatabaseConnectionsDialog dialog = new ManageDatabaseConnectionsDialog();
                dialog.setVisible(true);
            }
        };
    }
}