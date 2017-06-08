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
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import io.transwarp.midas.constant.midas.params.data.RetrieveJsonParams;
import io.transwarp.midas.constant.midas.params.data.RetrieveOperatorParams;
import io.transwarp.midas.constant.midas.params.data.SourceSwitchParams;

import java.util.ArrayList;
import java.util.List;

public class RetrieveJsonOp extends SourceSwitchBase {

    public RetrieveJsonOp(OperatorDescription description) {
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
                RetrieveJsonParams.parseMode(),
                "parse mode",
                new String[]{"PERMISSIVE", "DROPMALFORMED", "FAILFAST"},
                "PERMISSIVE",
                false);
        types.add(parseMode);
        ParameterTypeDouble samplingRatio =
                new ParameterTypeDouble(RetrieveJsonParams.samplingRatio(),
                        "sampling ratio", 0, 1.0, 1.0);
        samplingRatio.setExpert(true);
        types.add(samplingRatio);

        ParameterTypeBoolean primitivesAsString =
                new ParameterTypeBoolean(RetrieveJsonParams.primitivesAsString(),
                        "primitives as string", false);
        types.add(primitivesAsString);

        ParameterTypeBoolean prefersDecimal =
                new ParameterTypeBoolean(RetrieveJsonParams.prefersDecimal(),
                        "prefers decimal", false);
        types.add(prefersDecimal);

        ParameterTypeBoolean allowComments =
                new ParameterTypeBoolean(RetrieveJsonParams.allowComments(),
                        "allow comments", false);
        types.add(allowComments);

        ParameterTypeBoolean allowUnquotedFieldNames =
                new ParameterTypeBoolean(RetrieveJsonParams.allowUnquotedFieldNames(),
                        "allow unquoted field names",
                        false);
        types.add(allowUnquotedFieldNames);

        ParameterTypeBoolean allowSingleQuotes =
                new ParameterTypeBoolean(RetrieveJsonParams.allowSingleQuotes(),
                        "allow single quotes",
                        true);
        types.add(allowSingleQuotes);

        ParameterTypeBoolean allowNumericLeadingZeros =
                new ParameterTypeBoolean(RetrieveJsonParams.allowNumericLeadingZeros(),
                        "allow numeric leading zeros",
                        false);
        types.add(allowNumericLeadingZeros);

        ParameterTypeBoolean allowNonNumericNumbers =
                new ParameterTypeBoolean(RetrieveJsonParams.allowNonNumericNumbers(),
                        "allow non numeric numbers",
                        true);
        types.add(allowNonNumericNumbers);

        ParameterTypeBoolean allowBackslashEscapingAnyCharacter =
                new ParameterTypeBoolean(RetrieveJsonParams.allowBackslashEscapingAnyCharacter(),
                        "allow backslash escaping any character",
                        false);
        types.add(allowBackslashEscapingAnyCharacter);

        ParameterTypeString dateFormat =
                new ParameterTypeString(RetrieveJsonParams.dateFormat(),
                        "date format", "yyyy-MM-dd");
        types.add(dateFormat);

        ParameterTypeString timestampFormat =
                new ParameterTypeString(RetrieveJsonParams.timestampFormat(),
                        "timestamp format", "yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        types.add(timestampFormat);

        return types;
    }
}
