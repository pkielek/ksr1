package data;

import lombok.Data;
import java.util.ArrayList;
import java.util.Date;

@Data
public class Article {
    private final Country place;
    private final ArrayList<String> content;
    private ArrayList<String> contentWithoutDots;
    private final Integer publishTime;

    public ArrayList<String> getContentWithoutDots() {
        if(contentWithoutDots==null) {
            ArrayList<String> newList = new ArrayList<>(content);
            newList.forEach((word)-> word.toLowerCase().replace(".",""));

            contentWithoutDots = newList;
        }
        return contentWithoutDots;
    }

}


