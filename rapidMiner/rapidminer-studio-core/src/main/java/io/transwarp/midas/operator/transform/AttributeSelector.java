package io.transwarp.midas.operator.transform;

import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.preprocessing.filter.attributes.*;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import com.rapidminer.tools.Ontology;
import io.transwarp.midas.constant.midas.params.data.SelectAttrsParams;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AttributeSelector {
	public static String[] CONDITION_NAMES = new String[] { "all", "single", "subset", "regular_expression"};

	private static Class<?>[] CONDITION_IMPLEMENTATIONS = { TransparentAttributeFilter.class, SingleAttributeFilter.class,
			SubsetAttributeFilter.class, MidasRegexpAttributeFilter.class};

	private final ParameterHandler operator;

	private final InputPort inPort;

	private int[] valueTypes;

	public AttributeSelector(ParameterHandler operator, InputPort inPort) {
		this.operator = operator;
		this.inPort = inPort;
		this.valueTypes = new int[] { Ontology.ATTRIBUTE_VALUE };
	}
		/**
	 * This method creates the parameter types needed to filter attributes from example sets.
	 */
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new LinkedList<>();
		ParameterType type = new ParameterTypeCategory(SelectAttrsParams.AttributeFilterType(),
				"The condition specifies which attributes are selected or affected by this operator.", CONDITION_NAMES, 0);
		type.setExpert(false);
		types.add(type);

		for (int i = 0; i < CONDITION_IMPLEMENTATIONS.length; i++) {
			Collection<ParameterType> filterConditions;
			try {
				filterConditions = ((AttributeFilterCondition) CONDITION_IMPLEMENTATIONS[i].newInstance())
						.getParameterTypes(operator, inPort, valueTypes);
				for (ParameterType conditionalType : filterConditions) {
					types.add(conditionalType);
					conditionalType.registerDependencyCondition(new EqualTypeCondition(operator, SelectAttrsParams.AttributeFilterType(),
							CONDITION_NAMES, !conditionalType.isExpert(), i));
				}
				// can't do anything about it
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (SecurityException e) {
			}
		}

		type = new ParameterTypeBoolean(SelectAttrsParams.InvertSelection(),
				"Indicates if only attributes should be accepted which would normally filtered.", false);
		type.setExpert(false);
		types.add(type);

		return types;
	}
}
