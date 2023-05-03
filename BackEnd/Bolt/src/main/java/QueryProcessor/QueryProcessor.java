package QueryProcessor;
import DB.mongoDB;
import Indexer.Stemmer;
import Indexer.StopWordsRemover;
import Indexer.Tokenizer;
import org.bson.Document;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryProcessor {
    private String query;
    private final mongoDB DB;
    private final Tokenizer tokenizer;
    private final Stemmer stemmer;
    private final StopWordsRemover stopWordsRemover;
    private final Synonymization synonymization;
    public static void main(String [] args) throws IOException {
        QueryProcessor queryProcessor = new QueryProcessor("\"java\"");
        queryProcessor.run();
//        s.replaceAll(" ")
//        List<String> phrases = new ArrayList<>();
//        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
//        Matcher matcher = pattern.matcher(query);
    }
    public QueryProcessor(String query) throws IOException {
        this.query = query;
        this.DB  = new mongoDB("Bolt");
        tokenizer = new Tokenizer();
        stopWordsRemover = new StopWordsRemover();
        synonymization = new Synonymization();
        stemmer = new Stemmer();

    }

    public  List<Document> run() throws IOException {
        //======= Variables Section ========//
        List<String> phrases = extractPhrases();        //0. get Phrases
        List<String> words  = process(query);           //1. process query and return all words after processing
        List<Document> results = new ArrayList<>();
        System.out.println(words);

        if(phrases.isEmpty()) return results;
        //===== Get Documents into results ===== //
        for(String word: words) {
            results.addAll(DB.getWordDocuments(word));
        }
        //System.out.println(results);
        //===== Remove Urls That doesn't Contain the phrases ===== //
        int urls_cnt = 0;
        for(String phrase : phrases) {
            for(Document res : results) {
                @SuppressWarnings("unchecked")
                List<Document> urls = (List<Document>) res.get("pages");   //get key pages that contain all urls
                // === loop through urls and remove it if not contain phrase
                Iterator<Document> iterator = urls.iterator();
                while (iterator.hasNext()) {
                    String url = iterator.next().getString("url");
                    String url_body = DB.getUrlBody(url);
                    if (url_body == null || !url_body.contains(phrase)) {
                        System.out.println("remove: " + url);
                        iterator.remove();
                    }
                }
                if(urls.isEmpty()) return null;
                urls_cnt += urls.size();
            }
        }

        System.out.println(urls_cnt);
        //==== For Testing Purpose ====== //
//        System.out.println("The Count of Results = " + results.size());
//        for (Document result : results) {
//            System.out.println(result.toJson());
//        }
        return  results;
    }

    public List<String> extractPhrases() {
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
