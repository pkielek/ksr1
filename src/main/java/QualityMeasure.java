import data.Country;
import java.util.Map;
import java.util.List;

public class QualityMeasure {

    private List<Map<Country, Country>> actualAndPredicted;
    private Map<String, Integer> confusionMatrix;

    public QualityMeasure(List<Map<Country, Country>> actualAndPredicted) {
        this.actualAndPredicted = actualAndPredicted;
    }

    void setConfusionMatrix() {
//        for (Country country : Country.values()) {
//
//        }
    }

    double calcAccuracy() {
        double sum=0.0;
        for(Country country : Country.values()) {
            sum += confusionMatrix.get("TP_"+country.name());
        }
        return sum/actualAndPredicted.size();
    }

    double calcPrecision(Country country) {
        return 0;
    }

    double calcGlobalPrecision() {
        return 0;
    }

    double calcRecall(Country country) {
        return 0;
    }

    double calcGlobalRecall() {
        return 0;
    }

    double calcF1Score(Country country) {
        return 0;
    }


}
