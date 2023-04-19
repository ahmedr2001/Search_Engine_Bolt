package QueryProcessor;
import DB.mongoDB;
import Indexer.Stemmer;
import Indexer.StopWordsRemover;
import Indexer.Tokenizer;
import org.bson.Document;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryProcessor {
    private String query;
    private final mongoDB DB;

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        String query = scanner.nextLine();

        QueryProcessor queryProcessor = new QueryProcessor(query);

        queryProcessor.process();

    }
    public QueryProcessor(String query) {
        this.query = query;
        this.DB  = new mongoDB("Bolt");
    }

    public  void process() {
        //======= Variables Section ========//
        Tokenizer tokenizer = new Tokenizer();
        StopWordsRemover stopWordsRemover = new StopWordsRemover();
        Stemmer stemmer = new Stemmer();

        List<String> words;
        List<String> phrases;
        List<Document> results = new ArrayList<>();

        //======= Variables Section ========//

        //===== processing section ===== //
        phrases = extractPhrases(); //0. get Phrases + remove it from query

        query = query.replaceAll("[^a-zA-Z1-9]", " ");  //1.remove single characters and numbers

        words = tokenizer.runTokenizer(query);                          //2.Convert words to list + toLowerCase
        words = words.stream().distinct()                               //3.Remove Duplicates
                .collect(Collectors.toList());

        words = stopWordsRemover.runStopWordsRemover(words);            //4.Remove Stop Words
        words = stemmer.runStemmer(words);                              //5.Replace words with its steam

//        for (String word : words) {
//            //Set<String> synonyms =  getSynonyms(word);
//            if (synonyms != null && !synonyms.isEmpty()) {
//                synonymWords.addAll(synonyms);
//            }
//        }
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
