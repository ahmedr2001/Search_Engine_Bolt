package com.bolt.Brain.QueryProcessor;

import com.bolt.Brain.Utils.Stemmer;
import com.bolt.Brain.Utils.Tokenizer;
import com.bolt.SpringBoot.CrawlerService;
import com.bolt.SpringBoot.Page;
import com.bolt.SpringBoot.WordsDocument;
import com.bolt.SpringBoot.WordsService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PhraseSearching {
    private final Tokenizer tokenizer;
    private final Stemmer stemmer;
    private CrawlerService crawlerService;
    private WordsService wordService;

    public PhraseSearching(CrawlerService crawlerService,WordsService wordsService) {
        this.tokenizer = new Tokenizer();
        this.stemmer = new Stemmer();
        this.crawlerService=crawlerService;
        this.wordService=wordsService;
    }


    // the idea is to loop though words in phrase
    // get all urls exist in all words
    // then loop on urls and check if contain phrase or not
    public void run(String phrase) {
        // 1 . get all words of phrase
        List<String> words = tokenizer.runTokenizer(phrase);
        words = stemmer.runStemmer(words);
        // 2 . get all urls contain all words exist
        Set<String> urls = new HashSet<>();

        for (String word : words) {

            Set<String> urls_word = getUrlsContainWord(word);                 //store urls contain word
            if(urls_word == null) {
                System.out.println("Error in word : " + word);
                continue;
            }
            if(urls.isEmpty())
                urls.addAll(urls_word);
            else
                urls.retainAll(urls_word);

        }

        Iterator<String> iterator = urls.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            String url_body = crawlerService.getUrlBody(url);
            if (url_body == null) {
                System.out.println("remove: " + url);
                iterator.remove();
            }
        }

        System.out.println("== all urls ====");
        System.out.println(urls);
        //return urls;
    }

    private Set<String> getUrlsContainWord(String word) {
        Set<String> urls_res = new HashSet<>();                 //store urls contain word
        List<WordsDocument> wordDocs = wordService.findWords(word);    //get Documents from inverted file in DB

        // ======= not found a word  in  DB ========
        if(wordDocs == null || wordDocs.isEmpty()) return null;

        for (WordsDocument wordDoc : wordDocs) { //loop though documents contain word:word
            @SuppressWarnings("unchecked")
            List<Page> pages = (List<Page>) wordDoc.getPages();   //get key pages that contain all urls
            for (Page page : pages) {                                   //loop through pages and get url
                String url = page.getUrl();
                urls_res.add(url);
            }
        }
        //System.out.println("word : " + word);
        //System.out.println(urls_res);

        return  urls_res;
    }
}
