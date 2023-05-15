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
import java.util.concurrent.ConcurrentHashMap;

import javafx.util.Pair;

import javax.print.Doc;


public class WebIndexer {
    static final int TH_SZ = 2;
    static int elemTextIndex = -1;
    mongoDB DB;
    private HashMap<String, Document> indexedUrls; //
    private ConcurrentHashMap<String, List<Document>> indexedWords; // (Inverted File ) This stores for each word the documents that it was present in

    public WebIndexer(mongoDB db) {
        DB = db;
        synchronized (this) {
            elemTextIndex = DB.getNumOfIndexedParagraphs() - 1;
        }
    }

    public void updateWordsCollection() throws InterruptedException {
        class UpdateWordsCollection implements Runnable {

            List<Thread> thArr = new ArrayList<Thread>();
            List<List<String>> keys = new ArrayList<List<String>>();
            public UpdateWordsCollection() throws InterruptedException {
//                System.out.printf("Unique words: %d\n",indexedWords.keySet().size());
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
//        for (String word : index.keySet()) {
//            DB.addWord(word, index.get(word));
//        }

    }
    public void updateUrlsCollection(String url){
        Integer _id = indexedUrls.get(url).getInteger("_id");
        String title = indexedUrls.get(url).getString("title");
//        synchronized (this) {
        DB.addIndexedUrl(_id, url, title);
//        }
    }

    public void updateParagraphsCollection(String paragraph, Integer paragraphId) {
//        synchronized (this) {
        DB.addIndexedParagraph(paragraph, paragraphId);
//        }
    }

    public void startIndexer(String body, String title, String url, Integer _id) throws InterruptedException {
        if (DB.isUrlIndexed(_id)) {
//            System.out.println("Page already indexed");
            return;
        }
        indexedWords = new ConcurrentHashMap<String, List<Document>>();
        indexedUrls = new HashMap<String, Document>();
        org.jsoup.nodes.Document pageDoc = Jsoup.parse(body);
        Elements allPageElems = pageDoc.getAllElements();
        List<Document> allWords = new ArrayList<>();
        int localElemTextIndex = 0, wordIndex;
        String paragraph = "";
        String elemName = "", elemText, cleanElemText;
        Cleaner cleaner;
        Tokenizer tokenizer;
        StopWordsRemover stopWordsRemover;
        Stemmer stemmer;
        List<String> elemWords, elemNoStopWords, finalElemWords;
        for (Element elem : allPageElems) {
            elemName = elem.nodeName();
            if (elemName.equals("a")) {
                continue;
            }
            elemText = elem.ownText();
            if (elemText.equals("")) {
                continue;
            }
            paragraph += elemText;
            paragraph += " ";
            if (paragraph.length() >= 200) {
                synchronized (this) {
                    elemTextIndex++;
                    localElemTextIndex = elemTextIndex;
                }
                updateParagraphsCollection(paragraph, localElemTextIndex);
                cleaner = new Cleaner() ;
                cleanElemText = cleaner.runCleaner(paragraph);
                // 3 -  Tokenization
                tokenizer = new Tokenizer();
                elemWords = tokenizer.runTokenizer(cleanElemText);

                // 4 - Removing redundant words (Stop Words)
                stopWordsRemover = new StopWordsRemover();
                elemNoStopWords = stopWordsRemover.runStopWordsRemover(elemWords);
                //        for(String word : words) System.out.println("Word: " + word);
                stemmer = new Stemmer();
                finalElemWords = stemmer.runStemmer(elemNoStopWords);
                wordIndex = -1;
                String originalWord;
                Document elemNameDoc, elemTextDoc, wordDoc, allWordDoc;
                for (String finalElemWord : finalElemWords) {
                    wordIndex++;
                    originalWord = elemNoStopWords.get(wordIndex);
                    elemNameDoc = new Document();
                    elemNameDoc.append("elemName", elemName);

                    elemTextDoc = new Document();
                    elemTextDoc.append("elemText", paragraph)
                            .append("elemTextIndex", localElemTextIndex);

                    wordDoc = new Document();
                    wordDoc.append("word", finalElemWord)
                            .append("originalWord", originalWord)
                            .append("wordIndex", wordIndex);

                    allWordDoc = new Document();
                    allWordDoc.append("elemNameDoc", elemNameDoc)
                            .append("elemTextDoc", elemTextDoc)
                            .append("wordDoc", wordDoc);

                    allWords.add(allWordDoc);
                }
                paragraph = "";
            }
        }
        if (paragraph.length() > 0) {
            synchronized (this) {
                elemTextIndex++;
                localElemTextIndex = elemTextIndex;
            }
            updateParagraphsCollection(paragraph, localElemTextIndex);
            cleaner = new Cleaner() ;
            cleanElemText = cleaner.runCleaner(paragraph);
            // 3 -  Tokenization
            tokenizer = new Tokenizer();
            elemWords = tokenizer.runTokenizer(cleanElemText);

            // 4 - Removing redundant words (Stop Words)
            stopWordsRemover = new StopWordsRemover();
            elemNoStopWords = stopWordsRemover.runStopWordsRemover(elemWords);
            //        for(String word : words) System.out.println("Word: " + word);
            stemmer = new Stemmer();
            finalElemWords = stemmer.runStemmer(elemNoStopWords);
            wordIndex = -1;
            String originalWord;
            Document elemNameDoc, elemTextDoc, wordDoc, allWordDoc;
            for (String finalElemWord : finalElemWords) {
                wordIndex++;
                originalWord = elemNoStopWords.get(wordIndex);
                elemNameDoc = new Document();
                elemNameDoc.append("elemName", elemName);

                elemTextDoc = new Document();
                elemTextDoc.append("elemText", paragraph)
                        .append("elemTextIndex", localElemTextIndex);

                wordDoc = new Document();
                wordDoc.append("word", finalElemWord)
                        .append("originalWord", originalWord)
                        .append("wordIndex", wordIndex);

                allWordDoc = new Document();
                allWordDoc.append("elemNameDoc", elemNameDoc)
                        .append("elemTextDoc", elemTextDoc)
                        .append("wordDoc", wordDoc);

                allWords.add(allWordDoc);
            }
            paragraph = "";
        }

        int totalWords = allWords.size();
        HashMap<String, Document> wordDocMap = new HashMap<String, Document>();
        for (Document allWordDoc : allWords) {
            String tagType = allWordDoc.get("elemNameDoc", Document.class).getString("elemName");
            Integer paragraphIndex = allWordDoc.get("elemTextDoc", Document.class).getInteger("elemTextIndex");
            String word = allWordDoc.get("wordDoc", Document.class).getString("word");
            String originalWord = allWordDoc.get("wordDoc", Document.class).getString("originalWord");
            wordIndex = allWordDoc.get("wordDoc", Document.class).getInteger("wordIndex");

            List<String> originalWordsArr = new ArrayList<String>();
            List<String> tagTypesArr = new ArrayList<String>();
            List<Integer> paragraphIndexesArr = new ArrayList<Integer>();
            List<Integer> wordIndexesArr = new ArrayList<Integer>();
            if (wordDocMap.containsKey(word)) {
                originalWordsArr = wordDocMap.get(word).getList("originalWordsArr", String.class);
                tagTypesArr = wordDocMap.get(word).getList("tagTypesArr", String.class);
                paragraphIndexesArr = wordDocMap.get(word).getList("paragraphIndexesArr", Integer.class);
                wordIndexesArr = wordDocMap.get(word).getList("wordIndexesArr", Integer.class);
            }

            originalWordsArr.add(originalWord);
            tagTypesArr.add(tagType);
            paragraphIndexesArr.add(paragraphIndex);
            wordIndexesArr.add(wordIndex);
            Document doc = new Document();
            doc.append("originalWordsArr", originalWordsArr)
                    .append("tagTypesArr", tagTypesArr)
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
            List<String> originalWords = wordDocMap.get(word).getList("originalWordsArr", String.class);
            List<String> tagTypes = wordDocMap.get(word).getList("tagTypesArr", String.class);
            List<Integer> paragraphIndexes = wordDocMap.get(word).getList("paragraphIndexesArr", Integer.class);
            List<Integer> wordIndexes = wordDocMap.get(word).getList("wordIndexesArr", Integer.class);

            Document doc = new Document();
            doc.append("urlId", _id)
                    .append("originalWords", originalWords)
                    .append("TF", TF)
                    .append("tagTypes", tagTypes)
                    .append("paragraphIndexes", paragraphIndexes)
                    .append("wordIndexes", wordIndexes);

            if (TF < 0.5) { // Avoiding spamming
                if (indexedWords.containsKey(word)) {
                    synchronized (this) {
                        indexedWords.get(word).add(doc);
                    }
                } else {
                    List<Document> docArray = new ArrayList<Document>();
                    docArray.add(doc);
                    synchronized (this) {
                        indexedWords.put(word, docArray);
                    }
                }
            }
        }
        Document urlDoc = new Document("_id", _id)
                .append("url", url)
                .append("title", title);
        indexedUrls.put(url, urlDoc);
        updateUrlsCollection(url);
        updateWordsCollection();
    }


}