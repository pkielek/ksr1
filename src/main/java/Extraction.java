import data.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

public class Extraction {
    @Getter
    private Country country;
    @Getter
    private final HashMap<Integer, Feature> features;

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
            lastnameCounterForCountry(article,DictionaryStorage.getDictionaryMap().get("people_"+country.name()));
        }
    }

    public void publicationTime(Article article) {

    }

    public void firstArticleWordInTopicDict(Article article) {

    }

    public void organizationCounter(Article article) {

    }

    public void avgWordLengthWithCapitalLetter(Article article) {

    }

    public void isNuclearInArticle(Article article) {

    }

    public void countryMostCommonSynonyms(Article article) {

    }

    public void lastnameCounterForCountry(Article article,Dictionary dictionary) {

    }

}
