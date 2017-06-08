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
package io.transwarp.midas.impl;

import com.rapidminer.example.Example;
import com.rapidminer.operator.learner.tree.AbstractSplitCondition;
import io.transwarp.midas.adaptor.ICondition;
import io.transwarp.midas.adaptor.ISchema;
import io.transwarp.midas.adaptor.model.tree.ISplitCondition;

import java.util.List;

public class SplitCondition extends AbstractSplitCondition implements ISplitCondition {
    private ICondition condition;
    private String attributeName;
    public SplitCondition(ICondition condition, List<ISchema> schemas) {
        super(condition.getFeature());
        this.condition = condition;
        this.attributeName = schemas.get(Integer.parseInt(condition.getFeature())).getName();
    }
    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String getRelation() {
        return condition.getRelation();
    }

    @Override
    public String getValueString() {
        return condition.getValue();
    }

    @Override
    public boolean test(Example example) {
        return false;
    }
}
