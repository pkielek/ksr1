import data.Country;
import knn.Measure;
import knn.Metric;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class kNN {

    private int kValue;
    private double trainingRatio;
    private List<Integer> includedFeatures;
    private Measure measure;
    private Metric metric;
    private List<Extraction> trainingSet;
    private List<Extraction> testSet;
    private Map<Integer, Double> featureWeights;

    public List<Map<Country, Country>> distance() {
        List<Map<Country, Country>> buff = new ArrayList<>();
        for (Extraction testVector : testSet) {
            TreeMap<Double, Country> buff2 = new TreeMap<>();
            for (Extraction trainingVector : trainingSet) {
                buff2.put(metric
                        .calcDistance(testVector.getFeatures(), trainingVector.getFeatures()),
                        trainingVector.getCountry());
            }
            buff.add(checkCountry(buff2, testVector));
        }
        return buff;
    }

    public Map<Country, Country> checkCountry(TreeMap<Double, Country> buff2, Extraction testVector) {
        Map<Country, Country> checkedCountry = new HashMap<>();
        for (Map.Entry<Double, Country> buff3 : buff2.entrySet()) {
            checkedCountry.put(testVector.getCountry(), buff3.getValue());
            if (checkedCountry.size() == 8) {
                break;
            }
        }
        return checkedCountry;
    }

}
