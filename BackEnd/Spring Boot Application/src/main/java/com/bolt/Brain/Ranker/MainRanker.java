package com.bolt.Brain.Ranker;

import com.bolt.Brain.DataStructures.Sorting;
import com.bolt.SpringBoot.Page;
import com.bolt.SpringBoot.UrlDocument;
import com.bolt.SpringBoot.UrlsService;
import com.bolt.SpringBoot.WordsDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainRanker {

    List<WordsDocument> RelatedDocuments;
    List<String> Ranked_Result;
    HashMap<String, Double> Page_Score;

    UrlsService urlsService;
    private HashMap<String, Integer> numberOfWordsOnEachPage;

    public MainRanker(List<WordsDocument> QueryResult, UrlsService urlsService) {
        RelatedDocuments = QueryResult;
        this.urlsService = urlsService;
    }

    public void main(String[] args) {
        runRanker();
    }

    public List<String> runRanker() {
        // Testing purpose --> Uncomment for testing
//        RelatedDocuments = DB.getWordDocuments("cancel");
        if (RelatedDocuments == null) return null;
        Ranked_Result = new ArrayList<>();
        Page_Score = new HashMap<>();
        numberOfWordsOnEachPage = new HashMap<>();
        Calculate_Rank();
        Rank_Urls();
        for (String url : Ranked_Result) System.out.println(url);
        return Ranked_Result;
    }

    void Calculate_Rank() {
        for (WordsDocument word_doc : RelatedDocuments) {
            calculate_TF_IDF(word_doc);
        }
    }

    void calculate_TF_IDF(WordsDocument word_doc) {
        System.out.println(word_doc);
        double idf = (Double) word_doc.getIDF();
        ArrayList<Page> pages = (ArrayList<Page>) word_doc.getPages();
        Iterator<Page> pages_iterable = pages.iterator();
        while (pages_iterable.hasNext()) {
            Page page = pages_iterable.next();
            System.out.println(page);
            double tf = (Double) page.getTF();
            String url = (String) urlsService.findUrl(page.getUrlId())  ;
            if(url == null) continue;
            double rank = urlsService.findRank(url);
            double TF_IDF = idf * tf;
            if (Page_Score.get(url) == null) {
                Page_Score.put(url, TF_IDF+rank);
                numberOfWordsOnEachPage.put(url, 1);
            } else {
                Page_Score.put(url, Page_Score.get(url) + TF_IDF+rank);
                numberOfWordsOnEachPage.put(url, numberOfWordsOnEachPage.get(url) + 1);
            }
        }
    }

    void Rank_Urls() {
        Ranked_Result = Sorting.sortByValue(Page_Score);
        // Testing
//        for(String url : Ranked_Result ) {
//            System.out.println(url + "    :") ;
//            System.out.println(Page_Score.get(url));
//        }
    }

}
