package knn;

import data.Feature;

import java.util.List;

public class Manhattan implements Metric {
    @Override
    public double calcDistance(List<Feature> vector1, List<Feature> vector2) {
        double distance = 0;
        for (int i = 0; i < vector1.size(); i++) {
            distance += Math.abs(vector1.get(i).getDoubleValue() - vector2.get(i).getDoubleValue());
        }
        return distance;
    }
}
