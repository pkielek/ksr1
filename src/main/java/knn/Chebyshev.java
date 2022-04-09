package knn;

import data.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chebyshev implements Metric {
    @Override
    public double calcDistance(HashMap<Integer,Feature> vector1, HashMap<Integer,Feature> vector2, ArrayList<Integer> includedFeatures) {
        double maxDist = 0.0;
        Measure measure = new NGramMeasure();
        for (Integer i : includedFeatures) {
            double distance;
            if (vector1.get(i).getIsTextFeature()) {
                distance = 1 - measure.compare(vector1.get(i).getTextValue(), vector2.get(i).getTextValue());
            } else {
                distance = Math.abs(vector1.get(i).getDoubleValue() - vector2.get(i).getDoubleValue());
            }
            maxDist = Math.max(distance, maxDist);
        }
        return maxDist;
    }
}
