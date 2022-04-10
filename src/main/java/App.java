import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.*;

import data.Article;
import data.Country;
import knn.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class App {
    // k: 2 3 5 8 10 15 20 25 30 35
    // training ratio: 0.2,0.35,0.5,0.65,0.8
    // metrics:all
    // 4 feature sets: 1-6,all-{1,4},all-{6},all-{2,3,5}
    public static void runSimulations() throws IOException, ParseException {
        Parser parser = new Parser();
        for(int i=0;i<=21;i++) {
            parser.loadArticles(new File("reuters/reut2-0"+(i<10?"0":"")+i+".sgm"));
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
        ArrayList<Double> baseWeightsList = new ArrayList<>(Arrays.asList(1.0/1440.0,1.0,0.5,0.15,0.5,1.0,0.4,0.4,0.4,0.4,0.4,0.4));
        Double weightSum = baseWeightsList.stream().reduce(0.0,Double::sum);
        for(int i=1;i<=12;i++) {
            baseWeights.put(i,baseWeightsList.get(i-1)/weightSum);
        }

        int chartWidth=800;
        int chartHeight=600;
        ArrayList<Integer> expertimentalKValues = new ArrayList<>(Arrays.asList(2,3,5,8,10,15,20,25,30,35));
        ArrayList<Double> experimentalTrainingRatio = new ArrayList<>(Arrays.asList(0.2,0.35,0.5,0.65,0.8));
        ArrayList<Metric> experimentalMetric = new ArrayList<>(Arrays.asList(new Euclidean(),new Chebyshev(), new Manhattan()));
        ArrayList<String> metricNames = new ArrayList<>(Arrays.asList("Euklidesa","Czebyszewa","Manhattana"));
        ArrayList<ArrayList<Integer>> experimentalFeatureSets = new ArrayList<>();
        ArrayList<ArrayList<Double>> weightsSets = new ArrayList<>();
        ArrayList<Integer> fset1 = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
        experimentalFeatureSets.add(fset1);
        weightSum=0.0;
        for(Integer feature : fset1) {
            weightSum+=feature;
        }
        HashMap<Integer, Double> weightSet1 = new HashMap<>();
        for(Integer feature : fset1) {
            weightSet1.put(feature,baseWeightsList.get(feature-1)/weightSum);
        }
        ArrayList<Integer> fset2 = new ArrayList<>(Arrays.asList(2,3,5,6,7,8,9,10,11,12));
        experimentalFeatureSets.add(fset2);
        weightSum=0.0;
        for(Integer feature : fset2) {
            weightSum+=feature;
        }
        HashMap<Integer, Double> weightSet2 = new HashMap<>();
        for(Integer feature : fset2) {
            weightSet2.put(feature,baseWeightsList.get(feature-1)/weightSum);
        }
        ArrayList<Integer> fset3 = new ArrayList<>(Arrays.asList(1,2,3,4,5,7,8,9,10,11,12));
        experimentalFeatureSets.add(fset3);
        weightSum=0.0;
        for(Integer feature : fset3) {
            weightSum+=feature;
        }
        HashMap<Integer, Double> weightSet3 = new HashMap<>();
        for(Integer feature : fset3) {
            weightSet3.put(feature,baseWeightsList.get(feature-1)/weightSum);
        }
        ArrayList<Integer> fset4 = new ArrayList<>(Arrays.asList(1,4,6,7,8,9,10,11,12));
        experimentalFeatureSets.add(fset4);
        weightSum=0.0;
        for(Integer feature : fset4) {
            weightSum+=feature;
        }
        HashMap<Integer, Double> weightSet4 = new HashMap<>();
        for(Integer feature : fset4) {
            weightSet4.put(feature,baseWeightsList.get(feature-1)/weightSum);
        }

        StringBuilder latex = new StringBuilder();

        kNN algorithm = new kNN(baseKValue,baseTrainingRatio,baseIncludedFeatures,baseMeasure,baseMetric,extractions,baseWeights);
        QualityMeasure QM = new QualityMeasure(algorithm.runAlgorithm());
        String title = "Wyniki klasyfikacji dla parametrów domyślnych";
        latex.append(QM.generateLatex(title));
        latex.append(QM.generateBarChart("template",title));


        System.out.println("Eksperyment 1: porównanie wartości miar jakości klasyfikacji dla różnych wartości parametru k");
        latex.append("\\subsection{Eksperyment 1}\n");
        latex.append("\\subsubsection{Wyniki}\n");
        DefaultCategoryDataset e1_dataset = new DefaultCategoryDataset();
        for(Integer kvalue : expertimentalKValues) {
            System.out.println("Obecna wartość k: "+kvalue);
            algorithm = new kNN(kvalue,baseTrainingRatio,baseIncludedFeatures,baseMeasure,baseMetric,extractions,baseWeights);
            QM = new QualityMeasure(algorithm.runAlgorithm());
            e1_dataset.addValue(QM.calcAccuracy(), "Accuracy",kvalue);
            e1_dataset.addValue(QM.calcGlobalPrecision(),"Precision",kvalue);
            e1_dataset.addValue(QM.calcGlobalRecall(),"Recall",kvalue);
            title = "Wyniki klasyfikacji dla parametrów domyślnych oraz k="+kvalue;
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("k_"+kvalue,title));
        }
        JFreeChart e1_chart = ChartFactory.createBarChart(
                "Wartość miar jakości klasyfikacji dla różnych wartości parametru k","Wartość parametru k",
                "Wartość miary jakości klasyfikacji",e1_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e1_file = new File("k_summary.png");
        ChartUtils.saveChartAsPNG(e1_file,e1_chart,chartWidth,chartHeight);
        latex.append("\\subsubsection{Wykres z rezultatami końcowymi eksperymentu}\n");
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/k_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Zmiana wartości miar jakości klasyfikacji dla zmiany wartości parametru k}
                \\end{figure}
                """);

        System.out.println("Eksperyment 2: porównanie wartości miar jakości klasyfikacji dla różnych proporcji podziału zbioru");
        latex.append("\\subsection{Eksperyment 2}\n");
        latex.append("\\subsubsection{Wyniki}\n");
        DefaultCategoryDataset e2_dataset = new DefaultCategoryDataset();
        for(Double ratio : experimentalTrainingRatio) {
            int ratioInInteger = (int) (ratio*100);
            System.out.println("Obecny podział: zbiór uczący "+ratioInInteger+"%, zbiór testowy "+(100-ratioInInteger)+"%");
            algorithm = new kNN(baseKValue,ratio,baseIncludedFeatures,baseMeasure,baseMetric,extractions,baseWeights);
            QM = new QualityMeasure(algorithm.runAlgorithm());
            e2_dataset.addValue(QM.calcAccuracy(), "Accuracy",ratioInInteger+"/"+(100-ratioInInteger));
            e2_dataset.addValue(QM.calcGlobalPrecision(), "Precision",ratioInInteger+"/"+(100-ratioInInteger));
            e2_dataset.addValue(QM.calcGlobalRecall(), "Recall",ratioInInteger+"/"+(100-ratioInInteger));

            title = "Wyniki klasyfikacji dla parametrów domyślnych oraz podziału na zbiór uczący "+ratioInInteger+"\\%, zbiór testowy "+(100-ratioInInteger)+"\\%";
            String chartTitle = "Wyniki klasyfikacji dla parametrów domyślnych oraz podziału na zbiór uczący "+ratioInInteger+"%, zbiór testowy "+(100-ratioInInteger)+"%";
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("ratio_"+ratio,chartTitle));
        }
        JFreeChart e2_chart = ChartFactory.createBarChart(
                "Wartość miar jakości klasyfikacji dla różnej proporcji podziału na zbiór uczący i zbiór testowy","" +
                        "Podział na zbiór uczący/zbiór testowy (w procentach)",
                "Wartość miary jakości klasyfikacji",e2_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e2_file = new File("ratio_summary.png");
        ChartUtils.saveChartAsPNG(e2_file,e2_chart,chartWidth,chartHeight);
        latex.append("\\subsubsection{Wykres z rezultatami końcowymi eksperymentu}\n");
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/ratio_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Wartość miar jakości klasyfikacji dla różnej proporcji podziału na zbiór uczący i zbiór testowy}
                \\end{figure}
                """);

        System.out.println("Eksperyment 3: porównanie wartości miar jakości klasyfikacji dla wszystkich badanych metryk");
        latex.append("\\subsection{Eksperyment 3}\n");
        latex.append("\\subsubsection{Wyniki}\n");
        DefaultCategoryDataset e3_dataset = new DefaultCategoryDataset();
        for(int i=0;i<=2;i++) {
            System.out.println("Obecna metryka: "+metricNames.get(i));
            algorithm = new kNN(baseKValue,baseTrainingRatio,baseIncludedFeatures,baseMeasure,experimentalMetric.get(i),extractions,baseWeights);
            QM = new QualityMeasure(algorithm.runAlgorithm());
            e3_dataset.addValue(QM.calcAccuracy(), "Accuracy",metricNames.get(i));
            e3_dataset.addValue(QM.calcGlobalPrecision(), "Precision",metricNames.get(i));
            e3_dataset.addValue(QM.calcGlobalRecall(), "Recall",metricNames.get(i));

            title = "Wyniki klasyfikacji dla parametrów domyślnych oraz metryki "+metricNames.get(i);
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("metric_"+metricNames.get(i),title));
        }
        JFreeChart e3_chart = ChartFactory.createBarChart(
                "Wartość miar jakości klasyfikacji dla każdej z badanych metryk","Wybrana metryka",
                "Wartość miary jakości klasyfikacji",e3_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e3_file = new File("metric_summary.png");
        ChartUtils.saveChartAsPNG(e3_file,e3_chart,chartWidth,chartHeight);
        latex.append("\\subsubsection{Wykres z rezultatami końcowymi eksperymentu}\n");
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/metric_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Zmiana wartości miar jakości klasyfikacji ze względu na metrykę}
                \\end{figure}
                """);

        System.out.println("Eksperyment 4: porównanie wartości miar jakości klasyfikacji dla różnych podzbiorów cech");
        latex.append("\\subsection{Eksperyment 4}\n");
        latex.append("\\subsubsection{Wyniki}\n");

        DefaultCategoryDataset e4_dataset = new DefaultCategoryDataset();
        for(int i=0;i<4;i++) {
            System.out.println("Obecny podzbiór cech: "+(i+1));
            algorithm = new kNN(baseKValue,baseTrainingRatio,experimentalFeatureSets.get(i),baseMeasure,baseMetric,extractions,baseWeights);
            QM = new QualityMeasure(algorithm.runAlgorithm());
            e4_dataset.addValue(QM.calcAccuracy(), "Accuracy","Zbiór "+(i+1));
            e4_dataset.addValue(QM.calcGlobalPrecision(), "Precision","Zbiór "+(i+1));
            e4_dataset.addValue(QM.calcGlobalRecall(), "Recall","Zbiór "+(i+1));
            title = "Wyniki klasyfikacji dla parametrów domyślnych oraz podzbioru cech nr "+(i+1);
            latex.append(QM.generateLatex(title));
            latex.append(QM.generateBarChart("feature_"+(i+1),title));
        }
        JFreeChart e4_chart = ChartFactory.createBarChart(
                "Wartość miar jakości klasyfikacji dla każdego z 4 podzbiorów cech","Podzbiór cech",
                "Wartość miary jakości klasyfikacji",e4_dataset, PlotOrientation.VERTICAL,true,true,false);
        File e4_file = new File("feature_summary.png");
        ChartUtils.saveChartAsPNG(e4_file,e4_chart,chartWidth,chartHeight);
        latex.append("\\subsubsection{Wykres z rezultatami końcowymi eksperymentu}\n");
        latex.append("""
                \\begin{figure}[H]
                \\includegraphics[width=1\\textwidth]{wykresy/feature_summary.png}
                \\centering
                \\vspace{-0.3cm}
                \\caption{Zmiana wartości miar jakości klasyfikacji ze względu na wybór podzbioru cech}
                \\end{figure}
                """);

        System.out.println(latex);
    }

    public static void main(String[] args) throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        String choice;
        System.out.println("Wybierz wariant programu:");
        System.out.println("1 - ręczne wprowadzanie parametrów");
        System.out.println("2 - skonfigurowany zestaw eksperymentów");
        System.out.print("Twój wybór:");
        choice = scanner.nextLine();
        while (!choice.equals("1")&&!choice.equals("2")) {
            System.out.print("Zła opcja, wybierz jeszcze raz:");
            choice = scanner.nextLine();
        }
        if(choice.equals("2")) {
            runSimulations();
        } else {
            System.out.println("Pomyłka w trakcie wprowadzania parametrów? wpisz Z by cofnąć.");
            int step = 0;
            int feature;
            boolean errorFlag = false;
            boolean allFeatureFlag = false;
            File file;
            Parser parser = new Parser();

            int kValue = 1;
            double trainingRatio = 0.5;
            ArrayList<Integer> includedFeatures = new ArrayList<>();
            NGramMeasure measure = new NGramMeasure();
            Metric metric = new Euclidean();
            HashMap<Integer, Double> weights = new HashMap<>();


            while(step<=6) {
                if(!choice.equals("Z")) {
                    if(errorFlag) {
                        System.out.println("Błędnie podany parametr");
                        errorFlag=false;
                    } else {
                        step++;
                    }
                    switch (step) {
                        case 1 -> {
                            System.out.print("Podaj wartość k dla algorytmu k-NN (liczbę sąsiadów):");
                            choice = scanner.nextLine();
                            if(!choice.equals("Z")) {
                                kValue = Integer.parseInt(choice);
                                if (kValue < 1) {
                                    errorFlag = true;
                                }
                            }
                        }
                        case 2 -> {
                            System.out.print("Podaj udział procentowy zbioru uczącego w procesie klasyfikacji (wartość całkowita z przedziału 1-99):");
                            choice = scanner.nextLine();
                            if(!choice.equals("Z")) {
                                trainingRatio = Integer.parseInt(choice) / 100.0;
                                if (trainingRatio < 0.01 || trainingRatio > 0.99) {
                                    errorFlag = true;
                                }
                            }
                        }
                        case 3 -> {
                            System.out.println("Wybierz metrykę według której będzie liczona odległość w metodzie k-NN.");
                            System.out.println("1 - euklidesowa");
                            System.out.println("2 - uliczna");
                            System.out.println("3 - Czebyszewa");
                            System.out.print("Twój wybór:");
                            choice = scanner.nextLine();
                            if(!choice.equals("Z")) {
                                switch (choice) {
                                    case "1" -> metric = new Euclidean();
                                    case "2" -> metric = new Manhattan();
                                    case "3" -> metric = new Chebyshev();
                                    default -> errorFlag = true;
                                }
                            }
                        }
                        case 4 -> {
                            System.out.println("Wpisz po kolei cechy które chcesz zastosować:");
                            System.out.println("A - zastosuj wszystkie cechy");
                            System.out.println("L - zobacz listę pozostałych cech");
                            System.out.print("Twój wybór:");
                            choice = scanner.nextLine();
                            if (!choice.equals("Z")) {
                                if (choice.equals("A")) {
                                    includedFeatures = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
                                    for(int i=1;i<=12;i++) {
                                        weights.put(i,1.0);
                                    }
                                    allFeatureFlag = true;
                                    System.out.println("Jeżeli chcesz ustalić wagę dla którejś z cech wpisz jej numer (domyślna waga - 1), jeżeli chcesz przejść dalej wpisz X");

                                } else {
                                    step--;
                                    if (choice.equals("L")) {
                                        for (int i = 0; i <= 12; i++) {
                                            if (!includedFeatures.contains(i)) {
                                                switch (i) {
                                                    case 1 -> System.out.println("1 - Czas publikacji");
                                                    case 2 -> System.out.println("2 - Pierwsze słowo w tekście, które znajduje się w słowniku topics");
                                                    case 3 -> System.out.println("3 - Liczba wystąpień organizacji z słownika orgs");
                                                    case 4 -> System.out.println("4 - Średnia długość słowa zaczynającego się z wielkiej litery (po odrzuceniu pierwszych słów w zdaniu i nazw miesięcy)");
                                                    case 5 -> System.out.println("5 - Wartość binarna, określająca, czy w tekście występuje słowo ”nuclear”");
                                                    case 6 -> System.out.println("6 - Nazwa kraju, którego liczba wystąpień skrótu, nazwy i/lub przymiotnika jest największa (na bazie słownika countries)");
                                                    case 7 -> System.out.println("7 - Liczba wystąpień nazwisk dla słownika people_west-germany");
                                                    case 8 -> System.out.println("8 - Liczba wystąpień nazwisk dla słownika people_usa");
                                                    case 9 -> System.out.println("9 - Liczba wystąpień nazwisk dla słownika people_france");
                                                    case 10 -> System.out.println("10 - Liczba wystąpień nazwisk dla słownika people_uk");
                                                    case 11 -> System.out.println("11 - Liczba wystąpień nazwisk dla słownika people_canada");
                                                    case 12 -> System.out.println("12 - Liczba wystąpień nazwisk dla słownika people_japan");
                                                }
                                            }
                                        }
                                    } else if (choice.equals("X")) {
                                        step++;
                                        System.out.println("Wybrane cechy: " + includedFeatures);
                                        System.out.println("Jeżeli chcesz ustalić wagę dla którejś z cech wpisz jej numer (domyślna waga - 1), jeżeli chcesz przejść dalej wpisz X");

                                    } else {
                                        feature = Integer.parseInt(choice);
                                        if (feature > 0 && feature < 13) {
                                            if (includedFeatures.contains(feature)) {
                                                System.out.println("Cecha już została wcześniej dodana");
                                            } else {
                                                includedFeatures.add(feature);
                                                weights.put(feature,1.0);
                                                System.out.println("Poprawnie dodano cechę numer " + feature);
                                            }
                                        } else {
                                            step++;
                                            errorFlag = true;
                                        }
                                    }
                                }
                            }
                        }
                        case 5 -> {
                            System.out.print("Podaj numer cechy by zmienić jej wagę, lub zakończ wpisując X:");
                            choice = scanner.nextLine();
                            if (!choice.equals("Z")) {
                                if(!choice.equals("X")) {
                                    feature = Integer.parseInt(choice);
                                    if (includedFeatures.contains(feature)) {
                                        System.out.print("Podaj wagę:");
                                        choice = scanner.nextLine();
                                        double weight = Double.parseDouble(choice);
                                        if (weight>0) {
                                            step--;
                                            weights.put(feature,weight);
                                            System.out.println("Poprawnie ustawiono wagę cechy "+feature+" na "+String.format(Locale.FRANCE,"%,.3f",weight));
                                        } else {
                                            errorFlag=true;
                                        }
                                    } else {
                                        errorFlag = true;
                                    }
                                } else {
                                    System.out.println("Wybierz pliki użyte w klasyfikacji (numer pliku z zakresu od 0-21), lub A - wszystkie artykuły.");
                                }
                            }
                        }
                        case 6 -> {
                            System.out.print("Twój wybór (X by zakończyć wczytywanie):");
                            choice = scanner.nextLine();
                            if(!choice.equals("Z")) {
                                if(choice.equals("X")) {
                                    if(parser.getLoadedArticles().size()==0) {
                                        errorFlag=true;
                                    } else {
                                        System.out.println("Liczba wczytanych artykułów: "+parser.getLoadedArticles().size());
                                    }
                                } else if (choice.equals("A")) {
                                    parser = new Parser();
                                    for(int i=0;i<=21;i++) {
                                        parser.loadArticles(new File("reuters/reut2-0"+(i<10?"0":"")+i+".sgm"));
                                    }
                                    System.out.println("Liczba wczytanych artykułów: "+parser.getLoadedArticles().size());
                                } else {
                                    feature = Integer.parseInt(choice);
                                    if(feature<0||feature>21) {
                                        errorFlag=true;
                                    } else {
                                        int beginArticle;
                                        int noOfArticles;
                                        file = new File("reuters/reut2-0"+(feature<10?"0":"")+feature+".sgm");
                                        System.out.print("0 - użycie całego pliku w procesie klasyfikacji, 1 - wybranie przedziału artykułów:");
                                        choice = scanner.nextLine();
                                        if(Integer.parseInt(choice)==0) {
                                            step--;
                                            System.out.println("Wczytano "+parser.loadArticles(file)+" artykułów");
                                        } else if (Integer.parseInt(choice)==1) {
                                            System.out.print("Podaj od którego (prawidłowego) artykułu zacząć wczytywanie, 0 oznacza od początku:");
                                            choice = scanner.nextLine();
                                            beginArticle = Integer.parseInt(choice);

                                            System.out.print("Podaj limit wczytanych artykułów (artykułów zostanie wczytanych mniej, jeżeli plik się skończy):");
                                            choice = scanner.nextLine();
                                            noOfArticles = Integer.parseInt(choice);
                                            if(beginArticle<0||noOfArticles<=0) {
                                                errorFlag = true;
                                            } else {
                                                step--;
                                                System.out.println("Wczytano "+parser.loadArticles(file,beginArticle,noOfArticles)+" artykułów");
                                            }

                                        } else {
                                            errorFlag=true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    errorFlag=false;
                    choice = "";
                    if(step==4) {
                        if(allFeatureFlag) {
                            includedFeatures=new ArrayList<>();
                            allFeatureFlag=false;
                            step-=2;
                        } else if(!includedFeatures.isEmpty()) {
                            includedFeatures.remove(includedFeatures.size()-1);
                        } else {
                            step-=2;
                        }
                    } else {
                        step-=2;

                    }
                }
            }
            System.out.println("Proces wyboru parametrów zakończony");
            System.out.println("Podaj nazwę pliku pod którą ma się zapisać wykres: ");
            String filename = scanner.nextLine();
            System.out.println("Podaj tytuł eksperymentu:");
            String title = scanner.nextLine();
            System.out.println("Klasyfikacja trwa...");
            Double weightSum = weights.values().stream().reduce(0.0,Double::sum);
            for(int i=1;i<=12;i++) {
                if(weights.containsKey(i)) {
                    weights.put(i,weights.get(i)/weightSum);
                }
            }
            ArrayList<Extraction> extractions = new ArrayList<>();
            for(Article article : parser.getLoadedArticles()) {
                extractions.add(new Extraction(article));
            }
            Collections.shuffle(extractions, new Random(1000));
            kNN algorithm = new kNN(kValue,trainingRatio,includedFeatures,measure,metric,extractions,weights);
            QualityMeasure QM = new QualityMeasure(algorithm.runAlgorithm());
            QM.generateBarChart(filename,title);
            System.out.println("Klasyfikacja zakończona, utworzony został wykres w pliku "+filename+".jpg, uzyskane wyniki:");
            System.out.println("Liczba zbadanych artykułów: "+QM.getActualAndPredicted().size());
            System.out.println("Accuracy: "+String.format(Locale.FRANCE,"%,.3f",QM.calcAccuracy()*100)+"%");
            System.out.println("Precision dla west-germany: "+String.format(Locale.FRANCE,"%,.3f",QM.calcPrecision(Country.westgermany)*100)+"%");
            System.out.println("Precision dla usa: "+String.format(Locale.FRANCE,"%,.3f",QM.calcPrecision(Country.usa)*100)+"%");
            System.out.println("Precision dla france: "+String.format(Locale.FRANCE,"%,.3f",QM.calcPrecision(Country.france)*100)+"%");
            System.out.println("Precision dla uk: "+String.format(Locale.FRANCE,"%,.3f",QM.calcPrecision(Country.uk)*100)+"%");
            System.out.println("Precision dla canada: "+String.format(Locale.FRANCE,"%,.3f",QM.calcPrecision(Country.canada)*100)+"%");
            System.out.println("Precision dla japan: "+String.format(Locale.FRANCE,"%,.3f",QM.calcPrecision(Country.japan)*100)+"%");
            System.out.println("Precision ogólne: "+String.format(Locale.FRANCE,"%,.3f",QM.calcGlobalPrecision()*100)+"%");
            System.out.println("Recall dla west-germany: "+String.format(Locale.FRANCE,"%,.3f",QM.calcRecall(Country.westgermany)*100)+"%");
            System.out.println("Recall dla usa: "+String.format(Locale.FRANCE,"%,.3f",QM.calcRecall(Country.usa)*100)+"%");
            System.out.println("Recall dla france: "+String.format(Locale.FRANCE,"%,.3f",QM.calcRecall(Country.france)*100)+"%");
            System.out.println("Recall dla uk: "+String.format(Locale.FRANCE,"%,.3f",QM.calcRecall(Country.uk)*100)+"%");
            System.out.println("Recall dla canada: "+String.format(Locale.FRANCE,"%,.3f",QM.calcRecall(Country.canada)*100)+"%");
            System.out.println("Recall dla japan: "+String.format(Locale.FRANCE,"%,.3f",QM.calcRecall(Country.japan)*100)+"%");
            System.out.println("Recall ogólne: "+String.format(Locale.FRANCE,"%,.3f",QM.calcGlobalRecall()*100)+"%");
            System.out.println("Miara F1 dla west-germany: "+String.format(Locale.FRANCE,"%,.3f",QM.calcF1Score(Country.westgermany)*100)+"%");
            System.out.println("Miara F1 dla usa: "+String.format(Locale.FRANCE,"%,.3f",QM.calcF1Score(Country.usa)*100)+"%");
            System.out.println("Miara F1 dla france: "+String.format(Locale.FRANCE,"%,.3f",QM.calcF1Score(Country.france)*100)+"%");
            System.out.println("Miara F1 dla uk: "+String.format(Locale.FRANCE,"%,.3f",QM.calcF1Score(Country.uk)*100)+"%");
            System.out.println("Miara F1 dla canada: "+String.format(Locale.FRANCE,"%,.3f",QM.calcF1Score(Country.canada)*100)+"%");
            System.out.println("Miara F1 dla japan: "+String.format(Locale.FRANCE,"%,.3f",QM.calcF1Score(Country.japan)*100)+"%");
        }
    }
}
