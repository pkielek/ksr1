package knn;

import data.Feature;

import java.util.HashMap;
import java.util.List;

public interface Metric {
    double calcDistance(HashMap<Integer,Feature> vector1, HashMap<Integer,Feature> vector2);
}
