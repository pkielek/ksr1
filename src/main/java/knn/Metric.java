package knn;

import data.Feature;

import java.util.List;

public interface Metric {
    double calcDistance(List<Feature> vector1, List<Feature> vector2);
}
