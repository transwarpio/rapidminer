package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.repository.RepositoryAccessor;

public class TrueAccessValidator implements DatabaseAccessValidator {
    public TrueAccessValidator() {
    }

    public boolean canAccessDatabaseConnection(String entryName, RepositoryAccessor accessor) {
        return true;
    }
}

