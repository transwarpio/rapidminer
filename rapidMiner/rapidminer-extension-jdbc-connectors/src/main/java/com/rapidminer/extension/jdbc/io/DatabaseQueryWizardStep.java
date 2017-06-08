package com.rapidminer.extension.jdbc.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.core.io.gui.ImportWizard;
import com.rapidminer.core.io.gui.InvalidConfigurationException;
import com.rapidminer.core.io.gui.WizardDirection;
import com.rapidminer.extension.jdbc.gui.tools.dialogs.SQLQueryBuilder;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.studio.io.gui.internal.steps.AbstractWizardStep;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

final class DatabaseQueryWizardStep extends AbstractWizardStep {
    static final String DATABASE_SQL_QUERY_ID = "database.sql_query";
    private final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder((DatabaseHandler)null);
    private final DatabaseDataSource dataSource;
    private final ImportWizard wizard;

    public DatabaseQueryWizardStep(DatabaseDataSource dataSource, ImportWizard wizard) {
        this.dataSource = dataSource;
        this.wizard = wizard;
        this.sqlQueryBuilder.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                DatabaseQueryWizardStep.this.fireStateChanged();
            }
        });
    }

    public String getI18NKey() {
        return "database.sql_query";
    }

    public JComponent getView() {
        JPanel panel = this.sqlQueryBuilder.makeQueryBuilderPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));
        return panel;
    }

    public String getNextStepID() {
        return "store_data_to_repository";
    }

    public void validate() throws InvalidConfigurationException {
        if(this.sqlQueryBuilder.getQuery().length() == 0) {
            throw new InvalidConfigurationException();
        }
    }

    public void viewWillBecomeVisible(WizardDirection direction) throws InvalidConfigurationException {
        this.wizard.setProgress(60);
        this.sqlQueryBuilder.setConnectionEntry(this.dataSource.getDatabaseConnection(), false);
    }

    public void viewWillBecomeInvisible(WizardDirection direction) throws InvalidConfigurationException {
        this.dataSource.setQuery(this.sqlQueryBuilder.getQuery());
    }
}
