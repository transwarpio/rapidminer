package com.rapidminer.extension.jdbc.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.core.io.data.source.DataSourceFactory;
import com.rapidminer.core.io.gui.ImportWizard;
import com.rapidminer.core.io.gui.WizardStep;
import com.rapidminer.extension.jdbc.io.DatabaseDataSource;
import com.rapidminer.extension.jdbc.io.DatabaseQueryWizardStep;
import com.rapidminer.extension.jdbc.io.DatabaseSelectionWizardStep;
import java.util.Arrays;
import java.util.List;

public final class DatabaseDataSourceFactory implements DataSourceFactory<DatabaseDataSource> {
    private static final String DATABASE_ID = "database";

    public DatabaseDataSourceFactory() {
    }

    public DatabaseDataSource createNew() {
        return new DatabaseDataSource();
    }

    public String getI18NKey() {
        return "database";
    }

    public Class<DatabaseDataSource> getDataSourceClass() {
        return DatabaseDataSource.class;
    }

    public List<WizardStep> createCustomSteps(ImportWizard wizard, DatabaseDataSource dataSource) {
        return Arrays.asList(new WizardStep[]{new DatabaseQueryWizardStep(dataSource, wizard)});
    }

    public WizardStep createLocationStep(ImportWizard wizard) {
        return new DatabaseSelectionWizardStep(wizard);
    }
}
