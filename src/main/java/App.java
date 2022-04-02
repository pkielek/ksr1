import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        parser.loadArticles(new File("reut2-000.sgm"),0,1000);

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
