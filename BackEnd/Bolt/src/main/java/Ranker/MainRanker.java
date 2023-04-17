package Ranker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DB.mongoDB;


public class MainRanker {

    List<String> Search_Words ;
    List<String> Ranked_Result;
    HashMap<String , Double> Page_Score;

    static mongoDB DB ;
    public static void main(String [] args){
        DB = new mongoDB("Bolt");
        DB.getPagesWithWord("pide");
    }
    public List<String> runRanker(List<String> search_Words){
        Search_Words = search_Words ;
        Ranked_Result = new ArrayList<>();
        return Ranked_Result ;

    }
    void Calculate_Rank(){

        for(String word: Search_Words){



        }
    }
}
