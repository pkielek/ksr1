import data.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

public class Extraction implements Serializable {
    @Getter
    private Country country;
    @Getter
    private final HashMap<Integer, Feature> features;
    private static final HashSet<String> monthNames = new HashSet<>(Arrays.asList("January","February",
            "March","April","May","June","July","August","September","October","December"));

    private Extraction() {
        this.features= new HashMap<>();
    }

    public Extraction(Country country) {
        this();
        this.country = country;
    }

    public Extraction(Article article) {
        this(article.getPlace());
        publicationTime(article);
        firstArticleWordInTopicDict(article);
        organizationCounter(article);
        avgWordLengthWithCapitalLetter(article);
        isNuclearInArticle(article);
        countryMostCommonSynonyms(article);
        for(Country country : Country.values()) {
            lastnameCounterForCountry(article,country);
        }
    }
    public Extraction(Article article,HashSet<Integer> featureKeys) {
        this(article.getPlace());
        for(Integer key : featureKeys) {
            switch (key) {
                case 1 -> publicationTime(article);
                case 2 -> firstArticleWordInTopicDict(article);
                case 3 -> organizationCounter(article);
                case 4 -> avgWordLengthWithCapitalLetter(article);
                case 5 -> isNuclearInArticle(article);
                case 6 -> countryMostCommonSynonyms(article);
                case 7 -> lastnameCounterForCountry(article,Country.westgermany);
                case 8 -> lastnameCounterForCountry(article,Country.usa);
                case 9 -> lastnameCounterForCountry(article,Country.france);
                case 10 -> lastnameCounterForCountry(article,Country.uk);
                case 11 -> lastnameCounterForCountry(article,Country.canada);
                case 12 -> lastnameCounterForCountry(article,Country.japan);
                }
            }
    }

    public void publicationTime(Article article) {
        features.put(1,new Feature(article.getPublishTime(),false));
    }

    public void firstArticleWordInTopicDict(Article article) {
        Dictionary dict = DictionaryStorage.getDictionary("topics");
        features.put(2,new Feature
                (article.getContentWithoutDots().stream().filter(
                        dict::inDictionary).findFirst().orElse(""),true));
    }

    public void organizationCounter(Article article) {
        Dictionary dict = DictionaryStorage.getDictionary("orgs");
        features.put(3,new Feature
                (article.getContentWithoutDots().stream().filter(dict::inDictionary).count(),false));
    }

    public void avgWordLengthWithCapitalLetter(Article article) {
        ArrayList<String> content = article.getContent();
        int count=0;
        int sum=0;
        for(int i=1;i<content.size();i++) {

            String prevWord=content.get(i-1);
            String word=content.get(i);
            if(String.valueOf(prevWord.charAt(prevWord.length()-1)).equals(".") ||
                    monthNames.contains(word) ||
                    (word.charAt(0)<65 || word.charAt(0)>90)) {
                continue;
            }
            count++;
            sum+=word.length();
        }
        features.put(4,new Feature((double) sum/(double) count,false));
    }

    public void isNuclearInArticle(Article article) {
        features.put(5,new Feature(article.getContentWithoutDots().stream().anyMatch(c->c.equals("nuclear"))?1:0,false));
    }

    public void countryMostCommonSynonyms(Article article) {
        Country currentMax = null;
        long currentMaxCount=0;
        int currentMaxFirstIndex=-1;
        for(Country country : Country.values()) {
            Dictionary dict = DictionaryStorage.getDictionary("countries_"+country.name());
            Stream<String> stream = article.getContentWithoutDots().stream().filter(dict::inDictionary);
            long currentCount = stream.count();
            stream = article.getContentWithoutDots().stream().filter(dict::inDictionary);
            int firstIndex = article.getContentWithoutDots().indexOf(stream.findFirst().orElse(""));
            if(currentCount>currentMaxCount || (currentCount==currentMaxCount && currentMaxFirstIndex>firstIndex)) {
                currentMax=country;
                currentMaxCount=currentCount;
                currentMaxFirstIndex=firstIndex;
            }
        }
        features.put(6,new Feature(currentMax==null?"":currentMax.name(),true));
    }

    public void lastnameCounterForCountry(Article article,Country country) {
        Dictionary dict = DictionaryStorage.getDictionary("people_"+country.name());
        features.put(7+country.ordinal(),new Feature
                (article.getContentWithoutDots().stream().filter(dict::inDictionary).count(),false));
    }

}
