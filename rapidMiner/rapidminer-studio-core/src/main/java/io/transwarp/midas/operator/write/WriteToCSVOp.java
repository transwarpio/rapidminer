package io.transwarp.midas.operator.write;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import io.transwarp.midas.constant.midas.params.Compression;
import io.transwarp.midas.constant.midas.params.SaveMode;
import io.transwarp.midas.constant.midas.params.WriteToCSVParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.ArrayList;
import java.util.List;

public class WriteToCSVOp extends BaseOp {
    public InputPort fileInputPort = getInputPorts().createPort("input");
    public OutputPort fileOutputPort = getOutputPorts().createPort("output");

    public WriteToCSVOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new ArrayList<>();
        ParameterType file = new ParameterTypeString(
                WriteToCSVParams.file(),
                "file",
                false);
        types.add(file);

        ParameterType mode = new ParameterTypeStringCategory(
                WriteToCSVParams.mode(),
                "mode",
                new String[]{SaveMode.Append().toString(),
                        SaveMode.Overwrite().toString(),
                        SaveMode.Error().toString(),
                        SaveMode.Ignore().toString()},
                WriteToCSVParams.defaultMode(),
                false);
        mode.setExpert(false);
        types.add(mode);

        ParameterType sep = new ParameterTypeString(
                WriteToCSVParams.sep(),
                "sep",
                WriteToCSVParams.defaultSep(),
                true);
        sep.setExpert(true);
        types.add(sep);

        ParameterType quote = new ParameterTypeString(
                WriteToCSVParams.quote(),
                "quote",
                WriteToCSVParams.defaultQuote(),
                true);
        quote.setExpert(true);
        types.add(quote);

        ParameterType escape = new ParameterTypeString(
                WriteToCSVParams.escape(),
                "escape",
                WriteToCSVParams.defaultEscape(),
                true);
        escape.setExpert(true);
        types.add(escape);

        ParameterType escapeQuotes = new ParameterTypeBoolean(
                WriteToCSVParams.escapeQuotes(),
                "escapeQuotes",
                WriteToCSVParams.defaultEscapeQuote());
        escapeQuotes.setExpert(true);
        types.add(escapeQuotes);

        ParameterType quoteAll = new ParameterTypeBoolean(
                WriteToCSVParams.quoteAll(),
                "quoteAll",
                WriteToCSVParams.defaultQuoteAll());
        quoteAll.setExpert(true);
        types.add(quoteAll);

        ParameterType header = new ParameterTypeBoolean(
                WriteToCSVParams.header(),
                "header",
                WriteToCSVParams.defaultHeader());
        header.setExpert(true);
        types.add(header);

        ParameterType nullValue = new ParameterTypeString(
                WriteToCSVParams.nullValue(),
                "nullValue",
                WriteToCSVParams.defaultNullValue(),
                true);
        nullValue.setExpert(true);
        types.add(nullValue);

        ParameterType compression = new ParameterTypeStringCategory(
                WriteToCSVParams.compression(),
                "compression",
                new String[]{
                        Compression.none().toString(),
                        Compression.bzip2().toString(),
                        Compression.gzip().toString(),
                        Compression.lz4().toString(),
                        Compression.snappy().toString(),
                        Compression.deflate().toString()},
                WriteToCSVParams.defaultCompression(),
                false);
        compression.setExpert(true);
        types.add(compression);

        ParameterType dateFormat = new ParameterTypeString(
                WriteToCSVParams.dateFormat(),
                "dateFormat",
                WriteToCSVParams.defaultDateFormat(),
                true);
        dateFormat.setExpert(true);
        types.add(dateFormat);

        ParameterType timestampFormat = new ParameterTypeString(
                WriteToCSVParams.timestampFormat(),
                "timestampFormat",
                WriteToCSVParams.defaultTimestampFormat(),
                true);
        timestampFormat.setExpert(true);
        types.add(timestampFormat);
        return types;
    }
}
