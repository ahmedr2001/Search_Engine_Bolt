package QueryProcessor;
import DB.mongoDB;
import Indexer.Stemmer;
import Indexer.StopWordsRemover;
import Indexer.Tokenizer;
import ca.rmen.porterstemmer.PorterStemmer;
import org.bson.Document;
import java.util.*;
import java.util.stream.Collectors;

public class QueryProcessor {
    private String query;
    private mongoDB DB;
    private PorterStemmer porterStemmer;
    private List<String> stemWords;

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        String query = scanner.nextLine();
        System.out.println(query);
        QueryProcessor queryProcessor = new QueryProcessor(query);

        queryProcessor.process();

    }
    public QueryProcessor(String query) {
        this.query = query;
        this.DB  = new mongoDB("Bolt");
        this.porterStemmer = new PorterStemmer();
    }

    public  void process() {
        //======= Variables Section ========//
        Tokenizer tokenizer = new Tokenizer();
        StopWordsRemover stopWordsRemover = new StopWordsRemover();
        Stemmer stemmer = new Stemmer();
        List<String> words;
        List<Document> results = new ArrayList<>();
        //======= Variables Section ========//

        //===== processing section ===== //
        query = query.replaceAll("[^a-zA-Z1-9]", " ");  //1.remove single characters and numbers
        words = tokenizer.runTokenizer(query);                          //2.Convert words to list + toLowerCase
        words = words.stream().distinct()                               //3.Remove Duplicates
                .collect(Collectors.toList());

        words = stopWordsRemover.runStopWordsRemover(words);            //4.Remove Stop Words
        words = stemmer.runStemmer(words);                              //5.Replace words with its steam
        //===== processing section ===== //


        //===== Get Documents into results ===== //
        for(String word: words) {
            results.addAll(DB.getWordDocuments(word));
        }

        //==== For Testing Purpose ====== //
        System.out.println("The Count of Results = " + results.size());
        for (Document result : results) {
            System.out.println(result.toJson());
        }

    }
}
