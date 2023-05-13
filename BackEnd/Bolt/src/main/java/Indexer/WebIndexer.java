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

import javax.print.Doc;


public class WebIndexer {
    static final int TH_SZ = 100;
    static int elemNameIndex = 0;
    static int elemTextIndex = 0;
    mongoDB DB;
    public HashMap<String, Document> indexedUrls; //
    static HashMap<String, List<Document>> indexedWords; // (Inverted File ) This stores for each word the documents that it was present in
    private HashMap<String, Integer> indexedParagraphs;

    public WebIndexer(mongoDB db) {
        indexedWords = new HashMap<String, List<Document>>();
        indexedParagraphs = new HashMap<String, Integer>();
        DB = db;
    }

    public void updateWordsCollection() throws InterruptedException {
        class UpdateWordsCollection implements Runnable {

            List<Thread> thArr = new ArrayList<Thread>();
            List<List<String>> keys = new ArrayList<List<String>>();
            public UpdateWordsCollection() throws InterruptedException {
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
                for (String word : indexedWords.keySet()) {
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
                        DB.addIndexedWord(word, indexedWords.get(word));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        UpdateWordsCollection uDB = new UpdateWordsCollection();
        System.out.println(indexedWords.keySet().size());
//        for (String word : index.keySet()) {
//            DB.addWord(word, index.get(word));
//        }

    }
    public void updateUrlsCollection(String url){
        Integer _id = indexedUrls.get(url).getInteger("_id");
        String title = indexedUrls.get(url).getString("title");
        DB.addIndexedUrl(_id, url, title);
    }

    public void updateParagraphsCollection(String paragraph, Integer paragraphId) {
        DB.addIndexedParagraph(paragraph, paragraphId);
    }

    public void startIndexer(String body, String title, String url, Integer _id){
        if (DB.isUrlIndexed(url)) {
            System.out.println("Page already indexed");
            return;
        }
        indexedUrls = new HashMap<String, Document>();
        org.jsoup.nodes.Document pageDoc = Jsoup.parse(body);
        Elements allPageElems = pageDoc.getAllElements();
        List<Document> allWords = new ArrayList<>();
        for (Element elem : allPageElems) {
            String elemName = elem.nodeName();
            if (elemName.equals("a")) {
                continue;
            }
            String elemText = elem.ownText();
            if (!elemText.equals("")) {
                updateParagraphsCollection(elemText, elemTextIndex);
                elemTextIndex++;
            }
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
            List<String> finalElemWords = stemmer.runStemmer(elemNoStopWords);
            Integer wordIndex = -1;
            for (String finalElemWord : finalElemWords) {
                wordIndex++;
                Document elemNameDoc = new Document();
                elemNameDoc.append("elemName", elemName)
                        .append("elemNameIndex", elemNameIndex);

                Document elemTextDoc = new Document();
                elemTextDoc.append("elemText", elemText)
                        .append("elemTextIndex", elemTextIndex);

                Document wordDoc = new Document();
                wordDoc.append("word", finalElemWord)
                        .append("wordIndex", wordIndex);

                Document allWordDoc = new Document();
                allWordDoc.append("elemNameDoc", elemNameDoc)
                        .append("elemTextDoc", elemTextDoc)
                        .append("wordDoc", wordDoc);

                allWords.add(allWordDoc);
            }
            elemNameIndex++;
        }

        int totalWords = allWords.size();
        HashMap<String, Document> wordDocMap = new HashMap<String, Document>();
        for (Document allWordDoc : allWords) {
            String tagType = allWordDoc.get("elemNameDoc", Document.class).getString("elemName");
            Integer tagIndex = allWordDoc.get("elemNameDoc", Document.class).getInteger("elemNameIndex");
            Integer paragraphIndex = allWordDoc.get("elemTextDoc", Document.class).getInteger("elemTextIndex");
            String word = allWordDoc.get("wordDoc", Document.class).getString("word");
            Integer wordIndex = allWordDoc.get("wordDoc", Document.class).getInteger("wordIndex");

            List<String> tagTypesArr = new ArrayList<String>();
            List<Integer> tagIndexesArr = new ArrayList<Integer>();
            List<Integer> paragraphIndexesArr = new ArrayList<Integer>();
            List<Integer> wordIndexesArr = new ArrayList<Integer>();
            if (wordDocMap.containsKey(word)) {
                tagTypesArr = wordDocMap.get(word).getList("tagTypesArr", String.class);
                tagIndexesArr = wordDocMap.get(word).getList("tagIndexesArr", Integer.class);
                paragraphIndexesArr = wordDocMap.get(word).getList("paragraphIndexesArr", Integer.class);
                wordIndexesArr = wordDocMap.get(word).getList("wordIndexesArr", Integer.class);
            }

            tagTypesArr.add(tagType);
            tagIndexesArr.add(tagIndex);
            paragraphIndexesArr.add(paragraphIndex);
            wordIndexesArr.add(wordIndex);
            Document doc = new Document();
            doc.append("tagTypesArr", tagTypesArr)
                    .append("tagIndexesArr", tagIndexesArr)
                    .append("paragraphIndexesArr", paragraphIndexesArr)
                    .append("wordIndexesArr", wordIndexesArr);
            if (wordDocMap.containsKey(word)) {
                doc.append("TF", wordDocMap.get(word).getInteger("TF") + 1);
            } else {
                doc.append("TF", 1);
            }
            wordDocMap.put(word, doc);
        }

        for (String word : wordDocMap.keySet()) {
            double TF = wordDocMap.get(word).getInteger("TF") / (double) totalWords; // Normalized TF
            List<String> tagTypes = wordDocMap.get(word).getList("tagTypesArr", String.class);
            List<Integer> tagIndexes = wordDocMap.get(word).getList("tagIndexesArr", Integer.class);
            List<Integer> paragraphIndexes = wordDocMap.get(word).getList("paragraphIndexesArr", Integer.class);
            List<Integer> wordIndexes = wordDocMap.get(word).getList("wordIndexesArr", Integer.class);

            Document doc = new Document();
            doc.append("_id", _id)
                    .append("TF", TF)
                    .append("tagTypes", tagTypes)
                    .append("tagIndexes", tagIndexes)
                    .append("paragraphIndexes", paragraphIndexes)
                    .append("wordIndexes", wordIndexes);

            if (TF < 0.5) { // Avoiding spamming
                if (indexedWords.containsKey(word)) {
                    indexedWords.get(word).add(doc);
                } else {
                    List<Document> docArray = new ArrayList<Document>();
                    docArray.add(doc);
                    indexedWords.put(word, docArray);
                }
            }
        }
        Document urlDoc = new Document("_id", _id)
                .append("url", url)
                .append("title", title);
        indexedUrls.put(url, urlDoc);
        updateUrlsCollection(url);
    }


}
