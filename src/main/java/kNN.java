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
            ArrayList<Integer> removedFeatures = new ArrayList<>();
            extraction.getFeatures().forEach((k,v) -> {
                if(!includedFeatures.contains(k)) {
                    removedFeatures.add(k);
                }
                else if(featureWeights.containsKey(k) && !extraction.getFeatures().get(k).getIsTextFeature()) {
                    extraction.getFeatures().get(k).setDoubleValue(extraction.getFeatures().get(k).getDoubleValue()
                            *featureWeights.get(k));
                }
            });
            removedFeatures.forEach(extraction.getFeatures().keySet()::remove);
            if(!removedFeatures.isEmpty()) {
                System.out.println(removedFeatures);
                System.out.println(extraction.getFeatures());
            }
            if(i<breakpoint) {
                trainingSet.add(extraction);
            } else {
                testSet.add(extraction);
            }
        }
    }

    public List<HashMap<Country, Country>> distance() {
        List<HashMap<Country, Country>> buff = new ArrayList<>();
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

    public HashMap<Country, Country> checkCountry(TreeMap<Double, Country> buff2, Extraction testVector) {
        HashMap<Country, Integer> checkedCountry = new HashMap<>();
        int i=0;
        for (HashMap.Entry<Double, Country> buff3 : buff2.entrySet()) {
            if(i>=kValue) {
                break;
            }
            checkedCountry.put(buff3.getValue(),checkedCountry.getOrDefault(buff3.getValue(),0)+1);
            i++;
        }
        HashMap<Country,Country> returnMap = new HashMap<>();
        returnMap.put(testVector.getCountry(),Collections.max(checkedCountry.entrySet(),Map.Entry.comparingByValue()).getKey());
        return returnMap;
    }

}
