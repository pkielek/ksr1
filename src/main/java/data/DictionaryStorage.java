package data;

import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public final class DictionaryStorage {
    @Getter
    private final static HashMap<String, Dictionary> dictionaryMap = new HashMap<>();

    public static void loadDictionary(String name, File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        ArrayList<String> words = new ArrayList<>();
        while (line != null) {
            words.add(line.trim());
            line = reader.readLine();
        }
        reader.close();
        dictionaryMap.put(name, new Dictionary(words));
    }

    public static Dictionary getDictionary(String name) {
        if (!dictionaryMap.containsKey(name)) {
            try {
                File file = new File(name + ".txt");
                loadDictionary(name, file);
            }
            catch(IOException exception) {
                System.out.println("Fatal Error: Dictionary file "+name+" not found!");
                System.exit(404);
            }
        }
        return dictionaryMap.get(name);
    }
}