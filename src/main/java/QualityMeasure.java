import data.Country;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class QualityMeasure {

    @Getter
    @Setter
    private List<Map<Country, Country>> actualAndPredicted;
    @Getter
    @Setter
    private Map<String, Integer> confusionMatrix;

    public QualityMeasure(List<Map<Country, Country>> actualAndPredicted) {
        this.actualAndPredicted = actualAndPredicted;
        this.confusionMatrix = new HashMap<>();
        setConfusionMatrix();
    }

    private void setConfusionMatrix() {
        for (Country country : Country.values()) {
            for (Map<Country, Country> actAndPred : actualAndPredicted) {
                String c;
                if (actAndPred.containsKey(country)) {
                    if (actAndPred.containsValue(country)) {
                        c = "TP_" + country;
                    } else {
                        c = "FN_" + country;
                    }
                } else if (actAndPred.containsValue(country)) {
                    c = "FP_" + country;
                } else {
                    c = "TN_" + country;
                }
                confusionMatrix.put(c, confusionMatrix.getOrDefault(c, 1) + 1);
            }
        }
    }

    public double calcAccuracy() {
        double sum = 0.0;
        for (Country country : Country.values()) {
            sum += confusionMatrix.getOrDefault("TP_" + country, 0);
        }
        return sum / actualAndPredicted.size();
    }

    public double calcPrecision(Country country) {
        return (double) confusionMatrix.getOrDefault("TP_" + country, 0) /
                (confusionMatrix.getOrDefault("TP_" + country, 0) +
                        confusionMatrix.getOrDefault("FP_" + country, 0));
    }

    public double calcGlobalPrecision() {
        double numerator = 0.0;
        double denominator = 0.0;
        for (Country country : Country.values()) {
            numerator += (calcPrecision(country) * actualAndPredicted.size());
            denominator += actualAndPredicted.size();
        }
        return numerator / denominator;
    }

    public double calcRecall(Country country) {
        return (double) confusionMatrix.getOrDefault("TP_" + country, 0) /
                (confusionMatrix.getOrDefault("TP_" + country, 0) +
                        confusionMatrix.getOrDefault("FN_" + country, 0));
    }

    public double calcGlobalRecall() {
        double numerator = 0.0;
        double denominator = 0.0;
        for (Country country : Country.values()) {
            numerator += (calcRecall(country) * actualAndPredicted.size());
            denominator += actualAndPredicted.size();
        }
        return numerator / denominator;
    }

    public double calcF1Score(Country country) {
        double tp_c = confusionMatrix.getOrDefault("TP_" + country, 0);
        return (2 * tp_c) /
                (2 * tp_c + confusionMatrix.getOrDefault("FP_" + country, 0) +
                confusionMatrix.getOrDefault("FN_" + country, 0));
    }

}
