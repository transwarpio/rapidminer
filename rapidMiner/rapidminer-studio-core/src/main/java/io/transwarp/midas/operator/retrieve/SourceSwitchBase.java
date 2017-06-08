package io.transwarp.midas.operator.retrieve;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.io.AbstractReader;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import io.transwarp.midas.constant.midas.params.data.SourceSwitchParams;

import java.util.ArrayList;
import java.util.List;

class SourceSwitchBase extends AbstractReader<IOObject> {
    SourceSwitchBase(OperatorDescription description) {
        super(description, ExampleSet.class);
        remote = true;
    }

    @Override
    public IOObject read() throws OperatorException {
        return null;
    }

    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();

        String[] sources = new String[]{SourceSwitchParams.local(), SourceSwitchParams.remote()};

        ParameterTypeCategory sourceCategory = new ParameterTypeCategory(SourceSwitchParams.source(), "source type", sources, 0);
        sourceCategory.setExpert(false);
        sourceCategory.setOptional(false);
        types.add(sourceCategory);

        ParameterTypeFile localPath = new ParameterTypeFileUpload(SourceSwitchParams.localPath(), "local path", "csv", false);
        localPath.setExpert(false);
        localPath.registerDependencyCondition(new EqualTypeCondition(this, SourceSwitchParams.source(), sources, false, new int[]{0}));
        localPath.setOptional(true);
        types.add(localPath);

        types.addAll(super.getParameterTypes());

        ParameterTypeString remotePath = new ParameterTypeString(SourceSwitchParams.remotePath(), "hdfs path", true);
        remotePath.setExpert(false);
        remotePath.registerDependencyCondition(new EqualTypeCondition(this, SourceSwitchParams.source(), sources, false, new int[]{1}));
        remotePath.setOptional(true);
        types.add(remotePath);

        types.addAll(super.getParameterTypes());

        return types;
    }
}
