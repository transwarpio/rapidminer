/**
 * Copyright (C) 2016 Transwarp Technology(Shanghai ) Co., Ltd.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.transwarp.midas.operator.retrieve;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.io.AbstractReader;
import com.rapidminer.parameter.*;
import io.transwarp.midas.constant.midas.params.data.RetrieveCSVParams;
import io.transwarp.midas.constant.midas.params.data.SourceSwitchParams;

import java.util.ArrayList;
import java.util.List;

public class RetrieveCSVOp extends SourceSwitchBase {

    public RetrieveCSVOp(OperatorDescription description) {
        super(description);
    }

    @Override
    public IOObject read() throws OperatorException {
        return null;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        /*
       （1）、PERMISSIVE：try to parse all lines，nulls are inserted for missing tokens
            and extra tokens are ignored.
　　　　（2）、DROPMALFORMED：drops lines which have fewer or more tokens than expected
　　　　（3）、FAILFAST: aborts with a RuntimeException if encounters any malformed line
         */
        ParameterTypeStringCategory parseMode = new ParameterTypeStringCategory(
                RetrieveCSVParams.parseMode(),
                "parse mode",
                new String[]{"PERMISSIVE", "DROPMALFORMED", "FAILFAST"},
                "PERMISSIVE",
                false);
        types.add(parseMode);

        ParameterTypeString delimiter = new ParameterTypeString(RetrieveCSVParams.delimiter(), "field delimiter", ",", true);
        types.add(delimiter);

        ParameterTypeString charset = new ParameterTypeString(RetrieveCSVParams.charset(), "charset", "UTF-8");
        types.add(charset);

        ParameterTypeString quote = new ParameterTypeString(RetrieveCSVParams.quote(), "quote", "\"");
        types.add(quote);

        ParameterTypeString escape =
                new ParameterTypeString(RetrieveCSVParams.escape(), "escape", "\\");
        types.add(escape);

        ParameterTypeString comment =
                new ParameterTypeString(RetrieveCSVParams.comment(), "comment", "");
        types.add(comment);

        ParameterTypeBoolean headerFlag =
                new ParameterTypeBoolean(RetrieveCSVParams.header(), "header flag", false);
        types.add(headerFlag);

        ParameterTypeBoolean inferSchemaFlag =
                new ParameterTypeBoolean(RetrieveCSVParams.inferSchema(),
                        "infer schema flag", false);
        types.add(inferSchemaFlag);

        ParameterTypeBoolean ignoreLeadingWhiteSpaceFlag =
                new ParameterTypeBoolean(RetrieveCSVParams.ignoreLeadingWhiteSpace(),
                        "ignore leading white space flag",
                        false);
        types.add(ignoreLeadingWhiteSpaceFlag);

        ParameterTypeBoolean ignoreTrailingWhiteSpaceFlag =
                new ParameterTypeBoolean(RetrieveCSVParams.ignoreTrailingWhiteSpace(),
                        "ignore trailing white space flag",
                        false);
        types.add(ignoreTrailingWhiteSpaceFlag);

        ParameterTypeString nullValue =
                new ParameterTypeString(RetrieveCSVParams.nullValue(), "null value", "");
        types.add(nullValue);

        ParameterTypeString nanValue =
                new ParameterTypeString(RetrieveCSVParams.nanValue(), "nan value", "NaN");
        types.add(nanValue);

        ParameterTypeString positiveInf =
                new ParameterTypeString(RetrieveCSVParams.positiveInf(), "positive inf", "Inf");
        types.add(positiveInf);

        ParameterTypeString negativeInf =
                new ParameterTypeString(RetrieveCSVParams.negativeInf(), "negative inf", "-Inf");
        types.add(negativeInf);

        ParameterTypeString dateFormat =
                new ParameterTypeString(RetrieveCSVParams.dateFormat(), "date format", "yyyy-MM-dd");
        types.add(dateFormat);

        ParameterTypeString timestampFormat =
                new ParameterTypeString(RetrieveCSVParams.timestampFormat(), "timestamp format",
                        "yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        types.add(timestampFormat);

        ParameterTypeInt maxColumns =
                new ParameterTypeInt(RetrieveCSVParams.maxColumns(), "max columns", 0, Integer
                        .MAX_VALUE, 20480);
        types.add(maxColumns);

        ParameterTypeInt maxCharsPerColumn =
                new ParameterTypeInt(RetrieveCSVParams.maxCharsPerColumn(),
                        "max chars per column",
                        0, Integer.MAX_VALUE, 1000000);
        types.add(maxCharsPerColumn);

        ParameterTypeBoolean escapeQuotes =
                new ParameterTypeBoolean(RetrieveCSVParams.escapeQuotes(), "escape quotes", true);
        types.add(escapeQuotes);

        ParameterTypeInt maxMalformedLogPerPartition =
                new ParameterTypeInt(RetrieveCSVParams.maxMalformedLogPerPartition(),
                        "max malformed log per partition",
                        0, Integer.MAX_VALUE, 10);
        types.add(maxMalformedLogPerPartition);

        return types;
    }
}
