package knn;

public class NGramMeasure implements Measure {
    @Override
    public double compare(String word1, String word2) {
        int trigramCounter = 0;
        int N = Math.min(word1.length(), word2.length());
        for (int i = 0; i < word1.length() - 2; i++) {
            String trigram = word1.substring(i, i + 3);
            if (word2.contains(trigram)) {
                trigramCounter++;
            }
        }
        return (double) trigramCounter / (N - 2);
    }

}
