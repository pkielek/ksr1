package data;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Dictionary {
    private final ArrayList<String> listOfWords;

    public boolean inDictionary(String word) {
        return listOfWords.contains(word.toLowerCase());
    }
}
