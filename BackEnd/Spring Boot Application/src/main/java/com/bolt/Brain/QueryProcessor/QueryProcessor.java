package com.bolt.Brain.QueryProcessor;

import com.bolt.Brain.DataStructures.UrlParapraphContentDict;
import com.bolt.Brain.Utils.Stemmer;
import com.bolt.Brain.Utils.StopWordsRemover;
import com.bolt.Brain.Utils.Tokenizer;
import com.bolt.SpringBoot.*;

import java.io.IOException;
import java.io.Serial;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryProcessor {
    private final Tokenizer tokenizer;
    private final Stemmer stemmer;
    private final StopWordsRemover stopWordsRemover;
    private CrawlerService crawlerService;
    private WordsService wordsService;

    private ParagraphService paragraphService;

    List<String> originalWords ;



    public QueryProcessor(CrawlerService crawlerService,WordsService wordsService, ParagraphService paragraphService)  {
        tokenizer = new Tokenizer();
        stopWordsRemover = new StopWordsRemover();
        stemmer = new Stemmer();
        this.crawlerService = crawlerService;
        this.wordsService = wordsService;
        this.paragraphService = paragraphService;
        originalWords = new ArrayList<>();
    }

    public List<String> getOriginalWords(){
        return originalWords;
    }
    public List<WordsDocument> run(String query) throws IOException {
        //======= Variables Section ========//
        List<String> phrases = extractPhrases(query);           //0. get Phrases
        query = removePhraseFromQuery(query, phrases);
        List<String> words = process(query);                    //1. process query and return all words after processing
        List<WordsDocument> results = getWordsResult(words);    //2. get normal query results
        List<List<WordsDocument>> phrase_results = getPhraseResults(phrases);



        if(results.size()  > 0 && phrase_results.size() > 0) {
            //TODO: combine two lists and return it
            results.addAll(phrase_results.stream().flatMap(Collection::stream).toList());

        }
        else if(phrase_results.size() > 0) {
            //TODO: BONUS BUT now let combine results
            return phrase_results.stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        // else
        return results;
    }

    private void printingResults(List<List<WordsDocument>> phrase_results) {
        for(List<WordsDocument> phrase_res: phrase_results) {
            for(WordsDocument wDoc: phrase_res) {
                for(Page pg: wDoc.getPages()) {
                    for(Integer pix: pg.getParagraphIndexes()) {
                        String paragraph = paragraphService.findParagraph(pix).getParagraph();
                        System.out.println(pg.getUrlId() + " " + pix + ": " + paragraph);
                    }
                }
            }
        }
    }
    private List<String> extractPhrases(String query) {
        List<String> phrases = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            phrases.add(matcher.group(1));
        }

        return phrases;
    }
    private String removePhraseFromQuery(String query, List<String> phrases) {
        // This Code to remove phrase from query But I don't need it now
        for (String phrase : phrases) {
            query = query.replace("\"" + phrase + "\"", "").trim();
        }
        return query.replaceAll("\\s+", " ");
    }


    private List<String> basicProcess(String query) {
        List<String> words;
        words = tokenizer.runTokenizer(query);                          //1.Convert words to (list + toLowerCase)
        words = stopWordsRemover.runStopWordsRemover(words);            //2.Remove Stop Words
        return words;
    }

    private List<String> process(String query) throws IOException {
        List<String> words;
        words = basicProcess(query);                                    // [ convert it to words, remove stop words]
        originalWords = words;
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


    // get results for one phrase
    private List<WordsDocument> getPhraseResult(String phrase) throws IOException {
        List<String> phraseWordsStemming = process(phrase);
        List<String> phraseWords = basicProcess(phrase);

        Pattern phrasePattern = regexPatternPhrase(phraseWords);
        //get phrase results as normal
        List<WordsDocument> results = getWordsResult(phraseWordsStemming);
        // store processed paragraphs index so not process again
        HashMap<Integer, Boolean> pargraphIndexsStore = new HashMap<>();

        Iterator<WordsDocument> iteratorWDoc = results.iterator();
        while(iteratorWDoc.hasNext()) {                                  //1. loop on word search results
            WordsDocument wDoc = iteratorWDoc.next();
            Iterator<Page> iteratorPages = wDoc.getPages().iterator();
            while(iteratorPages.hasNext()) {                                //2. loop on urls
                Page pg = iteratorPages.next();
                List<Integer> paragraphIndexes = pg.getParagraphIndexes();
                for(int i = 0;i < paragraphIndexes.size();i++ ) {           //3. loop on p in url
                    Integer paragraphId = paragraphIndexes.get(i) ;
                    //3.1 if it already processed skip
                    if(pargraphIndexsStore.containsKey(paragraphId)) {
                        if(!pargraphIndexsStore.get(paragraphId)) {
                            removeParagraphData(pg, i);
                            i--;
                        }
                        continue;
                    }

                    String paragraph = paragraphService.findParagraph(paragraphId).getParagraph();

                    if(! wordsExistInParagraph(phrasePattern, paragraph)) {           // check pattern matcher
                        removeParagraphData(pg, i);
                        i--; // to calibrate loop
                        pargraphIndexsStore.put(paragraphId, false);

                    } else  pargraphIndexsStore.put(paragraphId, true);
                }
                if(pg.getParagraphIndexes().isEmpty()) iteratorPages.remove();
            }
            if(wDoc.getPages().isEmpty()) iteratorWDoc.remove();
        }
        return results;
    }

    // loop on phrases and get all results
    private List<List<WordsDocument>> getPhraseResults(List<String> phrases) throws IOException {
        List<List<WordsDocument>> phrase_results = new ArrayList<>();
        for(String phrase : phrases) {
            List<WordsDocument> single_phrase_result = getPhraseResult(phrase);
            phrase_results.add(single_phrase_result);
        }
        return phrase_results;
    }

    private Pattern regexPatternPhrase(List<String> words) {
        String regex = ".*" + String.join(".*", words) + ".*";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    private boolean wordsExistInParagraph(Pattern pattern, String paragraph) {
        Matcher matcher = pattern.matcher(paragraph);
        return  matcher.matches();
    }

    private void removeParagraphData(Page pg, int i) {
        if(pg.getTagIndexes()!=null) {
            pg.getTagIndexes().remove(i);
        }
        if(pg.getParagraphIndexes() != null) {
            pg.getParagraphIndexes().remove(i);
        }
        if(pg.getWordIndexes() != null) {
            pg.getWordIndexes().remove(i);
        }
        if(pg.getTagTypes() != null) {
            pg.getTagTypes().remove(i);
        }
    }


}