package com.bolt.Brain.QueryProcessor;

import com.bolt.Brain.DataStructures.Pair;
import com.bolt.Brain.Utils.Stemmer;
import com.bolt.Brain.Utils.StopWordsRemover;
import com.bolt.Brain.Utils.Tokenizer;
import com.bolt.SpringBoot.CrawlerService;
import com.bolt.SpringBoot.Page;
import com.bolt.SpringBoot.WordsDocument;
import com.bolt.SpringBoot.WordsService;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    public  class UrlParagraphContent {
        public Integer wordIndex;
        public Integer wordIndexPhrase;

        Page refPage;


        Integer paragraphIndexArr; //

        public UrlParagraphContent(Integer wordIndex, Integer wordIndexPhrase, Page refPage, Integer paragraphIndexArr) {
            this.wordIndex = wordIndex;
            this.wordIndexPhrase = wordIndexPhrase;
            this.refPage = refPage;
            this.paragraphIndexArr = paragraphIndexArr;
        }

        public  void remove() {
            int indexToRemove = paragraphIndexArr;
            refPage.getParagraphIndexes().remove(indexToRemove);
            refPage.getWordIndexes().remove(indexToRemove);
            refPage.getTagIndexes().remove(indexToRemove);
            refPage.getTagTypes().remove(indexToRemove);
        }
    }

    public  class UrlParagraphContentSorter {

        public static void sortByWordIndex(List<UrlParagraphContent> list) {
            Comparator<UrlParagraphContent> comparator = Comparator.comparingInt(o -> o.wordIndex);
            list.sort(comparator);
        }

    }

    public QueryProcessor(CrawlerService crawlerService,WordsService wordsService)  {
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
        List<WordsDocument> results = getWordsResult(words);
        System.out.println(words);



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


    public List<String> basicProcess(String query) {
        List<String> words;
        words = tokenizer.runTokenizer(query);                          //1.Convert words to (list + toLowerCase)
        words = stopWordsRemover.runStopWordsRemover(words);            //2.Remove Stop Words
        return words;
    }

    public List<String> process(String query) throws IOException {
        List<String> words;
        words = basicProcess(query);                                    // [ convert it to words, remove stop words]
        words = stemmer.runStemmer(words);                              //3.return to it's base
        words = words.stream().distinct()                               //4.Remove Duplicates
                .collect(Collectors.toList());
        return words;
    }

    private List<WordsDocument> getWordsResult(List<String> words) {
        List<WordsDocument> results = new ArrayList<>();

        //===== Get Documents into results ===== //
        for (String word : words) {
            results.addAll(wordsService.findWords(word));
        }
        return results;
    }

    private List<WordsDocument> getPhraseResult(String phrase) {
        List<String> phraseWords = basicProcess(phrase);
        HashMap<Integer, List<WordsDocument>> resultsPerWord = getWordsHashResult(phraseWords);
        List<WordsDocument> results = new ArrayList<>();
        // create dict of words of [same paragraph & same url]
        // dict key is word index inside paragraph
        // dict val is word index inside phrase
        HashMap<Pair<Integer,Integer>, List<UrlParagraphContent>> urlParapraphContentDict = new HashMap<>();

        //creating urlParapraphContentDict
        for(Integer wordIndex: resultsPerWord.keySet()) {               // 1. loop on index of wrd in phrase
            for (WordsDocument wDoc : resultsPerWord.get(wordIndex)) {  // 2. loop result of each on word
                for (Page pg : wDoc.getPages()) {                       // 3. loop on urls Content Details
                    Integer urlIndex = pg.getId();
                    List<Integer> paragraphIndex = pg.getParagraphIndexes();
                    List<Integer> wordIndexInP = pg.getWordIndexes();
                    for(int i = 0;i < paragraphIndex.size();i++) {      // 4. loop on details of each exist word in doc

                        Pair<Integer, Integer> key = new Pair<>(urlIndex, paragraphIndex.get(i));
                        List<UrlParagraphContent> val;
                        // if exist get it -> else create new List
                        if(urlParapraphContentDict.containsKey(key)) {
                            val = urlParapraphContentDict.get(key);
                        } else val = new ArrayList<>();
                        // add the content
                        val.add(new UrlParagraphContent(wordIndexInP.get(i),wordIndex, pg, i));
                        // add it to dict
                        urlParapraphContentDict.put(key, val);
                    }
                }

            }
        }

        //filtering urlParapraphContentDict
        for(List<UrlParagraphContent> urlParagraphContents : urlParapraphContentDict.values()) {
            // sort it by wordIndexInParagraph
            UrlParagraphContentSorter.sortByWordIndex(urlParagraphContents);

            // loop on them and make it sure it's 0 .... 1 .... 2 ..etc.. n
            int shouldWordIndex = 0;
            for(UrlParagraphContent content: urlParagraphContents) {
                    if(content.wordIndexPhrase == shouldWordIndex)
                        shouldWordIndex++;
                    if(shouldWordIndex == phraseWords.size()) break; // success stop lopping
            }
            //fail should remove it
            if(shouldWordIndex != phraseWords.size()) {
                break;
            }

        }
        return results;
    }

    private HashMap<Integer, List<WordsDocument>> getWordsHashResult(List<String> phraseWords) {
        HashMap<Integer, List<WordsDocument>> results = new  HashMap<Integer, List<WordsDocument>>();

        //===== Get Documents into results ===== //
        for(int i = 0;i < phraseWords.size();i++) {
            results.put(i, wordsService.findWords(phraseWords.get(i)));
        }
        return results;
    }

}
