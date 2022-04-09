import data.Country;
import data.Feature;
import knn.Measure;
import knn.Metric;
import lombok.RequiredArgsConstructor;

import java.sql.Array;
import java.util.*;

public class kNN {

    private final int kValue;
    private final double trainingRatio;
    private final ArrayList<Integer> includedFeatures;
    private final Measure measure;
    private final Metric metric;
    private final ArrayList<Extraction> trainingSet;
    private final ArrayList<Extraction> testSet;
    private final HashMap<Integer, Double> featureWeights;

    public kNN(int kValue, double trainingRatio, ArrayList<Integer> includedFeatures, Measure measure, Metric metric,
               List<Extraction> dataSet, HashMap<Integer, Double> featureWeights) {
        this.kValue=kValue;
        this.trainingRatio=trainingRatio;
        this.includedFeatures=includedFeatures;
        this.measure=measure;
        this.metric=metric;
        this.featureWeights=featureWeights;
        int breakpoint = (int) (trainingRatio*dataSet.size());
        this.trainingSet = new ArrayList<>();
        this.testSet = new ArrayList<>();
        for(int i=0;i<dataSet.size();i++) {
            Extraction extraction = org.apache.commons.lang3.SerializationUtils.clone(dataSet.get(i));
            if(i<breakpoint) {
                trainingSet.add(extraction);
            } else {
                testSet.add(extraction);
            }
        }
    }

    public List<HashMap<Country, Country>> runAlgorithm() {
        List<HashMap<Country, Country>> classificationList = new ArrayList<>();
        for (Extraction testVector : testSet) {
            TreeMap<Double, Country> distanceMap = new TreeMap<>();
            for (Extraction trainingVector : trainingSet) {
                distanceMap.put(metric
                        .calcDistance(testVector.getFeatures(), trainingVector.getFeatures(), includedFeatures, featureWeights),
                        trainingVector.getCountry());
            }
            classificationList.add(checkCountry(distanceMap, testVector));
        }
        return classificationList;
    }

    public HashMap<Country, Country> checkCountry(TreeMap<Double, Country> distancesMap, Extraction testVector) {
        HashMap<Country, Integer> checkedCountry = new HashMap<>();
        int i=0;
        for (HashMap.Entry<Double, Country> distanceCountryMap : distancesMap.entrySet()) {
            if(i>=kValue) {
                break;
            }
            checkedCountry.put(distanceCountryMap.getValue(),checkedCountry.getOrDefault(distanceCountryMap.getValue(),0)+1);
            i++;
        }
        HashMap<Country,Country> returnMap = new HashMap<>();
        returnMap.put(testVector.getCountry(),Collections.max(checkedCountry.entrySet(),Map.Entry.comparingByValue()).getKey());
        return returnMap;
    }

}
