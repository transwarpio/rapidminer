/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
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
package com.rapidminer.operator.clustering;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.math.similarity.DistanceMeasure;
import io.transwarp.midas.adaptor.ICentroid;
import io.transwarp.midas.adaptor.ICentroidClusterModel;
import io.transwarp.midas.adaptor.ICluster;
import io.transwarp.midas.adaptor.IExampleSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * This is the superclass for all centroid based cluster models and supports assigning unseen
 * examples to the nearest centroid.
 * 
 * @author Sebastian Land
 */
public class CentroidClusterModel extends ClusterModel implements Serializable, ICentroidClusterModel{

	private static final long serialVersionUID = 3780908886210272852L;

	private Collection<String> dimensionNames;
	private List<Centroid> centroids;
	private DistanceMeasure distanceMeasure;

	@Override
	public ICluster getICluster(int idx) {
		return getCluster(idx);
	}

	public CentroidClusterModel(IExampleSet exampleSet, int k, Collection<String> dimensionNames,
								DistanceMeasure distanceMeasure, boolean addClusterAsLabel, boolean removeUnknown) {
		super(exampleSet, k, addClusterAsLabel, removeUnknown);
		this.distanceMeasure = distanceMeasure;
		this.dimensionNames = dimensionNames;
		centroids = new ArrayList<Centroid>(k);
		for (int i = 0; i < k; i++) {
			centroids.add(new Centroid(dimensionNames.size()));
		}
	}

	public CentroidClusterModel(ExampleSet exampleSet, int k, Attributes attributes, boolean addClusterAsLabel,
			boolean removeUnknown) {
		super(exampleSet, k, addClusterAsLabel, removeUnknown);
		List<String> dimensionNames = new LinkedList<String>();
		for (Attribute attribute : attributes) {
			dimensionNames.add(attribute.getName());
		}
		this.dimensionNames = dimensionNames;
		centroids = new ArrayList<Centroid>(k);
		for (int i = 0; i < k; i++) {
			centroids.add(new Centroid(dimensionNames.size()));
		}
	}

	@Override
	public int[] getClusterAssignments(ExampleSet exampleSet) {
		int[] clusterAssignments = new int[exampleSet.size()];
		Attribute[] attributes = new Attribute[dimensionNames.size()];
		int i = 0;
		for (String attributeName : dimensionNames) {
			attributes[i] = exampleSet.getAttributes().get(attributeName);
			i++;
		}

		double[] exampleValues = new double[attributes.length];
		int exampleIndex = 0;
		for (Example example : exampleSet) {
			// copying examplevalues into double array
			for (i = 0; i < attributes.length; i++) {
				exampleValues[i] = example.getValue(attributes[i]);
			}
			// searching for nearest centroid
			int centroidIndex = 0;
			int bestIndex = 0;
			double minimalDistance = Double.POSITIVE_INFINITY;
			for (Centroid centroid : centroids) {
				double distance = distanceMeasure.calculateDistance(exampleValues, centroid.getCentroid());
				if (distance < minimalDistance) {
					bestIndex = centroidIndex;
					minimalDistance = distance;
				}
				centroidIndex++;
			}
			clusterAssignments[exampleIndex] = bestIndex;
			exampleIndex++;
		}
		return clusterAssignments;
	}

	/* This model does not need ids */
	@Override
	public void checkCapabilities(ExampleSet exampleSet) throws OperatorException {

	}

	public String[] getAttributeNames() {
		return dimensionNames.toArray(new String[dimensionNames.size()]);
	}

	/**
	 * Returns the List of all defined centroids.
	 */
	public List<Centroid> getCentroids() {
		return centroids;
	}

	/**
	 * set the list of all defined centroids
	 */
	public void setCentroids(List<Centroid> centroids_) {
		centroids = centroids_;
	}

	public double[] getCentroidCoordinates(int i) {
		return centroids.get(i).getCentroid();
	}

	public Centroid getCentroid(int i) {
		return centroids.get(i);
	}

	/**
	 * This method assigns the given doubleArray to the cluster with the given index. Centroids are
	 * calculated over all assigned arrays.
	 */
	public void assignExample(int clusterIndex, double[] example) {
		centroids.get(clusterIndex).assignExample(example);
	}

	public boolean finishAssign() {
		boolean stable = true;
		for (Centroid centroid : centroids) {
			stable &= centroid.finishAssign();
		}
		return stable;
	}

	public DistanceMeasure getDistanceMeasure() {
		return distanceMeasure;
	}

	@Override
	public String getExtension() {
		return "ccm";
	}

	@Override
	public String getFileDescription() {
		return "Centroid based cluster model";
	}

	@Override
	public void setICentroids(List<ICentroid> centroids_) {
		List<Centroid> convert = new ArrayList<Centroid>();
		for( ICentroid centroid: centroids_){
			convert.add((Centroid) centroid);
		}
		centroids = convert;
		if (!centroids.isEmpty()) {
			for (int i = 0; i < centroids.get(0).getCentroid().length; i++) {
				dimensionNames.add("col" + (i + 1));
			}
		}
	}
}
