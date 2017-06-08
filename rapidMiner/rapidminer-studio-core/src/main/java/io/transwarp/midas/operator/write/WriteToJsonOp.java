package io.transwarp.midas.operator.write;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.Compression;
import io.transwarp.midas.constant.midas.params.SaveMode;
import io.transwarp.midas.constant.midas.params.WriteToJsonParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class WriteToJsonOp extends BaseOp {
    public InputPort fileInputPort = getInputPorts().createPort("input");
    public OutputPort fileOutputPort = getOutputPorts().createPort("output");

    public WriteToJsonOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType file = new ParameterTypeString(
                WriteToJsonParams.file(),
                "file",
                false);
        types.add(file);

        ParameterType mode = new ParameterTypeStringCategory(
                WriteToJsonParams.mode(),
                "mode",
                new String[]{SaveMode.Append().toString(),
                        SaveMode.Overwrite().toString(),
                        SaveMode.Error().toString(),
                        SaveMode.Ignore().toString()},
                SaveMode.Overwrite().toString(),
                false);
        mode.setExpert(false);
        types.add(mode);

        ParameterType compression = new ParameterTypeStringCategory(
                WriteToJsonParams.compression(),
                "compression",
                new String[]{
                        Compression.none().toString(),
                        Compression.bzip2().toString(),
                        Compression.gzip().toString(),
                        Compression.lz4().toString(),
                        Compression.snappy().toString(),
                        Compression.deflate().toString()},
                Compression.none().toString().toString(),
                false);
        compression.setExpert(true);
        types.add(compression);

        ParameterType dateFormat = new ParameterTypeString(WriteToJsonParams.dateFormat(),
                "dateFormat", "yyyy-MM-dd", true);
        dateFormat.setExpert(true);
        types.add(dateFormat);

        ParameterType timestampFormat = new ParameterTypeString(WriteToJsonParams.timestampFormat(),
                "timestampFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZZ", true);
        timestampFormat.setExpert(true);
        types.add(timestampFormat);
        return types;
    }
}
