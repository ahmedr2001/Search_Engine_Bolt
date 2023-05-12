package com.bolt.Brain.Utils;
import ca.rmen.porterstemmer.PorterStemmer;

import java.util.ArrayList;
import java.util.List;

public class Stemmer {
    public PorterStemmer porterStemmer;
    List<String> stemWords;

    public Stemmer() {
        porterStemmer = new PorterStemmer();
        stemWords = new ArrayList<>();
    }

    public List<String> runStemmer(List<String> words) {
        for (String word : words) {
            String stem = porterStemmer.stemWord(word);
            stemWords.add(stem);
        }
        return stemWords;
    }
}
