package Ranker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import DB.mongoDB;
import org.bson.Document;

import javax.print.Doc;


public class MainRanker {

    List<Document> RelatedDocuments ;
    List<String> Ranked_Result;
    HashMap<String , Double> Page_Score;
    private HashMap<String, Integer> numberOfWordsOnEachPage;
    static mongoDB DB ;
    public static void main(String [] args){
        DB = new mongoDB("Bolt");
    }
    public List<String> runRanker(List<Document> search_Words){
        RelatedDocuments = search_Words ;
        Ranked_Result = new ArrayList<>();
        Page_Score = new HashMap<>();
        numberOfWordsOnEachPage = new HashMap<>();
        Calculate_Rank();
        Rank_Urls();
        return Ranked_Result ;
    }
    void Calculate_Rank(){
        for(Document word_doc: RelatedDocuments){
            calculate_TF_IDF(word_doc);
        }
    }
    void calculate_TF_IDF(Document word_doc){
        double idf = (Double) word_doc.get("IDF");
        ArrayList<Document> pages = (ArrayList<Document>) word_doc.get("pages");
        Iterator<Document> pages_iterable = pages.iterator();
        while (pages_iterable.hasNext()){
            Document page = pages_iterable.next();
            double tf = (Double) page.get("TF");
            String url = (String) page.get("url");
            double TF_IDF = idf*tf ;
            if (Page_Score.get(url) == null) {
                Page_Score.put(url, TF_IDF);
                numberOfWordsOnEachPage.put(url, 1);
            } else {
                Page_Score.put(url, Page_Score.get(url) + TF_IDF);
                numberOfWordsOnEachPage.put(url, numberOfWordsOnEachPage.get(url) + 1);
            }
        }
    }
    void Rank_Urls(){
        for (String url : Page_Score.keySet()) {
            Ranked_Result.add(url);
        }
    }
}
