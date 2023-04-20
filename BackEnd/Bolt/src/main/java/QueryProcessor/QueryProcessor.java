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


    public QueryProcessor(String query) {
        this.query = query;
        this.DB  = new mongoDB("Bolt");
    }

    public  List<Document> process() throws IOException {
        //======= Variables Section ========//
        Tokenizer tokenizer = new Tokenizer();
        StopWordsRemover stopWordsRemover = new StopWordsRemover();
        Synonymization synonymization = new Synonymization();


        List<String> words;
        List<String> phrases;
        List<Document> results = new ArrayList<>();

        //======= Variables Section ========//

        //===== processing section ===== //
        phrases = extractPhrases(); //0. get Phrases + remove it from query

        query = query.replaceAll("[^a-zA-Z1-9]", " ");  //1.remove single characters and numbers

        words = tokenizer.runTokenizer(query);                          //2.Convert words to list + toLowerCase
        words = synonymization.runSynonymization(words);                //3.Replace words with its steam synonyms
        words = stopWordsRemover.runStopWordsRemover(words);            //4.Remove Stop Words
        words = words.stream().distinct()                               //5.Remove Duplicates
                .collect(Collectors.toList());

        //===== processing section ===== //

        //===== Get Documents into results ===== //
        for(String word: words) {
            results.addAll(DB.getWordDocuments(word));
        }

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

        for (String phrase : phrases) {
            query = query.replaceAll("\"" + phrase + "\"", "").trim();
        }
        return phrases;
    }
}
