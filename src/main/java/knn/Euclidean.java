package knn;

import data.Feature;

import java.util.HashMap;
import java.util.List;

public class Euclidean implements Metric {
    @Override
    public double calcDistance(HashMap<Integer,Feature> vector1, HashMap<Integer, Feature> vector2) {
        double distance = 0;
        for (int i = 0; i < vector1.size(); i++) {
            distance += (vector1.get(i).getDoubleValue() - vector2.get(i).getDoubleValue()) *
                    (vector1.get(i).getDoubleValue() - vector2.get(i).getDoubleValue());
        }
        return Math.sqrt(distance);
    }
}
