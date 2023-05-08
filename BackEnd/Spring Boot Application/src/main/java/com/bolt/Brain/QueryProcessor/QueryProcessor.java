package com.bolt.Brain.QueryProcessor;

import com.bolt.Brain.Utils.Stemmer;
import com.bolt.Brain.Utils.StopWordsRemover;
import com.bolt.Brain.Utils.Synonymization;
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
    private final Synonymization synonymization;
    private CrawlerService crawlerService;
    private WordsService wordsService;

    public QueryProcessor(CrawlerService crawlerService,WordsService wordsService) throws IOException {
        tokenizer = new Tokenizer();
        stopWordsRemover = new StopWordsRemover();
        synonymization = new Synonymization();
        stemmer = new Stemmer();
        this.crawlerService=crawlerService;
        this.wordsService=wordsService;
    }

    public List<WordsDocument> run(String query) throws IOException {
        //======= Variables Section ========//
        List<String> phrases = extractPhrases(query);        //0. get Phrases
        //==== For Testing Purpose ====== //
//        for (String word : words){
//            System.out.println(word);
//        }
//        if (phrases.isEmpty()){
//            System.out.println("empty");
//        }
        List<String> words = process(query);           //1. process query and return all words after processing
        List<WordsDocument> results = new ArrayList<>();
        System.out.println(words);

        //===== Get Documents into results ===== //
        for (String word : words) {
            results.addAll(wordsService.findWords(word));
        }
        // ==== Handel phrases ==== //
        if (phrases.isEmpty()) return results;

        //===== Remove Urls That doesn't Contain the phrases ===== //
        int urls_cnt = 0;
        for (String phrase : phrases) {
            for (WordsDocument res : results) {
                @SuppressWarnings("unchecked")
                List<Page> urls = res.getPages();   //get key pages that contain all urls
                // === loop through urls and remove it if not contain phrase
                Iterator<Page> iterator = urls.iterator();
                while (iterator.hasNext()) {
                    String url = iterator.next().getUrl();
                    String url_body = crawlerService.getUrlBody(url);
                    if (url_body == null || !url_body.contains(phrase)) {
                        System.out.println("remove: " + url);
                        iterator.remove();
                    }
                }
                if (urls.isEmpty()) return null;
                urls_cnt += urls.size();
            }
        }

        System.out.println(urls_cnt);
        //==== For Testing Purpose ====== //
//        System.out.println("The Count of Results = " + results.size());
//        for (Document result : results) {
//            System.out.println(result.toJson());
//        }
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
        //        for (String phrase : phrases) {
        //            query = query.replaceAll("\"" + phrase + "\"", "").trim();
        //        }
        return phrases;
    }

    public List<String> process(String query) throws IOException {
        List<String> words;
        query = query.replaceAll("[^a-zA-Z1-9]", " "); //1.remove single characters and numbers
        words = tokenizer.runTokenizer(query);                          //2.Convert words to list + toLowerCase
        words = synonymization.runSynonymization(words);                //3.Replace words with its steam synonyms
        words = tokenizer.runTokenizer(query);                          //2.Convert words to list + toLowerCase
        words = stemmer.runStemmer(words);
        words = stopWordsRemover.runStopWordsRemover(words);            //4.Remove Stop Words
        words = words.stream().distinct()                               //5.Remove Duplicates
                .collect(Collectors.toList());
        return words;
    }


}
