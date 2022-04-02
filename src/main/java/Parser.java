import data.Article;
import data.Country;
import lombok.Getter;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    @Getter
    private final ArrayList<Article> loadedArticles;

    public Parser() {
        loadedArticles = new ArrayList<>();
    }

    public int loadArticles(File file, int startArticle, int noOfArticles) throws IOException, ParseException {
        Node mainNode = Jsoup.parse(file,null).childNode(2).childNode(1);
        int loadedArticlesCurrent=0;
        for(int i=startArticle*2;i<mainNode.childNodeSize();i+=2) {
            // if noOfArticles reached finish loading articles
            if(loadedArticlesCurrent>=noOfArticles) {
                break;
            }
            // load node at index
            Node articleNode = mainNode.childNode(i);
            // read places tag, if size not appropriate or it is not one of six select countries then continue to next article
            List<Node> placesNodes = articleNode.childNode(5).childNodes();
            if(placesNodes.size()!=1) {
                continue;
            }
            String countryName = placesNodes.get(0).childNode(0).toString().trim().replace("-","");
            Country country;
            try {
                country = Country.valueOf(countryName);
                loadedArticlesCurrent++;
            }
            catch(IllegalArgumentException e) {
                continue;
            }
            // load content of article, with unescaping html entities, and removing Reuters end tag and commas
            ArrayList<String> content = new ArrayList<>(List.of(StringEscapeUtils.
                    unescapeHtml4(
                            articleNode.
                                    childNode(17).
                                    childNode(4).
                                    toString())
                    .replace(" Reuter \u0003", "")
                    .replace(",", "")));
            // calculate publish time (number of minutes from 0 to 1339)
            String articleDatetimeString = articleNode.childNode(1).childNode(0).toString().trim();
            int articleDateTimeStringLength = articleDatetimeString.length();
            int offset = articleDateTimeStringLength-22;
            int articlePublishTime = Integer.parseInt(articleDatetimeString.substring(11+offset,13+offset))*60+
                    Integer.parseInt(articleDatetimeString.substring(14+offset,16+offset));
            loadedArticles.add(new Article(country,content,articlePublishTime));
            break;
        }
        return loadedArticlesCurrent;
    }

    public int loadArticles(File file) throws IOException, ParseException {
        return loadArticles(file,0,Integer.MAX_VALUE);
    }
}
