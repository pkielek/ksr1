package knn;

import data.Feature;

import java.util.ArrayList;
import java.util.HashMap;

public interface Metric {
    double calcDistance(HashMap<Integer, Feature> vector1, HashMap<Integer, Feature> vector2, ArrayList<Integer> includedFeatures, HashMap<Integer, Double> featureWeights);
}
