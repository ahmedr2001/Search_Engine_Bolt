package com.bolt.Brain.QueryProcessor;

import com.bolt.Brain.Utils.Stemmer;
import com.bolt.Brain.Utils.StopWordsRemover;
import com.bolt.Brain.Utils.Tokenizer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessQueryUnit {
    // this class provide all utilits to process query
    private final Tokenizer tokenizer ;
    private final Stemmer stemmer ;
    private final StopWordsRemover stopWordsRemover ;


    public ProcessQueryUnit(Tokenizer t, Stemmer st, StopWordsRemover stp) {
        tokenizer = t;
        stemmer = st;
        stopWordsRemover = stp;
    }

    public  List<String> basicProcess(String query) {
        List<String> words;
        words = tokenizer.runTokenizer(query);                          //1.Convert words to (list + toLowerCase)
        words = stopWordsRemover.runStopWordsRemover(words);            //2.Remove Stop Words
        return words;
    }

    public  List<String> process(String query) throws IOException {
        List<String> words;
        words = basicProcess(query);                                    // [ convert it to words, remove stop words]
        words = stemmer.runStemmer(words);                              //3.return to it's base
        words = words.stream().distinct()                               //4.Remove Duplicates
                .collect(Collectors.toList());
        return words;
    }


}
