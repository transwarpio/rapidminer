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
import com.rapidminer.operator.features.transformation.PCAModel;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.*;
import com.rapidminer.parameter.*;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;
import io.transwarp.midas.constant.midas.params.features.PCAParams;
import io.transwarp.midas.operator.BaseOp;

import java.util.List;
import java.util.logging.Level;

public class PCAOp extends BaseOp{
    public PCAOp(OperatorDescription description) {
        super(description);
        exampleSetInput.addPrecondition(new ExampleSetPrecondition(exampleSetInput, Ontology.NUMERICAL));

        getTransformer().addRule(new GenerateModelTransformationRule(exampleSetInput, modelOutput, PCAModel.class));
        getTransformer().addRule(new ExampleSetPassThroughRule(exampleSetInput, exampleSetOutput, SetRelation.EQUAL) {

            @Override
            public ExampleSetMetaData modifyExampleSet(ExampleSetMetaData metaData) throws UndefinedParameterError {
                int numberOfAttributes = metaData.getNumberOfRegularAttributes();
                int resultNumber = numberOfAttributes;
                if (getParameterAsInt(PCAParams.DimensionalityReduction()) == PCAParams.FixedNumberId()) {
                    resultNumber = getParameterAsInt(PCAParams.NumberOfComponents());
                    int regular_numbers = metaData.getNumberOfRegularAttributes();
                    if (regular_numbers < resultNumber) {
                        LogService.getRoot().log(Level.WARNING,
                                "com.rapidminer.operator.features.transformation.PCA.less_attributes",
                                new Object[] { resultNumber, regular_numbers });
                        resultNumber = regular_numbers;
                    }
                    metaData.attributesAreKnown();
                } else if (getParameterAsInt(PCAParams.DimensionalityReduction()) == PCAParams.KeepVarianceId()) {
                    resultNumber = numberOfAttributes;
                    metaData.attributesAreSubset();
                }
                metaData.clearRegular();
                for (int i = 1; i <= resultNumber; i++) {
                    AttributeMetaData pcAMD = new AttributeMetaData("pc_" + i, Ontology.REAL);
                    pcAMD.setMean(new MDReal(0.0));
                    metaData.addAttribute(pcAMD);
                }
                return metaData;
            }
        });
        getTransformer().addRule(new PassThroughRule(exampleSetInput, originalOutput, false));
    }

    private InputPort exampleSetInput = getInputPorts().createPort("example set input");

    private OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");
    private OutputPort originalOutput = getOutputPorts().createPort("original");
    private OutputPort modelOutput = getOutputPorts().createPort("preprocessing model");

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> list = super.getParameterTypes();
        ParameterType type = new ParameterTypeCategory(PCAParams.DimensionalityReduction(),
                "Indicates which type of dimensionality reduction should be applied", PCAParams.ReductionMethods(), 1);
        type.setExpert(false);
        list.add(type);

        type = new ParameterTypeDouble(PCAParams.VarianceThreshold(),
                "Keep the all components with a cumulative variance smaller than the given threshold.", 0, 1, 0.95);
        type.setExpert(false);
        type.registerDependencyCondition(new EqualTypeCondition(this, PCAParams.DimensionalityReduction(), PCAParams.ReductionMethods(), true,
                PCAParams.KeepVarianceId()));
        list.add(type);

        type = new ParameterTypeInt(PCAParams.NumberOfComponents(), "Keep this number of components.", 1, Integer.MAX_VALUE,
                1);
        type.setExpert(false);
        type.registerDependencyCondition(new EqualTypeCondition(this, PCAParams.DimensionalityReduction(), PCAParams.ReductionMethods(), true,
                PCAParams.FixedNumberId()));
        list.add(type);
        return list;
    }
}
