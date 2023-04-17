package Indexer;
import DB.mongoDB;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WebIndexer {

    public WebIndexer() {
        indexedPages = new HashMap<String, Integer>();
        index = new HashMap<String, List<Document>>();
    }
    mongoDB DB ;
    HashMap<String, Integer> indexedPages; //
    HashMap<String, List<Document>> index; // (Inverted File ) This stores for each word the documents that it was present in

    public void startIndexer(String body , String url, Object id){
        // 0 - Connecting to Database
        DB = new mongoDB("Bolt");
        // 1 - Checking if the page has been indexed before
//        if (DB.isIndexed(url)) {
//            System.out.println("This page has been indexed before");
//            return;
//        } else {
//            System.out.println("New page is being indexed ");
//        }
        // 2 - Cleaning
        Cleaner cleaner = new Cleaner() ;
        body = cleaner.runCleaner(body);
        System.out.println("After Cleaning");
        // 3 -  Tokenization
        Tokenizer tokenizer = new Tokenizer();
        List<String> words = tokenizer.runTokenizer(body);

        // 4 - Removing redundant words (Stop Words)
        StopWordsRemover stopWordsRemover = new StopWordsRemover();
        words = stopWordsRemover.runStopWordsRemover(words);
//        for(String word : words) System.out.println("Word: " + word);
        Stemmer stemmer = new Stemmer();
        List<String> stemWords = stemmer.runStemmer(words);
        for (int i = 0; i < stemWords.size(); i++) {
            System.out.printf("Word: %s, Stem Word: %s\n", words.get(i), stemWords.get(i));
        }
        int totalWords = stemWords.size();
        HashMap<String, Document> Words_TF = new HashMap<String, Document>();
        for (String stemWord : stemWords) {
            Document doc = new Document();
            if (Words_TF.containsKey(stemWord)) {
                doc.append("TF", Words_TF.get(stemWord).getInteger("TF") + 1);
            } else {
                doc.append("TF", 1);
            }
            Words_TF.put(stemWord, doc);
        }

        for (String word : Words_TF.keySet()) {
            double TF = Words_TF.get(word).getInteger("TF") / (double) totalWords;

            Document doc = new Document();
            doc.append("url", url);
            doc.append("_id", id);
            doc.append("TF", TF);

            if (TF < 0.5) {
                if (index.containsKey(word)) {
                    index.get(word).add(doc);
                } else {
                    List<Document> docArray = new ArrayList<Document>();
                    docArray.add(doc);
                    index.put(word, docArray);
                }
            }
        }

        indexedPages.put(url, totalWords);
    }


}
