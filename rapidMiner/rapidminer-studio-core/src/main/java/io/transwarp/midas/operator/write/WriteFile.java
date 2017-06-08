package io.transwarp.midas.operator.write;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.parameter.conditions.EqualStringCondition;
import io.transwarp.midas.constant.midas.params.Compression;
import io.transwarp.midas.constant.midas.params.FormatType;
import io.transwarp.midas.constant.midas.params.SaveMode;
import io.transwarp.midas.constant.midas.params.WriteToFileParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class WriteFile extends BaseOp {
    public InputPort fileInputPort = getInputPorts().createPort("input");
    public OutputPort fileOutputPort = getOutputPorts().createPort("output");

    public WriteFile(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType file = new ParameterTypeString(
                WriteToFileParams.file(),
                "file",
                false);
        types.add(file);

        String[] formats = new String[]{FormatType.text().toString(),
                FormatType.orc().toString(),
                FormatType.parquet().toString()};
        ParameterType format = new ParameterTypeStringCategory(
                WriteToFileParams.format(),
                "format",
                formats,
                FormatType.text().toString(),
                false);
        format.setExpert(false);
        types.add(format);

        ParameterType mode = new ParameterTypeStringCategory(
                WriteToFileParams.mode(),
                "mode",
                new String[]{SaveMode.Append().toString(),
                        SaveMode.Overwrite().toString(),
                        SaveMode.Error().toString(),
                        SaveMode.Ignore().toString()},
                        SaveMode.Overwrite().toString(),
                false);
        mode.setExpert(false);
        types.add(mode);

        ParameterTypeString fieldDelim = new ParameterTypeString(
                WriteToFileParams.fieldDelim(),
                "fieldDelim",
                true,
                true);
        fieldDelim.registerDependencyCondition(new EqualStringCondition(this,
                WriteToFileParams.format(),
                false,
                FormatType.text().toString()));
        types.add(fieldDelim);

        ParameterTypeString arrayDelim = new ParameterTypeString(
                WriteToFileParams.arrayDelim(),
                "arrayDelim",
                true,
                true);
        arrayDelim.registerDependencyCondition(new EqualStringCondition(this,
                WriteToFileParams.format(),
                false,
                FormatType.text().toString()));
        types.add(arrayDelim);


        ParameterTypeString mapDelim = new ParameterTypeString(
                WriteToFileParams.mapDelim(),
                "mapDelim",
                true,
                true);
        mapDelim.registerDependencyCondition(new EqualStringCondition(this,
                WriteToFileParams.format(),
                false,
                FormatType.text().toString()));
        types.add(mapDelim);


        ParameterType parquetCompression = new ParameterTypeStringCategory(
                WriteToFileParams.parquetCompression(),
                "parquetCompression",
                new String[]{
                        Compression.none().toString(),
                        Compression.snappy().toString(),
                        Compression.gzip().toString(),
                        Compression.lzo().toString()},
                Compression.none().toString(),
                false);
        parquetCompression.setExpert(true);
        parquetCompression.registerDependencyCondition(new EqualStringCondition(this,
                WriteToFileParams.format(),
                false,
                FormatType.parquet().toString()));
        types.add(parquetCompression);

        ParameterType orcCompression = new ParameterTypeStringCategory(
                WriteToFileParams.orcCompression(),
                "orcCompression",
                new String[]{
                        Compression.none().toString(),
                        Compression.snappy().toString(),
                        Compression.zlib().toString(),
                        Compression.lzo().toString()},
                Compression.none().toString(),
                false);
        orcCompression.setExpert(true);
        orcCompression.registerDependencyCondition(new EqualStringCondition(this,
                WriteToFileParams.format(),
                false,
                FormatType.orc().toString()));
        types.add(orcCompression);

        ParameterType textCompression = new ParameterTypeStringCategory(
                WriteToFileParams.textCompression(),
                "textCompression",
                new String[]{
                        Compression.none().toString(),
                        Compression.bzip2().toString(),
                        Compression.gzip().toString(),
                        Compression.lz4().toString(),
                        Compression.snappy().toString(),
                        Compression.deflate().toString()},
                Compression.none().toString().toString(),
                false);
        textCompression.setExpert(true);
        textCompression.registerDependencyCondition(new EqualStringCondition(this,
                WriteToFileParams.format(),
                false,
                FormatType.text().toString()));
        types.add(textCompression);

        return types;
    }
}
