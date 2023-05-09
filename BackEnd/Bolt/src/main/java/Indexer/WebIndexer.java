package Indexer;
import DB.mongoDB;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;


public class WebIndexer {
    static final int TH_SZ = 100;
    mongoDB DB;
    public HashMap<String, Integer> indexedPages; //
    static HashMap<String, List<Document>> index; // (Inverted File ) This stores for each word the documents that it was present in

    public WebIndexer(mongoDB db) {
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
        indexedPages = new HashMap<String, Integer>();
        org.jsoup.nodes.Document pageDoc = Jsoup.parse(body);
        Elements allPageElems = pageDoc.getAllElements();

        List<Pair<Pair<String, String>, String>> allWords = new ArrayList<>();
        for (Element elem : allPageElems) {
            String elemName = elem.nodeName();
            String elemText = elem.ownText();
            Cleaner cleaner = new Cleaner() ;
            String cleanElemText = cleaner.runCleaner(elemText);
            // 3 -  Tokenization
            Tokenizer tokenizer = new Tokenizer();
            List<String> elemWords = tokenizer.runTokenizer(cleanElemText);

            // 4 - Removing redundant words (Stop Words)
            StopWordsRemover stopWordsRemover = new StopWordsRemover();
            List<String> elemNoStopWords = stopWordsRemover.runStopWordsRemover(elemWords);
//        for(String word : words) System.out.println("Word: " + word);
            Stemmer stemmer = new Stemmer();
            /** stemWords is final set of words **/
            List<String> finalElemWords = stemmer.runStemmer(elemNoStopWords);
            for (String finalElemWord : finalElemWords) {
                allWords.add(new Pair<>(new Pair<>(elemName, finalElemWord), elemText));
            }
        }

        int totalWords = allWords.size();
        HashMap<String, Document> wordDocMap = new HashMap<String, Document>();
        for (Pair<Pair<String, String>, String> tagWordPair : allWords) {
            String tag = tagWordPair.getKey().getKey();
            String word = tagWordPair.getKey().getValue();
            String snippet = tagWordPair.getValue();

            Document doc = new Document();
            doc.append("tag", tag);
            doc.append("snippet", snippet);
            if (wordDocMap.containsKey(word)) {
                doc.append("TF", wordDocMap.get(word).getInteger("TF") + 1);
            } else {
                doc.append("TF", 1);
            }
            wordDocMap.put(word, doc);
        }

        for (String word : wordDocMap.keySet()) {
            double TF = wordDocMap.get(word).getInteger("TF") / (double) totalWords; // Normalized TF
            String tag = wordDocMap.get(word).getString("tag");
            String snippet = wordDocMap.get(word).getString("snippet");

            Document doc = new Document();
            doc.append("url", url);
            doc.append("_id", id);
            doc.append("TF", TF);
            doc.append("tag", tag);
            doc.append("snippet", snippet);

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
        updateLinkDB();
    }


}
