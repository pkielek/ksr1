package data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Dictionary {
    @Getter
    private final ArrayList<String> listOfWords;

    public boolean inDictionary(String word) {
        return listOfWords.contains(word.toLowerCase());
    }
}
