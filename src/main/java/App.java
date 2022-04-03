import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.*;

import data.Article;
import data.Country;
import knn.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class App {
    // k: 2 3 5 8 10 15 20 25 30 35
    // training ratio: 0.2,0.35,0.5,0.65,0.8
    // metrics:all
    // 4 feature sets: 1-6,all-{1,4},all-{6},2-5
    public static void runSimulations() throws IOException, ParseException {
        Parser parser = new Parser();
        for(int i=0;i<=20;i++) {
            parser.loadArticles(new File("reuters/reut2-0"+(i<10?"0":"")+i+".sgm"),0,200);
        }
        System.out.println("Liczba wczytanych artykułów: "+parser.getLoadedArticles().size());
        ArrayList<Extraction> extractions = new ArrayList<>();
        for(Article article : parser.getLoadedArticles()) {
            extractions.add(new Extraction(article));
        }
        Collections.shuffle(extractions, new Random(1000));
        int baseKValue = 8;
        double baseTrainingRatio = 0.35;
        ArrayList<Integer> baseIncludedFeatures = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12));
        NGramMeasure baseMeasure = new NGramMeasure();
        Euclidean baseMetric = new Euclidean();
        HashMap<Integer, Double> baseWeights = new HashMap<>();
        baseWeights.put(1,1.0/1440.0);
        baseWeights.put(3,0.5);
        baseWeights.put(4,0.15);
        baseWeights.put(5,0.5);
        baseWeights.put(7,0.4);
        baseWeights.put(8,0.4);
        baseWeights.put(9,0.4);
        baseWeights.put(10,0.4);
        baseWeights.put(11,0.4);
        baseWeights.put(12,0.4);

        int chartWidth=800;
        int chartHeight=600;
        ArrayList<Integer> expertimentalKValues = new ArrayList<>(Arrays.asList(2,3,5,8,10,15,20,25,30,35));
        ArrayList<Double> experimentalTrainingRatio = new ArrayList<>(Arrays.asList(0.2,0.35,0.5,0.65,0.8));
        ArrayList<Metric> experimentalMetric = new ArrayList<>(Arrays.asList(new Euclidean(),new Chebyshev(), new Manhattan()));
        ArrayList<String> metricNames = new ArrayList<>(Arrays.asList("Euklidesa","Czebyszewa","Manhattana"));
        ArrayList<ArrayList<Integer>> experimentalFeatureSets = new ArrayList<>();
        ArrayList<Integer> fset1 = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
        experimentalFeatureSets.add(fset1);
        ArrayList<Integer> fset2 = new ArrayList<>(Arrays.asList(2,3,5,6,7,8,9,10,11,12));
        experimentalFeatureSets.add(fset2);
        ArrayList<Integer> fset3 = new ArrayList<>(Arrays.asList(1,2,3,4,5,7,8,9,10,11,12));
        experimentalFeatureSets.add(fset3);
        ArrayList<Integer> fset4 = new ArrayList<>(Arrays.asList(2,3,4,5));
        experimentalFeatureSets.add(fset4);

        StringBuilder latex = new StringBuilder();

        System.out.println("Eksperyment 1: porównanie wartości miary Accuracy dla różnych wartośći parametru k");
        latex.append("\\subsection{Eksperyment 1}");
        latex.append("\\subsubsection{Wyniki}");
        DefaultCategoryDataset e1_dataset = new DefaultCategoryDataset();
        for(Integer kvalue : expertimentalKValues) {
            System.out.println("Obecna wartość k: "+kvalue);
            kNN algorithm = new kNN(kvalue,baseTrainingRatio,baseIncludedFeatures,baseMeasure,baseMetric,extractions,baseWeights);
            QualityMeasure QM = new QualityMeasure(algorithm.distance());
            e1_dataset.addValue(QM.calcAccuracy(), "Accuracy",kvalue);
            String title = "Wyniki klasyfikacji dla parametrów domyślnych oraz k="+kvalue;
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("k_"+kvalue,title));
        }
        JFreeChart e1_chart = ChartFactory.createLineChart(
                "Wartość miary Accuracy dla różnych wartości parametru k","Wartość parametru k",
                "Wartość accuracy",e1_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e1_file = new File("k_summary.png");
        ChartUtils.saveChartAsPNG(e1_file,e1_chart,chartWidth,chartHeight);
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/k_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Zmiana wartośći miary Accuracy dla zmiany wartości parametru k}
                \\end{figure}""");

        System.out.println("Eksperyment 2: porównanie wartości miary Accuracy dla różnych proporcji podziału zbioru");
        latex.append("\\subsection{Eksperyment 2}");
        latex.append("\\subsubsection{Wyniki}");
        DefaultCategoryDataset e2_dataset = new DefaultCategoryDataset();
        for(Double ratio : experimentalTrainingRatio) {
            int ratioInInteger = (int) (ratio*100);
            System.out.println("Obecny podział: zbiór uczący "+ratioInInteger+"%, zbiór testowy "+(100-ratioInInteger)+"%");
            kNN algorithm = new kNN(baseKValue,ratio,baseIncludedFeatures,baseMeasure,baseMetric,extractions,baseWeights);
            QualityMeasure QM = new QualityMeasure(algorithm.distance());
            e2_dataset.addValue(QM.calcAccuracy(), "Accuracy",ratioInInteger+"/"+(100-ratioInInteger));
            String title = "Wyniki klasyfikacji dla parametrów domyślnych oraz podziału na zbiór uczący "+ratioInInteger+"%, zbiór testowy "+(100-ratioInInteger)+"%";
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("ratio_"+ratio,title));
        }
        JFreeChart e2_chart = ChartFactory.createBarChart(
                "Wartość miary Accuracy dla różnej proporci podziału na zbiór uczący i zbiór testowy","" +
                        "Podział na zbiór uczący/zbiór testowy (w procentach)",
                "Wartość accuracy",e2_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e2_file = new File("ratio_summary.png");
        ChartUtils.saveChartAsPNG(e2_file,e2_chart,chartWidth,chartHeight);
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/metric_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Zmiana wartośći miary Accuracy ze względu na metrykę}
                \\end{figure}""");

        System.out.println("Eksperyment 3: porównanie wartości miary Accuracy dla wszystkich badanych metryk");
        latex.append("\\subsection{Eksperyment 3}");
        latex.append("\\subsubsection{Wyniki}");
        DefaultCategoryDataset e3_dataset = new DefaultCategoryDataset();
        for(int i=0;i<=2;i++) {
            System.out.println("Obecna metryka: "+metricNames.get(i));
            kNN algorithm = new kNN(baseKValue,baseTrainingRatio,baseIncludedFeatures,baseMeasure,experimentalMetric.get(i),extractions,baseWeights);
            QualityMeasure QM = new QualityMeasure(algorithm.distance());
            e3_dataset.addValue(QM.calcAccuracy(), "Accuracy",metricNames.get(i));
            String title = "Wyniki klasyfikacji dla parametrów domyślnych oraz metryki "+metricNames.get(i);
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("metric_"+metricNames.get(i),title));
        }
        JFreeChart e3_chart = ChartFactory.createBarChart(
                "Wartość miary Accuracy dla każdej z badanych metryk","Wybrana metryka",
                "Wartość accuracy",e3_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e3_file = new File("metric_summary.png");
        ChartUtils.saveChartAsPNG(e3_file,e3_chart,chartWidth,chartHeight);
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/metric_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Zmiana wartośći miary Accuracy ze względu na metrykę}
                \\end{figure}""");

        System.out.println("Eksperyment 4: porównanie wartości miary Accuracy dla różnych podzbiorów cech");
        latex.append("\\subsection{Eksperyment 4}");
        latex.append("\\subsubsection{Wyniki}");

        DefaultCategoryDataset e4_dataset = new DefaultCategoryDataset();
        for(int i=0;i<4;i++) {
            System.out.println("Obecna podzbiór cech: "+(i+1));
            kNN algorithm = new kNN(baseKValue,baseTrainingRatio,experimentalFeatureSets.get(0),baseMeasure,baseMetric,extractions,baseWeights);
            QualityMeasure QM = new QualityMeasure(algorithm.distance());
            e4_dataset.addValue(QM.calcAccuracy(), "Accuracy","Zbiór "+(i+1));
            String title = "Wyniki klasyfikacji dla parametrów domyślnych oraz podzbioru cech nr "+(i+1);
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("feature_"+(i+1),title));
        }
        JFreeChart e4_chart = ChartFactory.createLineChart(
                "Wartość miary Accuracy dla każdego z 4 podzbiorów cech","Podzbiór cech",
                "Wartość accuracy",e4_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e4_file = new File("feature_summary.png");
        ChartUtils.saveChartAsPNG(e4_file,e4_chart,chartWidth,chartHeight);
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/feature_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Zmiana wartośći miary Accuracy ze względu na wybór podzbioru cech}
                \\end{figure}""");

        System.out.println(latex);
    }

    public static void main(String[] args) throws IOException, ParseException {
        runSimulations();
        // TODO Interfejs CLI
//        Scanner scanner = new Scanner(System.in);
//        Parser parser = new Parser();
//        parser.loadArticles(new File("reuters/reut2-000.sgm"),123,1);
//        parser.loadArticles(new File("reuters/reut2-000.sgm"),126,1);
//        Extraction e1 = new Extraction(parser.getLoadedArticles().get(0));
//        Extraction e2 = new Extraction(parser.getLoadedArticles().get(1));
//        System.out.println(e1.getFeatures());
//        System.out.println(e2.getFeatures());
//        Measure measure = new NGramMeasure();
//        System.out.println(measure.compare("unicocoa", "coconut"));
//        List<Map<Country, Country>> countryMap = new ArrayList<>();
//        countryMap.add(Map.of(Country.canada, Country.canada));
//        countryMap.add(Map.of(Country.canada, Country.uk));
//        countryMap.add(Map.of(Country.canada, Country.usa));
//        countryMap.add(Map.of(Country.canada, Country.japan));
//        countryMap.add(Map.of(Country.canada, Country.westgermany));
//        countryMap.add(Map.of(Country.canada, Country.france));
//        countryMap.add(Map.of(Country.usa, Country.canada));
//        countryMap.add(Map.of(Country.usa, Country.uk));
//        countryMap.add(Map.of(Country.usa, Country.usa));
//        countryMap.add(Map.of(Country.usa, Country.japan));
//        countryMap.add(Map.of(Country.usa, Country.westgermany));
//        countryMap.add(Map.of(Country.usa, Country.france));
//        countryMap.add(Map.of(Country.usa, Country.usa));
//        countryMap.add(Map.of(Country.usa, Country.japan));
//        countryMap.add(Map.of(Country.usa, Country.westgermany));
//
//        QualityMeasure qualityMeasure = new QualityMeasure(countryMap);
//        System.out.println(qualityMeasure.calcAccuracy());
//        System.out.println(qualityMeasure.calcGlobalPrecision());
//        System.out.println(qualityMeasure.calcGlobalRecall());
//        System.out.println(qualityMeasure.calcF1Score(Country.usa));

//        System.out.println("Wybierz wariant programu:");
//        System.out.println("1 - ręczne wprowadzanie parametrów");
//        System.out.println("2 - skonfigurowany zestaw eksperymentów");
//        System.out.print("Twój wybór:");
//        scanner.nextLine();
//        System.out.print("Podaj wartość k dla algorytmu k-NN (liczbę sąsiadów):");
//        scanner.nextLine();
//        System.out.println("Pomyłka w trakcie wprowadzania parametrów? wpisz Z by cofnąć.");
//        System.out.print("Podaj udział procentowy zbioru uczącego w procesie klasyfikacji (wartość całkowita z przedziału 1-99):");
//        scanner.nextLine();
//        System.out.println("Wybierz metrykę według której będzie liczona odległość w metodzie k-NN.");
//        System.out.println("1 - euklidesowa");
//        System.out.println("2 - uliczna");
//        System.out.println("3 - Czebyszewa");
//        System.out.print("Twój wybór:");
//        scanner.nextLine();
//
//        System.out.println("Wpisz po kolei cechy które chcesz zastosować:");
//        System.out.println("A - zastosuj wszystkie cechy");
//        System.out.println("L - zobacz listę pozostałych cech");
//        System.out.print("Twój wybór:");
//        scanner.nextLine();
//        System.out.println("1 - Czas publikacji");
//        System.out.println("2 - Pierwsze słowo w tekście, które znajduje się w słowniku topics");
//        System.out.println("3 - Liczba wystąpień organizacji z słownika orgs");
//        System.out.println("4 - Średnia długość słowa zaczynającego się z wielkiej litery (po odrzuceniu pierwszych słów w zdaniu i nazw miesięcy)");
//        System.out.println("5 - Wartość binarna, określająca, czy w tekście występuje słowo ”nuclear”");
//        System.out.println("6 - Nazwa kraju, którego liczba wystąpień skrótu, nazwy i/lub przymiotnika jest największa (na bazie słownika countries)");
//        System.out.println("7 - Liczba wystąpień nazwisk dla słownika people_west-germany");
//        System.out.println("8 - Liczba wystąpień nazwisk dla słownika people_usa");
//        System.out.println("9 - Liczba wystąpień nazwisk dla słownika people_france");
//        System.out.println("10 - Liczba wystąpień nazwisk dla słownika people_uk");
//        System.out.println("11 - Liczba wystąpień nazwisk dla słownika people_canada");
//        System.out.println("12 - Liczba wystąpień nazwisk dla słownika people_japan");
//        System.out.print("Podaj cechę lub zakończ wybór wpisując X:");
//        scanner.nextLine();
//
//        System.out.print("Podaj cechę lub zakończ wybór wpisując X:");
//        scanner.nextLine();
//        System.out.print("Podaj cechę lub zakończ wybór wpisując X:");
//        scanner.nextLine();
//        System.out.println("Wybrane cechy:1,4");
//
//        System.out.println("Jeżeli chcesz ustalić wagę dla którejś z cech wpisz jej numer (domyślna waga - 1), jeżeli chcesz przejść dalej wpisz X");
//        System.out.print("Twój wybór:");
//        scanner.nextLine();
//        System.out.print("Podaj wagę:");
//        scanner.nextLine();
//
//        System.out.print("Podaj kolejny numer cechy by zmienić jej wagę, lub zakończ wpisując X:");
//        scanner.nextLine();
//
//        System.out.println("Wybierz pliki użyte w klasyfikacji (numer pliku z zakresu od 0-21), lub A - wszystkie pliki.");
//        System.out.print("Twój wybór:");
//        scanner.nextLine();
//
//        System.out.print("0 - użycie całego pliku w procesie klasyfikacji, 1 - wybranie przedziału artykułów:");
//        scanner.nextLine();
//
//        System.out.print("Podaj od którego (prawidłowego) artykułu zacząć wczytywanie, 0 oznacza od początku:");
//        scanner.nextLine();
//
//        System.out.print("Podaj limit wczytanych artykułów (artykułów zostanie wczytanych mniej, jeżeli plik się skończy):");
//        scanner.nextLine();
//
//        System.out.println("Wczytano 181 artykułów");
//        System.out.print("Podaj kolejny plik lub zakończ wpisując X:");
//        scanner.nextLine();
//        System.out.print("0 - użycie całego pliku w procesie klasyfikacji, 1 - wybranie przedziału artykułów:");
//        scanner.nextLine();
//        System.out.print("Podaj kolejny plik lub zakończ wpisując X:");
//        scanner.nextLine();
//        System.out.println("Wybierz sposób prezentacji wyników");
//        System.out.println("1 - Tekstowo");
//        System.out.println("2 - Kod LaTeX");
//        System.out.print("Twój wybór:");
//        scanner.nextLine();
//        System.out.println("Klasyfikacja trwa...");
//        System.out.println("Klasyfikacja zakończona, uzyskane wyniki:");
//        System.out.println("Liczba zbadanych artykułów: 800");
//        System.out.println("Accuracy: 80.0%");
//        System.out.println("Precision dla west-germany: 80.0%, 80/100");
//        System.out.println("Precision dla usa: 80.0%, 160/200");
//        System.out.println("Precision dla france: 80.0%, 80/100");
//        System.out.println("Precision dla uk: 80.0%, 160/200");
//        System.out.println("Precision dla canada: 80.0%, 80/100");
//        System.out.println("Precision dla japan: 80.0%, 80/100");
//        System.out.println("Precision ogólne: 80.0% 640/800");
//        System.out.println("Recall dla west-germany: 80.0%, 80/100");
//        System.out.println("Recall dla usa: 80.0%, 160/200");
//        System.out.println("Recall dla france: 80.0%, 80/100");
//        System.out.println("Recall dla uk: 80.0%, 160/200");
//        System.out.println("Recall dla canada: 80.0%, 80/100");
//        System.out.println("Recall dla japan: 80.0%, 80/100");
//        System.out.println("Recall ogólne: 80.0% 640/800");
//        System.out.println("Miara F1 dla west-germany: 80.0%, 80/100");
//        System.out.println("Miara F1 dla usa: 80.0%, 160/200");
//        System.out.println("Miara F1 dla france: 80.0%, 80/100");
//        System.out.println("Miara F1 dla uk: 80.0%, 160/200");
//        System.out.println("Miara F1 dla canada: 80.0%, 80/100");
//        System.out.println("Miara F1 dla japan: 80.0%, 80/100");





    }
}
