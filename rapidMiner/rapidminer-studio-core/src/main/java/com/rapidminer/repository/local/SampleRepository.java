package com.rapidminer.repository.local;

import com.rapidminer.repository.RepositoryException;
import com.rapidminer.tools.I18N;

import java.io.File;

public class SampleRepository extends LocalRepository{
    public SampleRepository(String name, File root) throws RepositoryException {
        super(name, root);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public String getIconName() {
        return I18N.getMessage(I18N.getGUIBundle(), "gui.repository.resource.icon");
    }
}
