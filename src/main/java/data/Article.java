package data;

import lombok.Data;
import java.util.ArrayList;
import java.util.Date;

@Data
public class Article {
    private final Country place;
    private final ArrayList<String> content;
    private final Integer publishTime;
}
