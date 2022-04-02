package knn;

import data.Feature;

import java.util.HashMap;
import java.util.List;

public class Chebyshev implements Metric {
    @Override
    public double calcDistance(List<Feature> vector1, List<Feature> vector2) {
        double maxDist = 0;
        for (int i = 0; i < vector1.size(); i++) {
            double distance = Math.abs(vector1.get(i).getDoubleValue() - vector2.get(i).getDoubleValue());
            maxDist = Math.max(distance, maxDist);
        }
        return maxDist;
    }
}
