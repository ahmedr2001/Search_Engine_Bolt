package com.bolt.Brain.Ranker;

import com.bolt.Brain.DataStructures.Sorting;
import com.bolt.SpringBoot.Page;
import com.bolt.SpringBoot.UrlsService;
import com.bolt.SpringBoot.WordsDocument;
import org.bson.Document;
import com.bolt.Brain.Utils.Stemmer;


import java.util.*;

public class MainRanker {

    List<WordsDocument> RelatedDocuments;
    List<String> Ranked_Result_URLs;
    List<Document> Ranked_Results;
    HashMap<String, Double> Page_Score;
    HashMap<String, Document> Url_Document;
    UrlsService urlsService;
    private HashMap<String, Integer> numberOfWordsOnEachPage;
    List<String > originalWords;

    public MainRanker(List<WordsDocument> QueryResult, UrlsService urlsService , List<String> originalWords) {
        RelatedDocuments = QueryResult;
        Url_Document = new HashMap<>();
        Ranked_Results = new ArrayList<>();
        this.urlsService = urlsService;
        this.originalWords = originalWords;
    }

    public void main(String[] args) {
        runRanker();
    }

    public List<Document> runRanker() {
        if (RelatedDocuments == null) return null;
        Ranked_Result_URLs = new ArrayList<>();
        Page_Score = new HashMap<>();
        Url_Document = new HashMap<String, org.bson.Document>();
        numberOfWordsOnEachPage = new HashMap<>();
        Calculate_Rank();

        Rank_Urls();
        return Ranked_Results;
    }

    void Calculate_Rank() {
        for (WordsDocument word_doc : RelatedDocuments) {
            calculate_TF_IDF(word_doc);
        }
    }

    void calculate_TF_IDF(WordsDocument word_doc) {
        double idf = (Double) word_doc.getIDF();
        ArrayList<Page> pages = (ArrayList<Page>) word_doc.getPages();
        Iterator<Page> pages_iterable = pages.iterator();
        String word = word_doc.getWord();
        while (pages_iterable.hasNext()) {
            Page page = pages_iterable.next();
            double tf = (Double) page.getTF();
            String url = (String) urlsService.findUrl(page.getUrlId());
            if (url == null) continue;
            double rank = urlsService.findRank(url);
            double TF_IDF = idf * tf;
            double score = 0;
            if (Page_Score.get(url) == null) {
//                if (page.getParagraphIndexes().size() > 0 && page.getWordIndexes().size() > 0) {
                String title = insertReturnedData(url, page.getParagraphIndexes().get(0), page.getWordIndexes().get(0));
                score = 5 * Contains(title);
                Page_Score.put(url, TF_IDF*(rank+1)+score);
                numberOfWordsOnEachPage.put(url, 1);
            } else {
                String title = Url_Document.get(url).getString("title");
                score = 5 * Contains(title);
                Page_Score.put(url,TF_IDF*(rank+1));
                Page_Score.put(url, Page_Score.get(url) + TF_IDF * (rank+1)+ score);
                numberOfWordsOnEachPage.put(url, numberOfWordsOnEachPage.get(url) + 1);
            }
        }
    }
    int LongestCommonSubstring(String title , String word){
        int mx = 1;
        for(int i = 0 ; i<=title.length()-word.length();i++){
            for(int j = word.length() ; j>0 ; j--){
                if(title.substring(i, i+j-1).equals(word.substring(0, j-1))) mx  = Math.max(mx ,j) ;
            }
        }
        return mx;
    }
    int Contains (String title){
        int cnt = 0; title = title.toLowerCase();
        System.out.println(title);
        for(String word:originalWords) {
            System.out.println(word);
            if( title.contains(word)) cnt++ ;
        }
        return  cnt ;
    }
    String insertReturnedData(String url, int paragraphIndex, int wordIndex) {
        Document doc = new Document().append("pIdx", paragraphIndex).append("wIdx", wordIndex);
        doc.append("url", url);
        String title = urlsService.getTitle(url);
        doc.append("title", title);
        Url_Document.put(url, doc);
        return title;
    }

    void Rank_Urls() {
        // Ranking by count of words present in the website
//        Page_Score.replaceAll((u, v) -> Page_Score.get(u) * numberOfWordsOnEachPage.get(u));
        Ranked_Result_URLs = Sorting.sortByValue(Page_Score);
        for (String url : Ranked_Result_URLs) {
            Ranked_Results.add(Url_Document.get(url));
            System.out.println(url) ;
            System.out.println(Page_Score.get(url));
        }
        System.out.println("Finished");
    }
    static int compute_Levenshtein_distanceDP(String str1,  String str2)
    {
        // A 2-D matrix to store previously calculated
        // answers of subproblems in order
        // to obtain the final

        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
        {
            for (int j = 0; j <= str2.length(); j++) {

                // If str1 is empty, all characters of
                // str2 are inserted into str1, which is of
                // the only possible method of conversion
                // with minimum operations.
                if (i == 0) {
                    dp[i][j] = j;
                }

                // If str2 is empty, all characters of str1
                // are removed, which is the only possible
                //  method of conversion with minimum
                //  operations.
                else if (j == 0) {
                    dp[i][j] = i;
                }

                else {
                    // find the minimum among three
                    // operations below


                    dp[i][j] = minm_edits(dp[i - 1][j - 1]
                                    + NumOfReplacement(str1.charAt(i - 1),str2.charAt(j - 1)), // replace
                            dp[i - 1][j] + 1, // delete
                            dp[i][j - 1] + 1); // insert
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }
    static int NumOfReplacement(char c1, char c2)
    {
        return c1 == c2 ? 0 : 1;
    }


    static int minm_edits(int... nums)
    {

        return Arrays.stream(nums).min().orElse(
                Integer.MAX_VALUE);
    }

}
