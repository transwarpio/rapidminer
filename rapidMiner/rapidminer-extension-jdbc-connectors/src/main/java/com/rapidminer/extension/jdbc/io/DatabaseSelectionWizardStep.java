package com.rapidminer.extension.jdbc.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.core.io.gui.ImportWizard;
import com.rapidminer.core.io.gui.InvalidConfigurationException;
import com.rapidminer.core.io.gui.WizardDirection;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.studio.io.gui.internal.steps.AbstractWizardStep;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.sql.SQLException;

final class DatabaseSelectionWizardStep extends AbstractWizardStep {
    private static final String DATABASE_CONNECTION_SELECTION_ID = "database.connection_selection";
    private final ImportWizard wizard;
    private transient DatabaseSelectionView view;

    public DatabaseSelectionWizardStep(final ImportWizard wizard) {
        this.wizard = wizard;
        SwingTools.invokeAndWait(new Runnable() {
            public void run() {
                DatabaseSelectionWizardStep.this.view = new DatabaseSelectionView(wizard.getDialog());
            }
        });
        this.view.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                DatabaseSelectionWizardStep.this.fireStateChanged();
            }
        });
    }

    public String getI18NKey() {
        return "database.connection_selection";
    }

    public JComponent getView() {
        return this.view;
    }

    public void validate() throws InvalidConfigurationException {
        ConnectionEntry selectedConnection = this.view.getSelectedConnection();
        if(selectedConnection == null) {
            throw new InvalidConfigurationException();
        }
    }

    public void viewWillBecomeVisible(WizardDirection direction) throws InvalidConfigurationException {
        this.wizard.setProgress(25);
        DatabaseDataSource dataSource = (DatabaseDataSource)this.wizard.getDataSource(DatabaseDataSource.class);
        ConnectionEntry selectedConnection = dataSource.getDatabaseConnection();
        this.view.setSelectedConnection(selectedConnection);
    }

    public void viewWillBecomeInvisible(WizardDirection direction) throws InvalidConfigurationException {
        DatabaseDataSource dataSource = (DatabaseDataSource)this.wizard.getDataSource(DatabaseDataSource.class);
        if(direction == WizardDirection.NEXT) {
            ConnectionEntry selectedConnection = this.view.getSelectedConnection();

            try {
                DatabaseConnectionService.testConnection(selectedConnection);
            } catch (SQLException var5) {
                this.view.setConnectionErrorText(var5.getMessage());
                throw new InvalidConfigurationException();
            }

            dataSource.setDatabaseConnection(selectedConnection);
        }

    }

    public String getNextStepID() {
        return "database.sql_query";
    }

    public ButtonState getNextButtonState() {
        if(this.view.hasTestFailed()) {
            return ButtonState.DISABLED;
        } else {
            try {
                this.validate();
                return ButtonState.ENABLED;
            } catch (InvalidConfigurationException var2) {
                return ButtonState.DISABLED;
            }
        }
    }
}
