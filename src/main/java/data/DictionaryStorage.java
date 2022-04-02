package data;

import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DictionaryStorage {
    @Getter
    private final HashMap<String,Dictionary> dictionaryMap;

    public DictionaryStorage() {
        dictionaryMap = new HashMap<>();
    }

    public void loadDictionary(String name, File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        ArrayList<String> words = new ArrayList<>();
        while(line != null) {
            words.add(line.trim());
            line = reader.readLine();
        }
        reader.close();
        dictionaryMap.put(name,new Dictionary(words));
    }
}