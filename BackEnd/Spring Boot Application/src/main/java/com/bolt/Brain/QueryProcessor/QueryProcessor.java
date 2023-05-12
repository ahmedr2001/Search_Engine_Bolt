package com.bolt.Brain.QueryProcessor;

import com.bolt.Brain.Utils.Stemmer;
import com.bolt.Brain.Utils.StopWordsRemover;
//import com.bolt.Brain.Utils.Synonymization;
import com.bolt.Brain.Utils.Tokenizer;
import com.bolt.SpringBoot.CrawlerService;
import com.bolt.SpringBoot.Page;
import com.bolt.SpringBoot.WordsDocument;
import com.bolt.SpringBoot.WordsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryProcessor {
    private final Tokenizer tokenizer;
    private final Stemmer stemmer;
    private final StopWordsRemover stopWordsRemover;
//    private final Synonymization synonymization;
    private CrawlerService crawlerService;
    private WordsService wordsService;




    public QueryProcessor(CrawlerService crawlerService,WordsService wordsService) throws IOException {
        tokenizer = new Tokenizer();
        stopWordsRemover = new StopWordsRemover();
//        synonymization = new Synonymization();
        stemmer = new Stemmer();
        this.crawlerService=crawlerService;
        this.wordsService=wordsService;
    }

    public List<WordsDocument> run(String query) throws IOException {
        //======= Variables Section ========//
        List<String> phrases = extractPhrases(query);        //0. get Phrases

        List<String> words = process(query);                //1. process query and return all words after processing
        List<WordsDocument> results = getWordResult(words);
        System.out.println(words);

        //===== Get Documents into results ===== //
        for (String word : words) {
            results.addAll(wordsService.findWords(word));
        }
        // ==== Handel phrases ==== //


        return results;
    }

    public List<String> extractPhrases(String query) {
        List<String> phrases = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            phrases.add(matcher.group(1));
        }

        // This Code to remove phrase from query But I don't need it now
        for (String phrase : phrases) {
            query = query.replaceAll("\"" + phrase + "\"", "").trim();
        }
        return phrases;
    }

    public List<String> process(String query) throws IOException {
        List<String> words;
        query = query.replaceAll("[^a-zA-Z1-9]", " "); //1.remove single characters and numbers
        words = tokenizer.runTokenizer(query);                          //2.Convert words to list + toLowerCase
//        words = synonymization.runSynonymization(words//3.Replace words with its steam synonyms
        words = tokenizer.runTokenizer(query);                          //3.Convert words to (list + toLowerCase)
        words = stemmer.runStemmer(words);                              //4.return to it's base
        words = stopWordsRemover.runStopWordsRemover(words);            //4.Remove Stop Words
        words = words.stream().distinct()                               //5.Remove Duplicates
                .collect(Collectors.toList());
        return words;
    }

    private List<WordsDocument> getWordResult(List<String> words) {
        List<WordsDocument> results = new ArrayList<>();

        //===== Get Documents into results ===== //
        for (String word : words) {
            results.addAll(wordsService.findWords(word));
        }
        return results;
    }

}
