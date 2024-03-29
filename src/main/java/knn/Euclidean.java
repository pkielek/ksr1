package knn;

import data.Feature;

import java.util.ArrayList;
import java.util.HashMap;

public class Euclidean implements Metric {
    @Override
    public double calcDistance(HashMap<Integer, Feature> vector1, HashMap<Integer, Feature> vector2, ArrayList<Integer> includedFeatures, HashMap<Integer, Double> featureWeights) {
        double distance = 0;
        Measure measure = new NGramMeasure();
        for (Integer i : includedFeatures) {
            if (vector1.get(i).getIsTextFeature()) {
                distance +=
                        Math.pow((1 - measure.compare(vector1.get(i).getTextValue(), vector2.get(i).getTextValue())) * featureWeights.get(i), 2);
            } else {
                distance += Math.pow(((vector1.get(i).getDoubleValue() - vector2.get(i).getDoubleValue()) * featureWeights.get(i)), 2) ;
            }
        }
        return Math.sqrt(distance);
    }
}
