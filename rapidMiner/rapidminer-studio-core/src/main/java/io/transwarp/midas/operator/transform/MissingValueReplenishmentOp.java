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
package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.ParameterCondition;
import io.transwarp.midas.operator.BaseDataProcessOp;

import java.util.List;

public class MissingValueReplenishmentOp extends BaseDataProcessOp {
    // Options in catalog
    private static final String DEFAULT_FILTER = "default";
    private static final String SPECIFIC_COLUMNS = "columns";
    private static final String REPLENISH_VALUE = "value";
    // Options in sub-catalog
    private static final String COLUMN_ATTRIBUTE = "attribute";
    private static final String COLUMN_FILTER = "filter";
    // Descriptions
    private static final String DEFAULT_FILTER_DES = "Function to apply to all columns that are not explicitly specified by parameter 'columns'.";
    private static final String SPECIFIC_COLUMNS_DES = "List of replacement functions for each column. To be mentioned, specific columns have higher priority than default filter";
    private static final String REPLENISH_VALUE_DES = "This value is used for some of the replenishment types.";
    private static final String COLUMN_ATTRIBUTE_DES = "Specifies the attribute, which missing values are replaced.";
    private static final String COLUMN_FILTER_DES = "Selects the function, which is used to determine the replacement for the missing values of this attribute.";
    // Filter names
    private static final String[] FILTER_NAMES = { "none", "minimum", "maximum", "average", "zero", "value" };

    // functions
    public MissingValueReplenishmentOp(OperatorDescription description) { super(description); }
    public String[] getFunctionNames() { return FILTER_NAMES; }
    public int getDefaultFunction() {
        return 3;
    }
    public int getDefaultColumnFunction() {
		return 3;
	}

    //private final OutputPort modelOutput = getOutputPorts().createPort("preprocessing model");
    AttributeSelector selector = new AttributeSelector(this, exampleSetInput);

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.addAll(selector.getParameterTypes());

        // The "default filter" field
        String[] functionNames = getFunctionNames();
        ParameterType filter = new ParameterTypeCategory(
                DEFAULT_FILTER,
                DEFAULT_FILTER_DES,
                functionNames,
                getDefaultFunction());
        filter.setExpert(false);
        types.add(filter);

        // The "specific columns" field
        ParameterTypeAttribute column_attribute = new ParameterTypeAttribute(
                COLUMN_ATTRIBUTE,
                COLUMN_ATTRIBUTE_DES,
                exampleSetInput);
        ParameterTypeStringCategory column_filter = new ParameterTypeStringCategory(
                COLUMN_FILTER,
                COLUMN_FILTER_DES,
                functionNames,
                getFunctionNames()[getDefaultColumnFunction()],
                false);
        column_filter.setEditable(false);
        ParameterTypeList columns = new ParameterTypeList(
                SPECIFIC_COLUMNS,
                SPECIFIC_COLUMNS_DES,
                column_attribute,
                column_filter);
        types.add(columns);

        // The jump-out "replenish value" field
        ParameterTypeString replenish_value = new ParameterTypeString(
                REPLENISH_VALUE,
                REPLENISH_VALUE_DES,
                true,
                false);
        replenish_value.registerDependencyCondition(
                new ParameterCondition(this, DEFAULT_FILTER, true) {
                    @Override
                    public boolean isConditionFullfilled() {
                        try {
                            if (getParameterAsInt(DEFAULT_FILTER) == 5) { return true; }
                            List<String[]> pairs = getParameterList(SPECIFIC_COLUMNS);
                            if (pairs != null) {
                                for (String[] pair : pairs) {
                                    if (pair[1].equals("value") || pair[1].equals("" + 5)) {
                                        return true;
                                    }
                                }
                            }
                        } catch (UndefinedParameterError e) {
                           e.printStackTrace();
                        }
                        return false;
                    }
        });
        types.add(replenish_value);

        return types;
    }
}
