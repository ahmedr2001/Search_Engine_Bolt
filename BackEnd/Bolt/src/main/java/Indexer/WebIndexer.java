package Indexer;
import DB.mongoDB;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WebIndexer {
    static final int TH_SZ = 1000;
    mongoDB DB;
    static HashMap<String, Integer> indexedPages; //
    static HashMap<String, List<Document>> index; // (Inverted File ) This stores for each word the documents that it was present in

    public WebIndexer(mongoDB db) {
        indexedPages = new HashMap<String, Integer>();
        index = new HashMap<String, List<Document>>();
        DB = db;
    }

    public void updateWordDB() throws InterruptedException {
        class UpdateWordDB implements Runnable {

            List<Thread> thArr = new ArrayList<Thread>();
            List<List<String>> keys = new ArrayList<List<String>>();
            public UpdateWordDB() throws InterruptedException {
                for (int i = 0; i < TH_SZ; i++) {
                    keys.add(new ArrayList<String>());
                }
                for (int i = 0; i < TH_SZ; i++) {
                    Thread th = new Thread(this);
                    String I = Integer.toString(i, 10);
                    th.setName(I);
                    thArr.add(th);
                }

                int cnt = 0;
                for (String word : index.keySet()) {
                    int idx = cnt % TH_SZ;
                    keys.get(idx).add(word);
                    cnt++;
                }

                for (Thread th : thArr) {
                    th.start();
                }

                for (Thread th : thArr) {
                    th.join();
                }
            }
            public void run() {
                try {
                    Thread t = Thread.currentThread();
                    String name = t.getName();
                    int idx = Integer.parseInt(name);
//                    System.out.println(idx);
                    for (String word : keys.get(idx)) {
                        DB.addWord(word, index.get(word));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        UpdateWordDB uDB = new UpdateWordDB();
        System.out.println(index.keySet().size());
//        for (String word : index.keySet()) {
//            DB.addWord(word, index.get(word));
//        }

    }
    public void updateLinkDB(){
        for (String url : indexedPages.keySet()) {
            DB.addIndexedPage(url, indexedPages.get(url));
        }
    }

    public void startIndexer(String body , String url, Object id){
        if (DB.isIndexed(url)) {
            System.out.println("Page already indexed");
            return;
        }
//        System.out.println("Index this page");
        // 1 - Checking if the page has been indexed before
        // 2 - Cleaning
        Cleaner cleaner = new Cleaner() ;
        body = cleaner.runCleaner(body);
        // 3 -  Tokenization
        Tokenizer tokenizer = new Tokenizer();
        List<String> words = tokenizer.runTokenizer(body);

        // 4 - Removing redundant words (Stop Words)
        StopWordsRemover stopWordsRemover = new StopWordsRemover();
        words = stopWordsRemover.runStopWordsRemover(words);
//        for(String word : words) System.out.println("Word: " + word);
        Stemmer stemmer = new Stemmer();
        List<String> stemWords = stemmer.runStemmer(words);
//        for (int i = 0; i < stemWords.size(); i++) {
//            System.out.printf("Word: %s, Stem Word: %s\n", words.get(i), stemWords.get(i));
//        }
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
            double TF = Words_TF.get(word).getInteger("TF") / (double) totalWords; // Normalized TF

            Document doc = new Document();
            doc.append("url", url);
            doc.append("_id", id);
            doc.append("TF", TF);

            if (TF < 0.5) { // Avoiding spamming
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
