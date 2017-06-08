package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.repository.RepositoryAccessor;

public interface DatabaseAccessValidator {
    boolean canAccessDatabaseConnection(String var1, RepositoryAccessor var2);
}