import data.Country;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class QualityMeasure {

    @Getter
    @Setter
    private List<HashMap<Country, Country>> actualAndPredicted;
    @Getter
    @Setter
    private HashMap<String, Integer> confusionMatrix;

    public QualityMeasure(List<HashMap<Country, Country>> actualAndPredicted) {
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
                confusionMatrix.put(c, confusionMatrix.getOrDefault(c, 0) + 1);
            }
        }
    }

    private int getCountryWeight(Country country) {
        return confusionMatrix.getOrDefault("TP_" + country,0) +
                confusionMatrix.getOrDefault("FN_" + country,0);
    }

    public double calcAccuracy() {
        double sum = 0.0;
        for (Country country : Country.values()) {
            sum += confusionMatrix.getOrDefault("TP_" + country, 0);
        }
        return sum / actualAndPredicted.size();
    }

    public double calcPrecision(Country country) {
        return (confusionMatrix.getOrDefault("TP_" + country, 0) +
                confusionMatrix.getOrDefault("FP_" + country, 0))==0?0:
                (double) confusionMatrix.getOrDefault("TP_" + country, 0) /
                (confusionMatrix.getOrDefault("TP_" + country, 0) +
                        confusionMatrix.getOrDefault("FP_" + country, 0));
    }

    public double calcGlobalPrecision() {
        double numerator = 0.0;
        for (Country country : Country.values()) {
            int weight = getCountryWeight(country);
            numerator += (calcPrecision(country) * weight);
        }
        return numerator / actualAndPredicted.size();
    }

    public double calcRecall(Country country) {
        return (confusionMatrix.getOrDefault("TP_" + country, 0) +
                confusionMatrix.getOrDefault("FN_" + country, 0))==0?0:
                (double) confusionMatrix.getOrDefault("TP_" + country, 0) /
                (confusionMatrix.getOrDefault("TP_" + country, 0) +
                        confusionMatrix.getOrDefault("FN_" + country, 0));
    }

    public double calcGlobalRecall() {
        double numerator = 0.0;
        for (Country country : Country.values()) {
            int weight = getCountryWeight(country);
            numerator += (calcRecall(country) * weight);
        }
        return numerator / actualAndPredicted.size();
    }

    public double calcF1Score(Country country) {
        double tp_c = confusionMatrix.getOrDefault("TP_" + country, 0);
        return (2 * tp_c + confusionMatrix.getOrDefault("FP_" + country, 0) +
                confusionMatrix.getOrDefault("FN_" + country, 0))==0?0:
                (2 * tp_c) /
                (2 * tp_c + confusionMatrix.getOrDefault("FP_" + country, 0) +
                confusionMatrix.getOrDefault("FN_" + country, 0));
    }

    public String generateLatex(String title){
        return "\\begin{table}[H]\n"+
        "\\centering\n"+
        "\\begin{tabularx}{1\\textwidth}{|>{\\arraybackslash} a|>{\\centering\\arraybackslash}X|>{\\centering\\arraybackslash}X|>{\\centering\\arraybackslash}X|>{\\centering\\arraybackslash}X|>{\\centering\\arraybackslash}X|}\n"+
        "\\hline\n"+
        "\\rowcolor[HTML]{DDDDDD}\n"+
        "\\textbf{Kraj}&\\textbf{Accuracy}&\\textbf{Precision}&\\textbf{Recall}&\\textbf{F1}&\\textbf{Liczba tekstów}  \\\\ \n"+
        "\\hline\n"+
        "west-germany & \\cellcolor{almostblack} & "+String.format(Locale.FRANCE,"%,.3f",calcPrecision(Country.westgermany)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcRecall(Country.westgermany)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcF1Score(Country.westgermany)*100)+"\\% & "+getCountryWeight(Country.westgermany)+" \\\\ \n"+
        "\\hline\n"+
        "usa & \\cellcolor{almostblack} & "+String.format(Locale.FRANCE,"%,.3f",calcPrecision(Country.usa)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcRecall(Country.usa)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcF1Score(Country.usa)*100)+"\\% & "+getCountryWeight(Country.usa)+"\\\\ \n"+
        "\\hline\n"+
        "france & \\cellcolor{almostblack} & "+String.format(Locale.FRANCE,"%,.3f",calcPrecision(Country.france)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcRecall(Country.france)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcF1Score(Country.france)*100)+"\\% & "+getCountryWeight(Country.france)+"\\\\ \n"+
        "\\hline\n"+
        "uk & \\cellcolor{almostblack} & "+String.format(Locale.FRANCE,"%,.3f",calcPrecision(Country.uk)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcRecall(Country.uk)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcF1Score(Country.uk)*100)+"\\% & "+getCountryWeight(Country.uk)+"\\\\ \n"+
        "\\hline\n"+
        "canada & \\cellcolor{almostblack} & "+String.format(Locale.FRANCE,"%,.3f",calcPrecision(Country.canada)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcRecall(Country.canada)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcF1Score(Country.canada)*100)+"\\% & "+getCountryWeight(Country.canada)+"\\\\ \n"+
        "\\hline\n"+
        "japan & \\cellcolor{almostblack} & "+String.format(Locale.FRANCE,"%,.3f",calcPrecision(Country.japan)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcRecall(Country.japan)*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcF1Score(Country.japan)*100)+"\\% & "+getCountryWeight(Country.japan)+"\\\\ \n"+
        "\\hline\n"+
        "Razem & \\textbf{"+String.format(Locale.FRANCE,"%,.3f",calcAccuracy()*100)+"\\%} & "+String.format(Locale.FRANCE,"%,.3f",calcGlobalPrecision()*100)+"\\% & "+String.format(Locale.FRANCE,"%,.3f",calcGlobalRecall()*100)+"\\% & \\cellcolor{almostblack} & "+ actualAndPredicted.size()+" \\\\ \n"+
        "\\hline\n"+
        "\\end{tabularx}\n"+
        "\\caption{"+title+"}\n"+
        "\\end{table}\n";
    }

    public String generateBarChart(String filename, String title) throws IOException {

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        dataset.addValue(calcAccuracy(),"Accuracy","Razem");
        dataset.addValue(calcGlobalPrecision(),"Precision","Razem");
        dataset.addValue(calcGlobalRecall(),"Recall","Razem");
        for(Country country: Country.values()) {
            dataset.addValue(calcPrecision(country),"Precision",country.name());
            dataset.addValue(calcRecall(country),"Recall",country.name());
            dataset.addValue(calcF1Score(country),"Miara F1",country.name());
        }



        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                "Kraj", "Wynik miary",
                dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 800;    /* Width of the image */
        int height = 600;   /* Height of the image */
        File BarChart = new File( filename+".png" );
        ChartUtils.saveChartAsPNG(BarChart,barChart,width,height);
        return "\\begin{figure}[H]\n" +
                "\\includegraphics[width=1\\textwidth]{wykresy/"+filename+".png}\n" +
                "\\centering\n" +
                "\\vspace{-0.3cm}\n" +
                "\\caption{"+title+"}\n" +
                "\\end{figure}\n";
    }
}
