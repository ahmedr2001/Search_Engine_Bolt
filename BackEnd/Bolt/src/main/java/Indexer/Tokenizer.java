package Indexer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public  List<String> runTokenizer(String sentence){
        sentence = sentence.toLowerCase();
        List<String> words = List.of(sentence.split(" "));
        words = new ArrayList<>(words);
        return words;
    }
}
